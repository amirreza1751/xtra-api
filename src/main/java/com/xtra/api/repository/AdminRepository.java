package com.xtra.api.repository;

import com.xtra.api.model.user.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Page<Admin> findAllByUsernameLike(String text, Pageable pageable);
}
