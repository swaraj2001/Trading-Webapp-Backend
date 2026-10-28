package com.swaraj.trading.Service;

import com.swaraj.trading.Model.TwoFactorOTP;
import com.swaraj.trading.Model.User;

public interface TwoFactorOTPService {
    TwoFactorOTP createTwoFactorOTP(User user , String otp , String jwt);

    TwoFactorOTP findByUser(Long userId);

    TwoFactorOTP findById(String id);

    boolean verifyTwoFactorOTP(TwoFactorOTP twoFactorOTP , String otp);

    void deleteTwoFactorOTP(TwoFactorOTP twoFactorOTP);
}
