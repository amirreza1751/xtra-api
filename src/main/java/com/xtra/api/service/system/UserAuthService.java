package com.xtra.api.service.system;

import com.xtra.api.model.line.Line;
import com.xtra.api.model.user.Reseller;
import com.xtra.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService {
    private static UserRepository repository;

    @Autowired
    public UserAuthService(UserRepository repository) {
        UserAuthService.repository = repository;
    }

    public static com.xtra.api.model.user.User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
            var principal = auth.getPrincipal();
            return repository.findByUsername(((User) principal).getUsername()).orElseThrow(() -> new AccessDeniedException("access denied"));
        }
        throw new AccessDeniedException("access denied");
    }

    public static Line getCurrentLine() {
        return (Line) getCurrentUser();
    }

    public static Reseller getCurrentReseller() {
        return (Reseller) getCurrentUser();
    }
}
