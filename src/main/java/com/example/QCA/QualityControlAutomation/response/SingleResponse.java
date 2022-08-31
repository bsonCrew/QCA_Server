package com.example.QCA.QualityControlAutomation.response;

import lombok.Getter;

@Getter
public class SingleResponse<T> extends CommonResponse {
    T data;
}
