package com.example.QCA.QualityControlAutomation.home.dto;

import lombok.Getter;

@Getter
public class ControlResponse {

    // 웹 사이트 이름
    String label;

    // 웹 사이트 주소
    String homepage;

    // 반환할 정보 (audits ~~~)
    int totalScore;

    public ControlResponse(String label, String homepage, int totalScore) {
        this.label = label;
        this.homepage = homepage;
        this.totalScore = totalScore;
    }
}
