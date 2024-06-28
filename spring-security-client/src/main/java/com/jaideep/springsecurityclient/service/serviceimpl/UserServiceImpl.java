package com.jaideep.springsecurityclient.service.serviceimpl;

import com.jaideep.springsecurityclient.entity.PasswordResetToken;
import com.jaideep.springsecurityclient.entity.User;
import com.jaideep.springsecurityclient.entity.VerificationToken;
import com.jaideep.springsecurityclient.model.PasswordModel;
import com.jaideep.springsecurityclient.model.UserModel;
import com.jaideep.springsecurityclient.repository.PasswordTokenRepository;
import com.jaideep.springsecurityclient.repository.UserRepository;
import com.jaideep.springsecurityclient.repository.VerificationTokenRepository;
import com.jaideep.springsecurityclient.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordTokenRepository passwordTokenRepository;
    @Override
    public User registerUser(UserModel userModel) {
        User user = new User();
        user.setEmail(userModel.getEmail());
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        user.setRole("USER");
        user = userRepository.save(user);
        return user;
    }

    @Override
    public void saveVerificationTokenForUser(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            return "invalid";
        }
        else if (verificationToken.getToken().equals(token)) {
            User user = verificationToken.getUser();
            user.setEnabled(true);
            userRepository.save(user);
            return "valid";
        }
        else if (verificationToken.getExpirationTime().getTime() < Calendar.getInstance().getTime().getTime()) {
            verificationTokenRepository.delete(verificationToken);
            return "expired";
        }
        return "invalid";
    }

    @Override
    public void resendVerificationTokenMail(String applicationUrl, VerificationToken verificationToken) {
        String url = applicationUrl
                + "/verifyRegistration?token="
                +verificationToken.getToken();

        log.info("Click the link to verify your account: {}", url);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public PasswordResetToken createPasswordResetTokenForUser(String email) {
        User user = this.findUserByEmail(email);
        if (user != null) {
            String token = UUID.randomUUID().toString();
            PasswordResetToken passwordResetToken= new PasswordResetToken(user, token);
            return passwordTokenRepository.save(passwordResetToken);
        }
        return null;
    }

    @Override
    public void sendPasswordResetTokenMail(String applicationUrl, String token) {
        String url = applicationUrl
                + "/savePassword?token="
                + token;

        log.info("Click the link to reset password for your account: {}", url);
    }

    @Override
    public String validatePasswordResetToken(String token, PasswordModel passwordModel) {
        PasswordResetToken passwordResetToken = passwordTokenRepository.findByToken(token);
        if (passwordResetToken == null) {
            return "Invalid token";
        }
        else if (passwordResetToken.getToken().equals(token)) {
            User user = passwordResetToken.getUser();
            user.setPassword(passwordEncoder.encode(passwordModel.getNewPassword()));
            userRepository.save(user);
            return "Password saved successfully";
        }
        else if (passwordResetToken.getExpirationTime().getTime() < Calendar.getInstance().getTime().getTime()) {
            passwordTokenRepository.delete(passwordResetToken);
            return "Expired token";
        }
        return "Invalid token";

    }

    @Override
    public boolean checkIfPasswordIsValid(PasswordModel passwordModel) {
        User user = userRepository.findByEmail(passwordModel.getEmail());
        return user != null && passwordEncoder.matches(passwordModel.getOldPassword(), user.getPassword());
    }

    @Override
    public void changePassword(PasswordModel passwordModel) {
        User user = userRepository.findByEmail(passwordModel.getEmail());
        if (user != null) {
            user.setPassword(passwordEncoder.encode(passwordModel.getNewPassword()));
            userRepository.save(user);
        }
    }

    @Override
    public VerificationToken generateNewVerificationToken(String oldToken) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(oldToken);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken = verificationTokenRepository.save(verificationToken);
        return verificationToken;
    }
}
