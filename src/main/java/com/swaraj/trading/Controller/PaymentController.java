package com.swaraj.trading.Controller;

import com.swaraj.trading.Domain.PaymentMethod;
import com.swaraj.trading.Model.PaymentOrder;
import com.swaraj.trading.Model.User;
import com.swaraj.trading.Response.PaymentResponse;
import com.swaraj.trading.Service.PaymentOrderService;
import com.swaraj.trading.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController

public class PaymentController {
    @Autowired
    private UserService userService ;

    @Autowired
    private PaymentOrderService paymentOrderService;

    @PostMapping("/api/payment/{paymentMethod}/amount/{amount}")
    public ResponseEntity<PaymentResponse> paymentHandler(
            @PathVariable PaymentMethod paymentMethod ,
            @PathVariable Long amount,
            @RequestHeader ("Authorization") String jwt
            ) throws Exception {
        User user = userService.findUserByJwt(jwt);
        PaymentResponse paymentResponse ;
        PaymentOrder order = paymentOrderService.createPayMentOrder(user , amount , paymentMethod);

        if (paymentMethod.equals(PaymentMethod.RAZORPAY)){
            paymentResponse = paymentOrderService.createRazorpayPaymentLink(user , amount , order.getId());
        }else {
            paymentResponse = paymentOrderService.createStripePaymentLink(user , amount ,order.getId());

        }
        return new ResponseEntity<>(paymentResponse , HttpStatus.CREATED);
    }
}
