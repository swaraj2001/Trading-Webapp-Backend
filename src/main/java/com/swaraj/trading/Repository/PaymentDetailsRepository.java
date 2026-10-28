package com.swaraj.trading.Repository;

import com.swaraj.trading.Model.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDetailsRepository extends JpaRepository<PaymentDetails , Long> {
    PaymentDetails findByUserId(Long userId);
}
