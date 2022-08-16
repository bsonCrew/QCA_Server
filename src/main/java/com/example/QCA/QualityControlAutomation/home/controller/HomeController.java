package com.example.QCA.QualityControlAutomation.home.controller;

import com.example.QCA.QualityControlAutomation.home.dto.ControlResponse;
import com.example.QCA.QualityControlAutomation.response.CommonResponse;
import com.example.QCA.QualityControlAutomation.response.ResponseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class HomeController {

    private final ResponseService responseService = new ResponseService();

    @GetMapping("/list")
    public CommonResponse list() {
        log.info("GET request, list 호출");

        // 임의로 전달할 정보들
        List<ControlResponse> list = new ArrayList<>();
        list.add(new ControlResponse("청와대" , "https://www.president.go.kr/", 90));
        list.add(new ControlResponse("국회" , "https://www.assembly.go.kr/portal/main/main.do", 85));
        list.add(new ControlResponse("대법원" , "https://www.scourt.go.kr/scourt/index.html", 85));
        list.add(new ControlResponse("경찰청" , "https://www.police.go.kr/", 80));
        list.add(new ControlResponse("도로교통공단" , "https://www.koroad.or.kr/", 79));

        return responseService.getListResponse(list);
    }

//    @PostMapping("/control")
//    public ControlResponse control(@RequestBody ControlRequest controlRequest) {
//        String url = controlRequest.getUrl();
//        log.info(url + " 에 대한 POST request, control 호출");
//        return new ControlResponse();
//    }
}
