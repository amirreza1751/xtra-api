package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.UserMapper;
import com.xtra.api.model.User;
import com.xtra.api.projection.admin.user.UserView;
import com.xtra.api.repository.UserRepository;
import com.xtra.api.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserService extends CrudService<User, Long, UserRepository> {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserMapper userMapper;

    protected UserService(UserRepository repository, BCryptPasswordEncoder bCryptPasswordEncoder, UserMapper userMapper) {
        super(repository, User.class);
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    protected Page<User> findWithSearch(Pageable page, String search) {
        return null;
    }

    @Override
    public User insert(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return super.insert(user);
    }

    public Page<UserView> getAllViews(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return (super.findAll(search, pageNo, pageSize, sortBy, sortDir).map(userMapper::convertToView));
    }

    public UserView getViewById(Long id) {
        return userMapper.convertToView(super.findByIdOrFail(id));
    }


    public Map<String, Object> verifyUser(Authentication auth) {
        var dbUser = repository.findByUsername(auth.getName()).orElseThrow();
        var userData = new LinkedHashMap<String, Object>();
        userData.put("type", dbUser.getUserType().toString());
        userData.put("permissions", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return userData;
    }

}
