package com.swaraj.trading.Service;

import com.swaraj.trading.Domain.OrderStatus;
import com.swaraj.trading.Domain.OrderType;
import com.swaraj.trading.Model.*;
import com.swaraj.trading.Repository.OrderItemRepository;
import com.swaraj.trading.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private WalletService walletService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private AssetService assetService;

    @Override
    public Order createOrder(User user, OrderItem orderItem, OrderType orderType) {
        double price = orderItem.getCoin().getCurrentPrice()*orderItem.getQuantity();
        Order order = new Order();
        order.setOrderItem(orderItem);
        order.setOrderType(orderType);
        order.setUser(user);
        order.setTimestamp(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setPrice(BigDecimal.valueOf(price));
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) throws Exception {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new Exception("Order not found...."));
    }

    @Override
    public List<Order> getAllOrdersOfUser(Long uerId, OrderType orderType, String assetSymbol) {
        return orderRepository.findByUserId(uerId);
    }

    private OrderItem createOrderItem(Coin coin, double quantity, double buyPrice , double sellPrice){
        OrderItem orderItem = new OrderItem();
        orderItem.setCoin(coin);
        orderItem.setBuyPrice(buyPrice);
        orderItem.setSellPrice(sellPrice);
        orderItem.setQuantity(quantity);
        return orderItemRepository.save(orderItem);
    }

    @Transactional
    public Order buyAsset(Coin coin , double quantity , User user) throws Exception {
        if (quantity <= 0){
            throw new Exception("Qauntity should be greater tha 0");
        }
        double buyPrice = coin.getCurrentPrice();

        OrderItem orderItem = createOrderItem(coin ,quantity , buyPrice , 0);
        Order order = createOrder(user , orderItem,OrderType.BUY);
        orderItem.setOrder(order);

        walletService.payOrderPayment(order , user);

        order.setStatus(OrderStatus.SUCCESS);
        order.setOrderType(OrderType.BUY);
        Order saveOrder = orderRepository.save(order);

//      Create  Asset
        Asset oldAsset = assetService.getAssetByUserIdAndCoinId(
                order.getUser().getId(),
                order.getOrderItem().getCoin().getId());
        if (oldAsset == null){
            assetService.createAsset(user , orderItem.getCoin() , orderItem.getQuantity());

        }else{
            assetService.updateAsset(oldAsset.getId() , quantity);
        }

        return saveOrder;
    }
    @Transactional
    public Order sellAsset(Coin coin , double quantity , User user) throws Exception {
        if (quantity <= 0){
            throw new Exception("Qauntity should be greater tha 0");
        }
        double sellPrice = coin.getCurrentPrice();
        Asset assetToSell = assetService.getAssetByUserIdAndCoinId(user.getId() ,coin.getId());
        double buyPrice = assetToSell.getBuyPrice();
        if (assetToSell != null){

            OrderItem orderItem = createOrderItem(coin ,quantity , buyPrice , sellPrice);
            Order order = createOrder(user , orderItem,OrderType.SELL);
            orderItem.setOrder(order);
            if (assetToSell.getQuantity() >= quantity){

                order.setStatus(OrderStatus.SUCCESS);
                order.setOrderType(OrderType.SELL);
                Order saveOrder = orderRepository.save(order);
                walletService.payOrderPayment(order , user);

                Asset updatedAsset = assetService.updateAsset(assetToSell.getId() , -quantity);
                if(updatedAsset.getQuantity()*coin.getCurrentPrice() <= 1){
                    assetService.deleteAsset(updatedAsset.getId());
                }
                return saveOrder;
        }
            throw new Exception("Insufficient quantity to sell....");
        }

        throw new Exception("Asset Not Found....");
    }


    @Override
    @Transactional
    public Order processOrder(Coin coin, double quantity, OrderType orderType, User user) throws Exception {
        if (orderType.equals(OrderType.BUY)){
            return buyAsset(coin , quantity ,user);
        } else if (orderType.equals(OrderType.SELL)) {
            return sellAsset(coin , quantity , user);
        }
        throw  new Exception("Invalid Order Type....");
    }
}
