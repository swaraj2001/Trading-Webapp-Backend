package com.swaraj.trading.Repository;

import com.swaraj.trading.Model.ForgotPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForgotPasswordRepository extends JpaRepository<ForgotPasswordToken , String> {
    ForgotPasswordToken findByUserId(Long userId);
}
