package com.example.QCA.QualityControlAutomation.home.dto;

import lombok.Getter;

@Getter
public class ControlResponse {

    private String url;

    private String name;

    private int compatibilityScore;

    private int accessibilityScore;

    private int opennessScore;

    private int connectivityScore;

    private int convenienceScore;

    private int totalScore;
}
