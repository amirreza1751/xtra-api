package com.xtra.api.service.admin;

import com.xtra.api.mapper.admin.UserMapper;
import com.xtra.api.model.User;
import com.xtra.api.projection.user.UserView;
import com.xtra.api.repository.UserRepository;
import com.xtra.api.service.CrudService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

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
        return new PageImpl<>(super.findAll(search, pageNo, pageSize, sortBy, sortDir).stream().map(userMapper::convertToView).collect(Collectors.toList()));
    }

    public UserView getViewById(Long id) {
        return userMapper.convertToView(super.findByIdOrFail(id));
    }


    public Map<String, Object> verifyUser(Authentication auth) {
        var dbUser = repository.findByUsername(auth.getName());
        var userData = new LinkedHashMap<String, Object>();
        userData.put("type", dbUser.getUserType().toString());
        userData.put("permissions", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return userData;
    }

  /*  public Page<AdminView> getAllAdminViews(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return new PageImpl<>(findAll(search, pageNo, pageSize, sortBy, sortDir).stream().map(userMapper::convertToAdminView).collect(Collectors.toList()));
    }*/
}
