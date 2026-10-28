package com.swaraj.trading.Service;

import com.swaraj.trading.Model.Coin;
import com.swaraj.trading.Model.User;
import com.swaraj.trading.Model.WatchList;
import com.swaraj.trading.Repository.WatchListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WatchListServiceImpl implements WatchListService{

    @Autowired
    private WatchListRepository watchListRepository;

    @Override
    public WatchList getUserWatchList(Long userId) throws Exception {
        WatchList watchList = watchListRepository.findByUserId(userId);
        if (watchList == null){
            throw new Exception("WatchList Not Found....");
        }
        return watchList;
    }

    @Override
    public WatchList createWatchList(User user) {
        WatchList watchList = new WatchList();
        watchList.setUser(user);
        return watchListRepository.save(watchList);
    }

    @Override
    public WatchList findById(Long id) throws Exception {
        Optional<WatchList> optionalWatchList = watchListRepository.findById(id);
        if (optionalWatchList.isEmpty()){
            throw new Exception("WatchList Not Found....");
        }
        return optionalWatchList.get();
    }

    @Override
    public Coin addItemToWatchList(Coin coin, User user) throws Exception {
        WatchList watchList = getUserWatchList(user.getId());

        if (watchList.getCoins().contains(coin)){
            watchList.getCoins().remove(coin);
        }else {
            watchList.getCoins().add(coin);
        }
        watchListRepository.save(watchList);

        return coin;
    }
}
