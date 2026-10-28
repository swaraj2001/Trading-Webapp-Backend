package com.swaraj.trading.Controller;

import com.swaraj.trading.Config.JwtProvider;
import com.swaraj.trading.Model.TwoFactorOTP;
import com.swaraj.trading.Model.User;
import com.swaraj.trading.Repository.UserRepository;
import com.swaraj.trading.Response.AuthResponse;
import com.swaraj.trading.Service.CustomDetailService;
import com.swaraj.trading.Service.EmailService;
import com.swaraj.trading.Service.TwoFactorOTPService;
import com.swaraj.trading.Service.WatchListService;
import com.swaraj.trading.Utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomDetailService customDetailService;

    @Autowired
    private TwoFactorOTPService twoFactorOTPService;
    @Autowired
    private WatchListService watchListService;

    @Autowired
    private EmailService emailService;


    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {


        User isEmailExist = userRepository.findByEmail(user.getEmail());

        if (isEmailExist != null){
            throw  new Exception("Email is already used with another account");
        }
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setFullName(user.getFullName());

        User savedUser = userRepository.save(newUser);

        watchListService.createWatchList(savedUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword()
                );
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = JwtProvider.generateToken(auth);
        AuthResponse authResponse = new AuthResponse();

        authResponse.setJwt(jwt);
        authResponse.setStatus(true);
        authResponse.setMessage("Registered Successfully");
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception {

        String username = user.getEmail();
        String password = user.getPassword();

        Authentication auth = authenticate(username,password);
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = JwtProvider.generateToken(auth);

        User authUser = userRepository.findByEmail(username);
        if(user.getTwoFactorAuth().isEnabled()){
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("Two factor auth is enabled");
            authResponse.setTwoFactorAuthEnabled(true);
            String otp = OtpUtils.genrateOtp();
            TwoFactorOTP oldTwoFactorOTP = twoFactorOTPService.findByUser(authUser.getId());
            if (oldTwoFactorOTP != null){
                twoFactorOTPService.deleteTwoFactorOTP(oldTwoFactorOTP);
            }
            TwoFactorOTP newTwoFactorOTP = twoFactorOTPService.createTwoFactorOTP(
                    authUser, otp,jwt);

            emailService.sendVerificationOTPOnEmail(username,otp);

            authResponse.setSession(newTwoFactorOTP.getId());
            return new ResponseEntity<>(authResponse , HttpStatus.ACCEPTED);

        }

        AuthResponse authResponse = new AuthResponse();

        authResponse.setJwt(jwt);
        authResponse.setStatus(true);
        authResponse.setMessage("Login Successfully");
        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customDetailService.loadUserByUsername(username);
        if(userDetails == null){
            throw new BadCredentialsException("Invalid Username");
        }
        if (!password.equals(userDetails.getPassword())){
            throw new BadCredentialsException("Invalid Password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails , password ,userDetails.getAuthorities());
    }

    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySignInOTP(@PathVariable String otp , @RequestParam String id) throws Exception {
        TwoFactorOTP twoFactorOTP = twoFactorOTPService.findById(id);
        if (twoFactorOTPService.verifyTwoFactorOTP(twoFactorOTP , otp)){
            AuthResponse res = new AuthResponse();
            res.setMessage("Two factor authentication is verified");
            res.setTwoFactorAuthEnabled(true);
            res.setJwt(twoFactorOTP.getJwt());
            return new ResponseEntity<>(res , HttpStatus.OK);
        }

       throw new Exception("Invalid OTP");
    }

}
