package com.swaraj.trading.Controller;

import com.swaraj.trading.Model.*;
import com.swaraj.trading.Service.OrderService;
import com.swaraj.trading.Service.PaymentOrderService;
import com.swaraj.trading.Service.UserService;
import com.swaraj.trading.Service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController

public class WalletController {

    @Autowired
    private WalletService walletService;
    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentOrderService paymentOrderService;




    @GetMapping("/api/wallet")
    ResponseEntity<Wallet> getUserWallet(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwt(jwt);

        Wallet wallet = walletService.getUserWallet(user);

        return new ResponseEntity<>(wallet , HttpStatus.ACCEPTED);
    }

    @PutMapping("/api/wallet/{walletId}/transfer")
    ResponseEntity<Wallet> walletToWalletTransfer(@RequestHeader("Authorization") String jwt ,
                                                  @PathVariable Long walletId,
                                                  @RequestBody WalletTransaction req) throws Exception {

        User sendUser = userService.findUserByJwt(jwt);

        Wallet receiverWallet = walletService.findWalletByID(walletId);

        Wallet wallet = walletService.walletToWalletTransfer(sendUser,receiverWallet,req.getAmount());

        return new ResponseEntity<>(wallet , HttpStatus.ACCEPTED);

    }

    @PutMapping("/api/wallet/order/{orderId}/pay")
    ResponseEntity<Wallet> payOrderPayment(@RequestHeader("Authorization") String jwt ,
                                                  @PathVariable Long orderId) throws Exception {

        User user = userService.findUserByJwt(jwt);

        Order order = orderService.getOrderById(orderId);

        Wallet wallet = walletService.payOrderPayment(order , user);

        return new ResponseEntity<>(wallet , HttpStatus.ACCEPTED);

    }


    @PutMapping("/api/wallet/deposit")
    ResponseEntity<Wallet> addBalanceToWallet (
            @RequestHeader("Authorization") String jwt ,
            @RequestParam(name = "order_id") Long orderId ,
            @RequestParam(name = "payment_id") String paymentId ) throws Exception {

        User user = userService.findUserByJwt(jwt);

        Wallet wallet = walletService.getUserWallet(user);

        PaymentOrder order = paymentOrderService.getPaymentOrderById(orderId);

        Boolean status = paymentOrderService.ProceedPaymentOrder(order , paymentId);

        if (wallet.getBalance() == null){
            wallet.setBalance(BigDecimal.valueOf(0));
        }

        if (status){
            wallet = walletService.addBalance(wallet , order.getAmount());

        }

        return new ResponseEntity<>(wallet , HttpStatus.ACCEPTED);

    }

}
