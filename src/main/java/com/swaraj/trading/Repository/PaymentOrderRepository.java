package com.swaraj.trading.Repository;

import com.swaraj.trading.Model.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOrderRepository extends JpaRepository<PaymentOrder , Long> {

}
