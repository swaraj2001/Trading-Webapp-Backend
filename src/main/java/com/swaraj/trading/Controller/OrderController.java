package com.swaraj.trading.Controller;

import com.swaraj.trading.Domain.OrderType;
import com.swaraj.trading.Model.Coin;
import com.swaraj.trading.Model.Order;
import com.swaraj.trading.Model.User;
import com.swaraj.trading.Request.CreateOrderRequest;
import com.swaraj.trading.Service.CoinService;
import com.swaraj.trading.Service.OrderService;
import com.swaraj.trading.Service.UserService;
import com.swaraj.trading.Service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/orders")
@RestController
public class OrderController {
    @Autowired
    private WalletService walletService;
    @Autowired
    private UserService userService;
    @Autowired
    private CoinService coinService;
    @Autowired
    private OrderService orderService;

//    private WalletServiceetTrasactionService walletTrasactionService;
//

    @PostMapping("/pay")
    public ResponseEntity<Order> payOrderPayment(
            @RequestHeader("Authorization") String jwt ,
            @RequestBody CreateOrderRequest req) throws Exception {
        User user = userService.findUserByJwt(jwt);
        Coin coin = coinService.findById(req.getCoinId());

        Order order = orderService.processOrder(coin,req.getQuantity(),req.getOrderType() , user);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(
            @RequestHeader("Authorization") String jwtToken ,
            @PathVariable Long orderId
    ) throws Exception {
        if (jwtToken == null){
            throw new Exception("Token is missing");

        }
        User user = userService.findUserByJwt(jwtToken);
        Order order = orderService.getOrderById(orderId);
        if (order.getUser().getId().equals(user.getId())){
            return ResponseEntity.ok(order);
        }else {
            throw new Exception("You don't have access....");
        }

    }

    @GetMapping()
    public ResponseEntity<List<Order>> getAllOrdersForUser(
            @RequestHeader("Authorization") String jwtToken,
            @RequestParam(required = false) OrderType order_type,
            @RequestParam(required = false) String asset_symbol
    ) throws Exception {

        Long userId = userService.findUserByJwt(jwtToken).getId();
        List<Order> userOrders = orderService.getAllOrdersOfUser(userId ,order_type, asset_symbol);

        return ResponseEntity.ok(userOrders);
    }

}
