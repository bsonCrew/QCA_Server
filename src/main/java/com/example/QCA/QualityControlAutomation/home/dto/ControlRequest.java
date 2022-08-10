package com.example.QCA.QualityControlAutomation.home.dto;

import javax.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ControlRequest {

    @NotBlank
    private String url;
}
