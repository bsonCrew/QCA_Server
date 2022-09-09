package com.example.QCA.QualityControlAutomation.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

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

    @Lob
    private String categoryScore;

    @Lob
    private String validator;

    private boolean robot;

    @Nullable
    private LocalDate recentRequestedDate;

    public ControlResult(String label, String homepage) {
        this.label = label;
        this.homepage = homepage;
    }
}
