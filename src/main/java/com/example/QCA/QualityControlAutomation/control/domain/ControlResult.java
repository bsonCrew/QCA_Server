package com.example.QCA.QualityControlAutomation.control.domain;

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

    private String label;

    @Id
    private String homepage;

    @Lob
    private String audits;

    @Lob
    private String validator;

    @Lob
    private String robot;

    @Nullable
    private LocalDate recentRequestedDate;

    public ControlResult(String label, String homepage) {
        this.label = label;
        this.homepage = homepage;
    }

    public ControlResult(String label, String homepage, String audits, String validator, String robot, @Nullable LocalDate recentRequestedDate) {
        this.label = label;
        this.homepage = homepage;
        this.audits = audits;
        this.validator = validator;
        this.robot = robot;
        this.recentRequestedDate = recentRequestedDate;
    }
}