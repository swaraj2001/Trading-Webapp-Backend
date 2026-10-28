package com.swaraj.trading.Controller;

import com.swaraj.trading.Domain.VerificationType;
import com.swaraj.trading.Request.ForgotPasswordTokenRequest;
import com.swaraj.trading.Model.ForgotPasswordToken;
import com.swaraj.trading.Model.User;
import com.swaraj.trading.Model.VerificationCode;
import com.swaraj.trading.Request.ResetPasswordRequest;
import com.swaraj.trading.Response.ApiResponse;
import com.swaraj.trading.Response.AuthResponse;
import com.swaraj.trading.Service.EmailService;
import com.swaraj.trading.Service.ForgotPasswordService;
import com.swaraj.trading.Service.UserService;
import com.swaraj.trading.Service.VerificationCodeService;
import com.swaraj.trading.Utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwt(jwt);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PostMapping("/api/users/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(@RequestHeader("Authorization") String jwt , @PathVariable VerificationType verificationType ) throws Exception {
        User user = userService.findUserByJwt(jwt);
        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());
        if (verificationCode == null){
            verificationCode = verificationCodeService.sendVerificationOtp(user,verificationType);
        }
        if (verificationType.equals(VerificationType.EMAIL)){
            emailService.sendVerificationOTPOnEmail(user.getEmail(),verificationCode.getOtp());

        }
        return new ResponseEntity<>("Verification otp sent successfully", HttpStatus.OK);
    }

    @PatchMapping("/api/users/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthentication(@PathVariable String otp ,@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwt(jwt);
        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());
        String sendTo = verificationCode.getVerificationType().equals(VerificationType.EMAIL)
                ? verificationCode.getEmail() :verificationCode.getMobile();

        boolean isVerified = verificationCode.getOtp().equals(otp);
        if (isVerified){
            User updatedUser = userService.enableTwoFactorAuthentication(verificationCode.getVerificationType(),sendTo,user);
            verificationCodeService.deleteVerificationCode(verificationCode);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }
        throw new Exception("Wrong Otp");
    }

    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgotPasswordOtp(@RequestBody ForgotPasswordTokenRequest req) throws Exception {
        User user = userService.findUserByEmail(req.getSendTo());
        String otp = OtpUtils.genrateOtp();
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();
        ForgotPasswordToken token = forgotPasswordService.findByUser(user.getId());
        if (token == null)
        {
            token = forgotPasswordService.createToken(user,id , otp , req.getVerificationType(), req.getSendTo() );
        }
        if (req.getVerificationType().equals(VerificationType.EMAIL)){
            emailService.sendVerificationOTPOnEmail(user.getEmail(),token.getOtp());
        }
        AuthResponse response = new AuthResponse();
        response.setSession(token.getId());
        response.setMessage("Password reset otp sent successfully");
        return new ResponseEntity<>(response , HttpStatus.OK);
    }

    @PatchMapping("/api/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String id
            , @RequestBody ResetPasswordRequest req
            , @RequestHeader("Authorization") String jwt) throws Exception {

        ForgotPasswordToken token = forgotPasswordService.findById(id);
        boolean isVerified = token.getOtp().equals(req.getOtp());

        if (isVerified){
            userService.updatePassword(token.getUser() , req.getPassword());
            ApiResponse response = new ApiResponse();
            response.setMessage("Password Updated Successfully");
            return new ResponseEntity<>(response , HttpStatus.ACCEPTED);
        }
        throw new Exception("Wrong OTP");
    }


}
