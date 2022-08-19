package com.example.QCA.QualityControlAutomation.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class ControlResult {

    @Id
    private String label;

    private String homepage;

    @OneToMany
    private List<Audit> audits;

    @OneToOne
    private CategoryScore categoryScore;

    private Date requestedDate;

    public ControlResult(String label, String homepage) {
        this.label = label;
        this.homepage = homepage;
    }
}
