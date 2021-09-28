package com.xtra.api.config;

import com.xtra.api.model.line.Line;
import com.xtra.api.model.user.User;
import com.xtra.api.model.user.UserType;
import com.xtra.api.repository.LineRepository;
import com.xtra.api.repository.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import static com.xtra.api.util.Utilities.generateRandomString;

@Component
public class StartupApplicationListener implements
        ApplicationListener<ContextRefreshedEvent> {
    private final UserRepository userRepository;
    private final LineRepository lineRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public StartupApplicationListener(UserRepository userRepository, LineRepository lineRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.lineRepository = lineRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // Create Super Admin if it does not exists
        if (!userRepository.existsByUserType(UserType.SUPER_ADMIN)) {
            var user = new User();
            user.setUsername("super_admin");
            user.setPassword(bCryptPasswordEncoder.encode("super_admin"));
            user.setUserType(UserType.SUPER_ADMIN);
            userRepository.save(user);
        }

        // Create system line if it doesn't exist
        if (!lineRepository.existsByUsername("system_line")) {
            var line = new Line();
            line.setUsername("system_line");
            line.setPassword(bCryptPasswordEncoder.encode(generateRandomString(8, 12, false)));
            line.setNeverExpire(true);
            String token;
            do {
                token = generateRandomString(8, 12, false);
            }
            while (lineRepository.findByLineToken(token).isPresent());
            line.setLineToken(token);
            line.setMaxConnections(10);
            lineRepository.save(line);
        }
    }
}
