package com.xtra.api.service.system;

import com.xtra.api.model.role.Role;
import com.xtra.api.model.user.UserType;
import com.xtra.api.repository.PermissionRepository;
import com.xtra.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository, PermissionRepository permissionRepository) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getUserType()));

        if (user.getRole() != null) {
            authorities.addAll(getAuthorities(user.getRole()));
        } else if (user.getUserType() == UserType.SUPER_ADMIN) {
            authorities.addAll(getAuthoritiesSuperAdmin());
        }
        return new User(user.getUsername(), user.getPassword(), true, true, true, !user.isBanned(), authorities);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Role role) {
        if (role.getType() == UserType.SUPER_ADMIN) {
            return permissionRepository.findAll().stream().map(permission -> new SimpleGrantedAuthority(permission.getId().getName())).collect(Collectors.toList());
        }
        return emptyIfNull(role.getPermissions()).stream().map(permission -> new SimpleGrantedAuthority(permission.getPermission().getId().getName())).collect(Collectors.toList());
    }

    private Collection<? extends GrantedAuthority> getAuthoritiesSuperAdmin() {
        return permissionRepository.findAll().stream().map(permission -> new SimpleGrantedAuthority(permission.getId().getName())).collect(Collectors.toList());
    }
}
