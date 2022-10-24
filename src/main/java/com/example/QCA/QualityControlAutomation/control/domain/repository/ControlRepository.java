package com.example.QCA.QualityControlAutomation.control.domain.repository;

import com.example.QCA.QualityControlAutomation.control.domain.ControlResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ControlRepository extends JpaRepository<ControlResult, String> {
    Optional<ControlResult> findByHomepage(String homepage);
    List<ControlResult> findTop5ByRecentRequestedDateIsNotNullOrderByRecentRequestedDateDesc();
}
