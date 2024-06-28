package com.jaideep.springsecurityclient.service;

import com.jaideep.springsecurityclient.entity.PasswordResetToken;
import com.jaideep.springsecurityclient.entity.User;
import com.jaideep.springsecurityclient.entity.VerificationToken;
import com.jaideep.springsecurityclient.model.PasswordModel;
import com.jaideep.springsecurityclient.model.UserModel;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(User user, String token);

    String validateVerificationToken(String token);

    VerificationToken generateNewVerificationToken(String oldToken);

    void resendVerificationTokenMail(String applicationUrl, VerificationToken verificationToken);

    User findUserByEmail(String email);
    PasswordResetToken createPasswordResetTokenForUser(String email);

    void sendPasswordResetTokenMail(String applicationUrl, String token);

    String validatePasswordResetToken(String token, PasswordModel passwordModel);

    boolean checkIfPasswordIsValid(PasswordModel passwordModel);

    void changePassword(PasswordModel passwordModel);

}
