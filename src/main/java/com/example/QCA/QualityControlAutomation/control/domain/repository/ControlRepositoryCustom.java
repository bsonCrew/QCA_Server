package com.example.QCA.QualityControlAutomation.control.domain.repository;

import com.example.QCA.QualityControlAutomation.control.domain.ControlResult;

import java.util.List;

public interface ControlRepositoryCustom {
    // 커스텀 메소드 선언
    List<ControlResult> findTop5List();
}
