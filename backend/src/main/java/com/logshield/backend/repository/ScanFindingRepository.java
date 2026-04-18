package com.logshield.backend.repository;

import com.logshield.backend.entity.ScanFinding;
import com.logshield.backend.entity.ScanFinding.FindingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScanFindingRepository extends JpaRepository<ScanFinding, Long> {

    List<ScanFinding> findByScanJobId(Long scanJobId);

    List<ScanFinding> findByScanJobIdAndCategory(Long scanJobId, FindingCategory category);
}
