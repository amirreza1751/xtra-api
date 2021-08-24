package com.xtra.api.security;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.xtra.api.model.line.LoginLog;
import com.xtra.api.model.line.LoginLogStatus;
import com.xtra.api.projection.auth.UserCredentials;
import com.xtra.api.repository.UserRepository;
import com.xtra.api.service.admin.LogService;
import com.xtra.api.service.system.UserAuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static com.xtra.api.security.SecurityConstants.*;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final LogService logService;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, UserRepository userRepository, LogService logService) {
        this.authenticationManager = authenticationManager;
        //setFilterProcessesUrl("/api/users/login");
        this.userRepository = userRepository;
        this.logService = logService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            UserCredentials creds = new ObjectMapper()
                    .readValue(req.getInputStream(), UserCredentials.class);
            var user = userRepository.findByUsername(creds.getUsername()).orElseThrow(() -> new UsernameNotFoundException(creds.getUsername()));
            if (user.isUsing2FA()){
                int responseCode = 0;
                if (creds.getTotp() == null)
                {
                    res.sendError(460, "Totp is required.");
                } else if (!UserAuthService.getTOTPCode(user.get_2FASec()).equals(creds.getTotp())){
                    res.sendError(461, "Incorrect Totp");
                }
            }

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getUsername(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) {
        var user = ((org.springframework.security.core.userdetails.User) auth.getPrincipal());
        res.setContentType("application/json");
        res.setCharacterEncoding("UTF-8");
        try {
            var dbUser = userRepository.findByUsername(user.getUsername()).orElseThrow(() -> new UsernameNotFoundException(user.getUsername()));
            updateLastLoginDetails(user.getUsername(), ZonedDateTime.now(), req.getRemoteAddr());
            String token = JWT.create()
                    .withSubject(((org.springframework.security.core.userdetails.User) auth.getPrincipal()).getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .sign(HMAC512(SECRET.getBytes()));
            var out = res.getWriter();
            var userData = new LinkedHashMap<String, Object>();
            userData.put("token", token);
            userData.put("type", dbUser.getUserType().toString());
            userData.put("permissions", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
            out.print(new Gson().toJson(userData));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateLastLoginDetails(String username, ZonedDateTime lastLoginDate, String ipAddress) {
        var user = userRepository.findByUsername(username).orElseThrow();
        user.setLastLoginDate(lastLoginDate);
        user.setLastLoginIp(ipAddress);
        userRepository.save(user);

        logService.saveLoginLog(new LoginLog(user.getUsername(), user.getUserType(), ipAddress, LoginLogStatus.SUCCESS, LocalDateTime.now()));
    }
}
