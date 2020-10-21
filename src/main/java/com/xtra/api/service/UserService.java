package com.xtra.api.service;

import com.xtra.api.model.User;
import com.xtra.api.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService extends CrudService<User, Long, UserRepository> {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    protected UserService(UserRepository repository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        super(repository, User.class);
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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
}
