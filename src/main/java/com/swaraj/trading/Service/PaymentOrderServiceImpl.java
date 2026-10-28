package com.swaraj.trading.Service;

import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.swaraj.trading.Domain.PaymentMethod;
import com.swaraj.trading.Domain.PaymentOrderStatus;
import com.swaraj.trading.Model.PaymentOrder;
import com.swaraj.trading.Model.User;
import com.swaraj.trading.Repository.PaymentOrderRepository;
import com.swaraj.trading.Response.PaymentResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentOrderServiceImpl implements PaymentOrderService{

    @Autowired
    private PaymentOrderRepository paymentOrderRepository;

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecretKey;

    @Override
    public PaymentOrder createPayMentOrder(User user, Long amount, PaymentMethod paymentMethod) {
        PaymentOrder paymentOrder = new PaymentOrder();
        paymentOrder.setUser(user);
        paymentOrder.setPaymentMethod(paymentMethod);
        paymentOrder.setAmount(amount);
        paymentOrder.setPaymentOrderStatus(PaymentOrderStatus.PENDING);
        return paymentOrderRepository.save(paymentOrder);
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long Id) throws Exception {
        return paymentOrderRepository.findById(Id).orElseThrow(
                ()-> new Exception(" Payment order not found...."));
    }

    @Override
    public Boolean ProceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws RazorpayException {
       if (paymentOrder.getPaymentOrderStatus() == null){
           paymentOrder.setPaymentOrderStatus(PaymentOrderStatus.PENDING);
       }
        if(paymentOrder.getPaymentOrderStatus().equals(PaymentOrderStatus.PENDING)){
            if (paymentOrder.getPaymentMethod().equals(PaymentMethod.RAZORPAY)){
                RazorpayClient razorpayClient = new RazorpayClient(apiKey,apiSecretKey);
                Payment payment = razorpayClient.payments.fetch(paymentId);

                Integer amount = payment.get("amount");
                String status = payment.get("status");

                if (status.equals("captured")){
                    paymentOrder.setPaymentOrderStatus(PaymentOrderStatus.SUCCESS);
                    return true;
                }
                paymentOrder.setPaymentOrderStatus(PaymentOrderStatus.FAILED);
                paymentOrderRepository.save(paymentOrder);
                return false;


            }

            paymentOrder.setPaymentOrderStatus(PaymentOrderStatus.SUCCESS);
            paymentOrderRepository.save(paymentOrder);
            return true;
        }
        return false;
    }

    @Override
    public PaymentResponse createRazorpayPaymentLink(User user, Long amount , Long orderId) throws RazorpayException {
        Long Amount = amount * 100;
         try {
             RazorpayClient razorpay = new RazorpayClient(apiKey , apiSecretKey);

             // JSON Object for Payment Link parameters
             JSONObject paymentLinkRequest = new JSONObject();
             paymentLinkRequest.put("amount" , amount);
             paymentLinkRequest.put("currency" , "INR");

             // JSON Object for Customer Details
             JSONObject customer = new JSONObject();
             customer.put("name", user.getFullName());
             customer.put("email" , user.getEmail());

             paymentLinkRequest.put("customer" , customer);

             // JSON object with notification settings
             JSONObject notify = new JSONObject();
             notify.put("email" , true);
             paymentLinkRequest.put("notify" , notify);

             paymentLinkRequest.put("reminder_enable", true);

             paymentLinkRequest.put("callback_url" , "http://localhost:5450/wallet?order_id="+orderId);
             paymentLinkRequest.put("callback_method" , "get");

             // Creating Payment Link
             PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);

             String paymentLinkId = payment.get("id");
             String paymentLinkUrl = payment.get("short_url");

             PaymentResponse res = new PaymentResponse();
             res.setPayment_url(paymentLinkUrl);

             return res;


         }
         catch (RazorpayException e){
             System.out.println("Error while creating payment link : " + e.getMessage());
             throw new RazorpayException(e.getMessage());
         }

    }

    @Override
    public PaymentResponse createStripePaymentLink(User user, Long amount, Long orderId) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5450/wallet?order_id="+orderId)
                .setCancelUrl("http://localhost:5450/payment/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(amount*100)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Top up wallet")
                                        .build()
                                ).build()
                        ).build()
                ).build();

        Session session = Session.create(params);
        System.out.println("Session__________" + session);

        PaymentResponse res = new PaymentResponse();
        res.setPayment_url(session.getUrl());

        return res;
    }
}
