package com.example.QCA.QualityControlAutomation.common;

import lombok.Getter;

@Getter
public class InitInfo {
    String label;
    String homepage;

    public InitInfo(String label, String homepage) {
        this.label = label;
        this.homepage = homepage;
    }
}
