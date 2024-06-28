package com.jaideep.springsecurityclient.controller;

import com.jaideep.springsecurityclient.entity.PasswordResetToken;
import com.jaideep.springsecurityclient.entity.User;
import com.jaideep.springsecurityclient.entity.VerificationToken;
import com.jaideep.springsecurityclient.event.RegistrationCompleteEvent;
import com.jaideep.springsecurityclient.model.PasswordModel;
import com.jaideep.springsecurityclient.model.UserModel;
import com.jaideep.springsecurityclient.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    @PostMapping("/register")
    public String registerUser(@RequestBody UserModel userModel, final HttpServletRequest httpRequest) {
        User user = userService.registerUser(userModel);
        publisher.publishEvent(new RegistrationCompleteEvent(
                user,
                applicationUrl(httpRequest)
        ));
        return "Success";
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")) {
            return "User verified successfully";
        }
        return "Bad user";
    }

    @GetMapping("/resendVerificationToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest httpServletRequest) {
        VerificationToken verificationToken = userService.generateNewVerificationToken(oldToken);
        userService.resendVerificationTokenMail(applicationUrl(httpServletRequest), verificationToken);
        return "Verification link sent successfully";
    }

    @GetMapping("/resetPassword")
    public String resetPassword(@RequestParam("email") String email, HttpServletRequest httpServletRequest) {
        PasswordResetToken passwordResetToken = userService.createPasswordResetTokenForUser(email);
        if (passwordResetToken != null) {
            userService.sendPasswordResetTokenMail(applicationUrl(httpServletRequest), passwordResetToken.getToken());
            return "Password reset link sent successfully";
        }
        return "Failed to send link";
    }

    @PostMapping("/savePassword")
    public String savePassword(@RequestBody PasswordModel passwordModel, @RequestParam("token") String token) {
        return userService.validatePasswordResetToken(token, passwordModel);
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestBody PasswordModel passwordModel) {
        if (userService.checkIfPasswordIsValid(passwordModel)) {
            userService.changePassword(passwordModel);
            return "Password saved successfully";
        }
        return "Incorrect password";
    }

    private String applicationUrl(HttpServletRequest httpRequest) {
        return "http://"
                +httpRequest.getServerName()
                + ":"
                + httpRequest.getServerPort()
                +httpRequest.getContextPath()
                +"/v1/api";
    }
}
