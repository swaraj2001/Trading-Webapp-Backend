package com.swaraj.trading.Service;

import com.swaraj.trading.Model.Order;
import com.swaraj.trading.Model.User;
import com.swaraj.trading.Model.Wallet;

public interface WalletService {
    Wallet getUserWallet(User user);
    Wallet addBalance(Wallet wallet , Long money);
    Wallet findWalletByID(Long id) throws Exception;
    Wallet walletToWalletTransfer(User sender , Wallet receiverWallet ,Long amount) throws Exception;
    Wallet payOrderPayment(Order order , User user) throws Exception;
}
