package com.example.QCA.QualityControlAutomation.repository;

import com.example.QCA.QualityControlAutomation.domain.ControlResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ControlRepository extends JpaRepository<ControlResult, String> {
    ControlResult findByHomepage(String homepage);
}
