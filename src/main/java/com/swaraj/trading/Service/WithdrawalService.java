package com.swaraj.trading.Service;

import com.swaraj.trading.Model.User;
import com.swaraj.trading.Model.Withdrawal;

import java.util.List;

public interface WithdrawalService {
    Withdrawal requestWithdrawal(Long amount , User user);

    Withdrawal proceedWithdrawal(Long withdrawalId , boolean accept) throws Exception;

    List<Withdrawal> getUserWithdrawalHistory(User user);

    List<Withdrawal> getAllWithdrawalRequest();

}
