package com.xtra.api.config;

import com.xtra.api.model.user.User;
import com.xtra.api.model.user.UserType;
import com.xtra.api.repository.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class StartupApplicationListener implements
        ApplicationListener<ContextRefreshedEvent> {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public StartupApplicationListener(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!userRepository.existsByUserType(UserType.SUPER_ADMIN)) {
            var user = new User();
            user.setUsername("super_admin");
            user.setPassword(bCryptPasswordEncoder.encode("super_admin"));
            user.setUserType(UserType.SUPER_ADMIN);
            userRepository.save(user);
        }
    }
}
