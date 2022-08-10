package com.example.QCA.QualityControlAutomation.home.controller;

import com.example.QCA.QualityControlAutomation.home.dto.ControlRequest;
import com.example.QCA.QualityControlAutomation.home.dto.ControlResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/list")
    public String list() {
        log.info("GET request, list 호출");
        return "GET request, list 호출";
    }

    @PostMapping("/control")
    public ControlResponse control(@RequestBody ControlRequest controlRequest) {
        String url = controlRequest.getUrl();
        log.info(url + " 에 대한 POST request, control 호출");
        return new ControlResponse();
    }
}
