package com.example.QCA.QualityControlAutomation.controller;

import com.example.QCA.QualityControlAutomation.dto.ControlRequest;
import com.example.QCA.QualityControlAutomation.response.CommonResponse;
import com.example.QCA.QualityControlAutomation.service.ControlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class HomeController {

    private final ControlService controlService;

    @Autowired
    public HomeController(ControlService controlService) {
        this.controlService = controlService;
    }

    @GetMapping(value = "/list")
    @ResponseBody
    public CommonResponse resultList() {
        log.info("GET request, resultList 호출");
        return controlService.findList();
    }

    @PostMapping("/control")
    public CommonResponse controlDetail(@RequestBody ControlRequest controlRequest) throws Exception {
        String url = controlRequest.getUrl();
        log.info(url + " 에 대한 POST request, control 호출");
        return controlService.findControlResult(controlRequest);
    }

    // 처음 한 번만 실행
//    @GetMapping()
//    public void dbInit() throws Exception {
//        log.info("DB 초기화");
//        controlService.setLabelAndHomepage();
//        log.info("DB 초기화 완료");
//    }
}
