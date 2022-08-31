package com.example.QCA.QualityControlAutomation.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ControlResult {

    @Id
    private String label;

    private String homepage;

    @Lob
    private String audits;

    // lighthouse - 전자정부 가이드 매핑으로 모두 처리된다면, 필요없어짐!
    @Lob
    private String categoryScore;

    private LocalDate recentRequestedDate;

    public ControlResult(String label, String homepage) {
        this.label = label;
        this.homepage = homepage;
    }
}
