package com.swaraj.trading.Service;

import com.swaraj.trading.Domain.VerificationType;
import com.swaraj.trading.Model.User;
import com.swaraj.trading.Model.VerificationCode;
import com.swaraj.trading.Repository.VerificationCodeRepository;

public interface VerificationCodeService  {
    VerificationCode sendVerificationOtp(User user , VerificationType verificationType);
    VerificationCode getVerificationCodeById(Long id) throws Exception;
    VerificationCode getVerificationCodeByUser(Long userId);
    void deleteVerificationCode(VerificationCode verificationCode);
}
