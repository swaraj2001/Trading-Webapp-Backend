package com.swaraj.trading.Repository;

import com.swaraj.trading.Model.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WithdrawalRepository extends JpaRepository<Withdrawal , Long> {
    List<Withdrawal> findByUserId(Long userId);
}
