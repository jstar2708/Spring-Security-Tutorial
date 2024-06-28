package com.jaideep.springsecurityclient.listener;

import com.jaideep.springsecurityclient.entity.User;
import com.jaideep.springsecurityclient.event.RegistrationCompleteEvent;
import com.jaideep.springsecurityclient.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    private final UserService userService;
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        //Create verification token for the user with link
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.saveVerificationTokenForUser(user, token);
        String url = event.getApplicationUrl()
                + "/v1/api/verifyRegistration?token="
                + token;
        //Send mail to user
        log.info("Click the link to verify your account: {}", url);
    }
}
