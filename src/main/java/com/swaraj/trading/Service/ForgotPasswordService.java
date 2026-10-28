package com.swaraj.trading.Service;

import com.swaraj.trading.Domain.VerificationType;
import com.swaraj.trading.Model.ForgotPasswordToken;
import com.swaraj.trading.Model.User;

public interface ForgotPasswordService {
    ForgotPasswordToken createToken(User user,
                                    String id, String otp,
                                    VerificationType verificationType,
                                    String sendTo);

    ForgotPasswordToken findById(String id);

    ForgotPasswordToken findByUser(Long userId);

    void deleteToken(ForgotPasswordToken token);

}
