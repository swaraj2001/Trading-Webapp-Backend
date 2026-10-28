package com.swaraj.trading.Service;

import com.swaraj.trading.Domain.OrderType;
import com.swaraj.trading.Model.Coin;
import com.swaraj.trading.Model.Order;
import com.swaraj.trading.Model.OrderItem;
import com.swaraj.trading.Model.User;

import java.util.List;

public interface OrderService {
    Order createOrder(User user , OrderItem orderItem , OrderType orderType);

    Order getOrderById(Long orderId) throws Exception;

    List<Order> getAllOrdersOfUser(Long uerId ,OrderType orderType , String assetSymbol);

    Order processOrder(Coin coin , double quantity , OrderType orderType , User user) throws Exception;



}
