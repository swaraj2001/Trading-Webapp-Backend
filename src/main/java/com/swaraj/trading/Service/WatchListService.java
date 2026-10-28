package com.swaraj.trading.Service;

import com.swaraj.trading.Model.Coin;
import com.swaraj.trading.Model.User;
import com.swaraj.trading.Model.WatchList;

public interface WatchListService {
    WatchList getUserWatchList(Long userId) throws Exception;

    WatchList createWatchList(User user);

    WatchList findById(Long id) throws Exception;

    Coin addItemToWatchList(Coin coin , User user) throws Exception;



}
