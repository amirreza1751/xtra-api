package com.xtra.api.service.system;

import com.xtra.api.model.Reseller;
import com.xtra.api.repository.ResellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

@Service
public class SystemResellerService {
    private static ResellerRepository repository;

    @Autowired
    public SystemResellerService(ResellerRepository repository) {
        SystemResellerService.repository = repository;
    }

    public static Reseller getCurrentReseller() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            var principal = auth.getPrincipal();
            return repository.findByUsername(((User) principal).getUsername()).orElseThrow(() -> new AccessDeniedException("access denied"));
        }
        throw new AccessDeniedException("access denied");
    }
}
