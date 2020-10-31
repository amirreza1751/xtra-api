package com.xtra.api.service;

import com.xtra.api.mapper.UserMapper;
import com.xtra.api.model.User;
import com.xtra.api.projection.UserInsertView;
import com.xtra.api.projection.UserView;
import com.xtra.api.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public UserView insert(UserInsertView userView) {
        var user = userMapper.convertToEntity(userView);
        return userMapper.convertToDto(this.insert(user));
    }

    public Page<UserView> getAllViews(String search, int pageNo, int pageSize, String sortBy, String sortDir) {
        return new PageImpl<>(super.findAll(search, pageNo, pageSize, sortBy, sortDir).stream().map(userMapper::convertToDto).collect(Collectors.toList()));
    }

    public UserView getViewById(Long id) {
        return userMapper.convertToDto(super.findByIdOrFail(id));
    }

    public UserView updateOrFail(Long id, UserInsertView userView) {
        var existingUser = findByIdOrFail(id);
        List<String> toIgnore = new ArrayList<>();
        toIgnore.add("id");
        if (userView.getRoleId() == null) toIgnore.add("roleId");
        if (userView.getPassword() == null) toIgnore.add("password");
        else userView.setPassword(bCryptPasswordEncoder.encode(userView.getPassword()));
        copyProperties(userMapper.convertToEntity(userView), existingUser, toIgnore.toArray(new String[0]));
        return userMapper.convertToDto(repository.save(existingUser));
    }
}
