package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.UserMapper;
import com.xtra.api.model.line.BlockedIp;
import com.xtra.api.model.user.User;
import com.xtra.api.projection.admin.user.UserView;
import com.xtra.api.repository.BlockedIpRepository;
import com.xtra.api.repository.LoginLogRepository;
import com.xtra.api.repository.UserRepository;
import com.xtra.api.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Validated
public class UserService extends CrudService<User, Long, UserRepository> {
    private final UserMapper userMapper;
    private final BlockedIpRepository blockedIpRepository;
    private final LoginLogRepository loginLogRepository;

    protected UserService(UserRepository repository, UserMapper userMapper, BlockedIpRepository blockedIpRepository, LoginLogRepository loginLogRepository) {
        super(repository, "User");
        this.userMapper = userMapper;
        this.blockedIpRepository = blockedIpRepository;
        this.loginLogRepository = loginLogRepository;
    }

    @Override
    protected Page<User> findWithSearch(String search, Pageable page) {
        return null;
    }


    public Page<UserView> getAllViews(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return (super.findAll(search, pageNo, pageSize, sortBy, sortDir).map(userMapper::convertToView));
    }

    public UserView getViewById(Long id) {
        return userMapper.convertToView(super.findByIdOrFail(id));
    }


    public Map<String, Object> verifyUser(UserDetails userDetails) {
        var dbUser = repository.findByUsername(userDetails.getUsername()).orElseThrow();
        if (dbUser.isBanned())
            throw new AuthenticationServiceException("User is Banned!");
        var userData = new LinkedHashMap<String, Object>();
        userData.put("username", dbUser.getUsername());
        userData.put("email", dbUser.getEmail());
        userData.put("type", dbUser.getUserType().toString());
        userData.put("permissions", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return userData;
    }

    public void blockIp(Long id) {
        var loginLog = loginLogRepository.findById(id).orElseThrow();
        var blockedIp = blockedIpRepository.findById(loginLog.getIp())
                .orElse(new BlockedIp(loginLog.getIp(), true));
        blockedIp.setForever(true);
        blockedIpRepository.save(blockedIp);
    }

}
