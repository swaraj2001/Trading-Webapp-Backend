package com.swaraj.trading.Controller;

import com.swaraj.trading.Model.Coin;
import com.swaraj.trading.Model.User;
import com.swaraj.trading.Model.WatchList;
import com.swaraj.trading.Service.CoinService;
import com.swaraj.trading.Service.UserService;
import com.swaraj.trading.Service.WalletService;
import com.swaraj.trading.Service.WatchListService;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
public class WatchListController {
    @Autowired
    private WatchListService watchListService;
    @Autowired
    private UserService userService;
    @Autowired
    private CoinService coinService;

    @GetMapping("/user")
    public ResponseEntity<WatchList> getUserWatchList(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserByJwt(jwt);
        WatchList watchList = watchListService.getUserWatchList(user.getId());

        return ResponseEntity.ok(watchList);


    }

    @PostMapping("/create")
    public ResponseEntity<WatchList> createWatchList(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserByJwt(jwt);
        WatchList createdWatchList = watchListService.createWatchList(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdWatchList);


    }

    @GetMapping("/{watchListId}")
    public ResponseEntity<WatchList> getWatchListById(
            @PathVariable Long watchListId
    ) throws Exception {
        WatchList watchList = watchListService.findById(watchListId);
        return ResponseEntity.ok(watchList);
    }

    @PatchMapping("/add/coin/{coinId}")
    public ResponseEntity<Coin> addItemToWatchList(
            @RequestHeader("Authorization") String jwt,
            @PathVariable String coinId
    ) throws Exception {
        User user = userService.findUserByJwt(jwt);
        Coin coin = coinService.findById(coinId);

        Coin addedCoin = watchListService.addItemToWatchList(coin , user);

        return ResponseEntity.ok(addedCoin);
    }


}
