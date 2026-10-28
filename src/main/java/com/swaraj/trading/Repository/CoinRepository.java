package com.swaraj.trading.Repository;

import com.swaraj.trading.Model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository extends JpaRepository<Coin , String> {

}
