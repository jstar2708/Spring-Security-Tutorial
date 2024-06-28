package com.jaideep.spring_security_client.service;

import com.jaideep.spring_security_client.entity.PasswordResetToken;
import com.jaideep.spring_security_client.entity.User;
import com.jaideep.spring_security_client.entity.VerificationToken;
import com.jaideep.spring_security_client.model.PasswordModel;
import com.jaideep.spring_security_client.model.UserModel;

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
