package com.logshield.backend.repository;

import com.logshield.backend.entity.ScanJob;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanJobRepository extends JpaRepository<ScanJob, Long> {

    List<ScanJob> findAllByOrderByUploadedAtDesc();

    @Query("SELECT j FROM ScanJob j WHERE " +
           "(:search IS NULL OR LOWER(j.filename) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY j.uploadedAt DESC")
    Page<ScanJob> findByFilenameContaining(@Param("search") String search, Pageable pageable);
}
