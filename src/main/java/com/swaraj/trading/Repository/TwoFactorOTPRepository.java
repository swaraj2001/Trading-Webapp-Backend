package com.swaraj.trading.Repository;

import com.swaraj.trading.Model.TwoFactorOTP;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TwoFactorOTPRepository extends JpaRepository<TwoFactorOTP , String > {
    TwoFactorOTP findByUserId(Long userId);
}
