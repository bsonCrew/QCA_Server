package com.example.QCA.QualityControlAutomation.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ResponseService {
    // 단일 데이터만 전달
    public <T> SingleResponse<T> getSingleResponse(T data) {
        SingleResponse response = new SingleResponse();
        response.data = data;
        setSuccessResponse(response);
        return response;
    }

    // 데이터 리스트를 전달
    public <T> ListResponse<T> getListResponse(List<T> data) {
        ListResponse<T> response = new ListResponse<>();
        response.data = data;
        setSuccessResponse(response);
        return response;
    }

    void setSuccessResponse(CommonResponse response) {
        response.status = HttpStatus.OK.value();
        response.message = "SUCCESS";
    }
}
