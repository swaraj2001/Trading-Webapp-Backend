package com.swaraj.trading.Repository;

import com.swaraj.trading.Model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet , Long> {
    Wallet findByUserId(Long userId);
}
