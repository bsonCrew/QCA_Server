package com.example.QCA.QualityControlAutomation.service;

import com.example.QCA.QualityControlAutomation.domain.ControlResult;
import com.example.QCA.QualityControlAutomation.domain.InitInfo;
import com.example.QCA.QualityControlAutomation.dto.ControlRequest;
import com.example.QCA.QualityControlAutomation.repository.ControlRepository;
import com.example.QCA.QualityControlAutomation.response.CommonResponse;
import com.example.QCA.QualityControlAutomation.response.ResponseService;
import com.example.QCA.QualityControlAutomation.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Time;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ControlService {

    private final ControlRepository controlRepository;
    private final ResponseService responseService;
    private final String filePath = "/Users/bsu/Desktop/2022OpenSW";

    @Autowired
    public ControlService(ControlRepository controlRepository, ResponseService responseService) {
        this.controlRepository = controlRepository;
        this.responseService = responseService;
    }

    public void setLabelAndHomepage() throws Exception {
        DataUtil dataUtil = new DataUtil();
        List<InitInfo> initInfoList = dataUtil.getLabelAndHomepage();
        List<ControlResult> controlResultList = new ArrayList<>();

        for (InitInfo initInfo : initInfoList)
            controlResultList.add(new ControlResult(initInfo.getLabel(), initInfo.getHomepage()));

        controlRepository.saveAll(controlResultList);
    }

    public CommonResponse findControlResult(ControlRequest controlRequest) throws Exception {
        String homepage = controlRequest.getUrl();
        LocalDate requestedDate = controlRequest.getRequestedDate();

        log.info("요청 정보 // homepage : " + homepage + ", requestedDate : " + requestedDate);

        // Repository 조회, 값이 무조건 있음
        // 단지 요청날짜가 있냐, 없냐의 차이고, 있다면 1달 이내인지 비교해야 함
        ControlResult findResult = controlRepository.findByHomepage(homepage);

        String label = findResult.getLabel();
        LocalDate recentRequestDate = findResult.getRecentRequestedDate();

        log.info("조회 정보 // label : " + label + ", recentRequestDate : " + recentRequestDate);

        // 저장된 날짜가 없거나, 검사한 지 1달이 넘은 경우는 새로 검사하고, 결과를 저장한다.
        if (recentRequestDate == null || !isinMonth(requestedDate, recentRequestDate)) {
            findResult = operateQualityControl(label, homepage, requestedDate);
            controlRepository.save(findResult);
        }
        return responseService.getSingleResponse(findResult);
    }

    // 검사 수행
    private ControlResult operateQualityControl(String label, String homepage, LocalDate requestedDate) throws Exception {
        ControlResult controlResult = new ControlResult(label, homepage);
        controlResult.setRecentRequestedDate(requestedDate);

        ProcessBuilder pb = new ProcessBuilder("sh", "lighthouse.sh", homepage);
        pb.redirectErrorStream(true);
        pb.directory(new File(filePath));
        Process process = pb.start();
        int exitCode = process.waitFor();
        assert exitCode == 0;

        return parseJson(controlResult);
    }

    private ControlResult parseJson(ControlResult controlResult) throws Exception {
        String fileName = controlResult.getHomepage().replace("/", "").replace("http:", "").replace("https:", "");
        log.info("fileName : {}", fileName);
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(new FileReader(filePath + "/QCA_Server/src/output/" + fileName + "_output.json"));

        controlResult.setAudits(jsonObject.get("audits").toString());
        controlResult.setCategoryScore(jsonObject.get("categories").toString());

        return controlResult;
    }

    // 두 날짜의 기간 차이를 달 기준으로 비교하여 반환하는 함수
    private boolean isinMonth(LocalDate requestedDate, LocalDate recentRequestDate) {
        if (requestedDate.isBefore(recentRequestDate))
            throw new RuntimeException("요청날짜가 저장된 날짜보다 먼저일 수 없습니다.");

        // 기간 차이가 1달 이내라면 true 반환
        Period period = Period.between(recentRequestDate, requestedDate);
        return period.getMonths() == 0;
    }
}
