package com.example.QCA.QualityControlAutomation.controller;

import com.example.QCA.QualityControlAutomation.dto.ControlRequest;
import com.example.QCA.QualityControlAutomation.dto.ControlResponse;
import com.example.QCA.QualityControlAutomation.response.CommonResponse;
import com.example.QCA.QualityControlAutomation.response.ResponseService;
import com.example.QCA.QualityControlAutomation.service.ControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class HomeController {

    private final ResponseService responseService;
    private final ControlService controlService;
    private boolean hasInit = false;

    @Autowired
    public HomeController(ResponseService responseService, ControlService controlService) {
        this.responseService = responseService;
        this.controlService = controlService;
    }

    @GetMapping("/list")
    public CommonResponse resultList() {
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

    @PostMapping("/control")
    public CommonResponse controlDetail(@RequestBody ControlRequest controlRequest) throws Exception {
        String url = controlRequest.getUrl();
        log.info(url + " 에 대한 POST request, control 호출");
        return controlService.findControlResult(controlRequest);
    }

    // label, homepage 쌍을 DB에 저장하기 위해 처음 1회만 사용
    @GetMapping("")
    public void init() throws Exception {
        log.info("DB 초기화");
        if (!hasInit)
            controlService.setLabelAndHomepage();
        hasInit = true;
    }
}
