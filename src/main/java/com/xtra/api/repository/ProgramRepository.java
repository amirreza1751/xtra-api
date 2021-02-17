package com.xtra.api.repository;

import com.xtra.api.model.Program;
import com.xtra.api.model.ProgramId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgramRepository extends JpaRepository<Program, ProgramId> {

    List<Program> findByIdTitle(String title);
}
