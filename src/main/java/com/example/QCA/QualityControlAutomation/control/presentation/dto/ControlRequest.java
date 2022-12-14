package com.example.QCA.QualityControlAutomation.control.presentation.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ControlRequest {
    // POST 요청 시 사용

    // 검사를 수행할 웹사이트 주소
    @NotBlank
    private String url;

    // 검사 수행을 요청한 날짜, 매번 새로운 검사를 수행하지 않기 위해 사용
    @NotNull
    private LocalDate requestedDate;

    private boolean requestNewVal;
}
