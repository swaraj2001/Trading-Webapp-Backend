package com.swaraj.trading.Service;

import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;
import com.swaraj.trading.Domain.PaymentMethod;
import com.swaraj.trading.Model.PaymentOrder;
import com.swaraj.trading.Model.User;
import com.swaraj.trading.Response.PaymentResponse;

public interface PaymentOrderService {

    PaymentOrder createPayMentOrder(User user , Long amount , PaymentMethod paymentMethod);

    PaymentOrder getPaymentOrderById(Long Id) throws Exception;

    Boolean ProceedPaymentOrder(PaymentOrder paymentOrder , String paymentId) throws RazorpayException;

    PaymentResponse createRazorpayPaymentLink(User user , Long amount , Long orderId) throws RazorpayException;

    PaymentResponse createStripePaymentLink(User user, Long amount, Long orderId) throws StripeException;
}
