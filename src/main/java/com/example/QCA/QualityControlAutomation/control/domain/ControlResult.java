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

    @Id
    private String label;

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
}