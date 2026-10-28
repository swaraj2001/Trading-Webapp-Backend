package com.swaraj.trading.Repository;

import com.swaraj.trading.Model.WatchList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchListRepository extends JpaRepository<WatchList , Long> {
    WatchList findByUserId(Long userId);
}
