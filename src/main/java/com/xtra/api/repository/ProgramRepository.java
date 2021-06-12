package com.xtra.api.repository;

import com.xtra.api.model.epg.EpgProgram;
import com.xtra.api.model.vod.ProgramId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgramRepository extends JpaRepository<EpgProgram, ProgramId> {

    List<EpgProgram> findByIdTitle(String title);
}
