package com.example.QCA.QualityControlAutomation.domain;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public class CategoryScore {

    @Id
    private String label;

    private int performanceScore;

    private int accessibilityScore;

    private int bestPracticesScore;

    private int seoScore;

    private int pwaScore;

    public CategoryScore(String label, int performanceScore, int accessibilityScore, int bestPracticesScore, int seoScore, int pwaScore) {
        this.label = label;
        this.performanceScore = performanceScore;
        this.accessibilityScore = accessibilityScore;
        this.bestPracticesScore = bestPracticesScore;
        this.seoScore = seoScore;
        this.pwaScore = pwaScore;
    }
}
