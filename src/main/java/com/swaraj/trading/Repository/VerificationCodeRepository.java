package com.swaraj.trading.Repository;

import com.swaraj.trading.Model.User;
import com.swaraj.trading.Model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode , Long> {
    public VerificationCode findByUserId(Long userId);
}
