package com.example.QCA.QualityControlAutomation.domain;

import lombok.NoArgsConstructor;
import net.bytebuddy.utility.nullability.AlwaysNull;
import org.springframework.lang.Nullable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
@NoArgsConstructor
public class Audit {

    @Id
    private String label;

    private String id;

    @Lob
    private String description;

    private long score;

    @Lob
    @Nullable
    private String detail;

    @Nullable
    private String displayValue;

    public Audit(String id, String description, long score, String detail, String displayValue) {
        this.id = id;
        this.description = description;
        this.score = score;
        this.detail = detail;
        this.displayValue = displayValue;
    }
}
