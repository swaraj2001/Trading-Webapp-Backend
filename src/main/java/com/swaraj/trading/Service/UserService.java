package com.swaraj.trading.Service;

import com.swaraj.trading.Domain.VerificationType;
import com.swaraj.trading.Model.User;

public interface UserService {
    public User findUserByJwt(String jwt) throws Exception;
    public User findUserByEmail(String email) throws Exception;
    public User findUserById(Long userId) throws Exception;

    public User enableTwoFactorAuthentication(VerificationType verificationType,String sendTo, User user );
    public User updatePassword(User user , String newPassword);
}
