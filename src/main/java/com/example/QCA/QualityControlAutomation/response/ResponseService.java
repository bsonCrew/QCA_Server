package com.example.QCA.QualityControlAutomation.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ResponseService {
    // 성공 여부만 전달
    public CommonResponse getSuccessResponse() {
        CommonResponse response = new CommonResponse();
        setSuccessResponse(response);
        return response;
    }

    // 데이터 리스트를 전달 - /api/list - GET 요청에 사용
    public <T> ListResponse<T> getListResponse(List<T> data) {
        ListResponse<T> response = new ListResponse<>();
        response.data = data;
        log.info(String.valueOf(data));
        setSuccessResponse(response);
        return response;
    }

    void setSuccessResponse(CommonResponse response) {
        response.status = HttpStatus.OK.value();
        response.message = "SUCCESS";
    }
}
