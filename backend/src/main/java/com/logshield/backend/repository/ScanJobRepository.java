package com.logshield.backend.repository;

import com.logshield.backend.entity.ScanJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanJobRepository extends JpaRepository<ScanJob, Long> {

    List<ScanJob> findAllByOrderByUploadedAtDesc();
}
