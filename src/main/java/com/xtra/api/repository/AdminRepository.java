package com.xtra.api.repository;

import com.xtra.api.model.user.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Page<Admin> findAllByUsernameContains(String text, Pageable pageable);

    List<Admin> findAllByUsernameContains(String username);
}
