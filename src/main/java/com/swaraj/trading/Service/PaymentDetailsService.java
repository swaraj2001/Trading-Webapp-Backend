package com.swaraj.trading.Service;

import com.swaraj.trading.Model.PaymentDetails;
import com.swaraj.trading.Model.User;

public interface PaymentDetailsService {
    public PaymentDetails addPaymentDetails(String accountNumber, String accountHolderName,
                                            String ifsc, String bankName, User user);

    public PaymentDetails getUserPaymentDetails(User user);

}
