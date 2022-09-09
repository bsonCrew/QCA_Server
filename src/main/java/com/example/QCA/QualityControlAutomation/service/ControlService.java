package com.example.QCA.QualityControlAutomation.service;

import com.example.QCA.QualityControlAutomation.domain.ControlResult;
import com.example.QCA.QualityControlAutomation.domain.InitInfo;
import com.example.QCA.QualityControlAutomation.dto.ControlRequest;
import com.example.QCA.QualityControlAutomation.repository.ControlRepository;
import com.example.QCA.QualityControlAutomation.response.CommonResponse;
import com.example.QCA.QualityControlAutomation.response.ResponseService;
import com.example.QCA.QualityControlAutomation.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.URI;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ControlService {

    private final ControlRepository controlRepository;
    private final ResponseService responseService;
    private final boolean isWindow;
    private final Environment env;

    @Autowired
    public ControlService(ControlRepository controlRepository, ResponseService responseService, Environment env) {
        this.controlRepository = controlRepository;
        this.responseService = responseService;
        this.env = env;
        this.isWindow = System.getProperty("os.name").toLowerCase().contains("windows");
    }

    private String filePath;

    private String vnuCommand;

    private String outputPath;

    public void setLabelAndHomepage() throws Exception {
        DataUtil dataUtil = new DataUtil();
        List<InitInfo> initInfoList = dataUtil.getLabelAndHomepage();
        List<ControlResult> controlResultList = new ArrayList<>();

        for (InitInfo initInfo : initInfoList)
            controlResultList.add(new ControlResult(initInfo.getLabel(), initInfo.getHomepage()));

        controlRepository.saveAll(controlResultList);
    }

    public CommonResponse findList() {
        List<ControlResult> list = controlRepository.findTop5ByRecentRequestedDateIsNotNullOrderByRecentRequestedDateDesc();
        if (list.isEmpty()) list.add(new ControlResult());
        return responseService.getListResponse(list);
    }

    @Transactional
    public CommonResponse findControlResult(ControlRequest controlRequest) throws Exception {
        initPath();

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

    //
    // private methods
    //

    // 검사 수행
    private ControlResult operateQualityControl(String label, String homepage, LocalDate requestedDate) throws Exception {
        ControlResult controlResult = new ControlResult(label, homepage);
        controlResult.setRecentRequestedDate(requestedDate);
        controlResult.setValidator(operateValidator(homepage));
        controlResult.setRobot(operateRobots(homepage));
        operateLighthouse(homepage);

        JSONObject jsonObject = parseJson(homepage.replace("/", "").replace("http:", "").replace("https:", ""));

        controlResult.setAudits(jsonObject.get("audits").toString());
        controlResult.setCategoryScore(jsonObject.get("categories").toString());

        return controlResult;
    }

    private String operateValidator(String homepage) throws IOException {
        log.info("validator 검사 수행");
        Runtime runtime = Runtime.getRuntime();
        JSONArray validator = new JSONArray();
        Process process = runtime.exec(vnuCommand + homepage);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String s;
            while ((s = br.readLine()) != null)
                validator.add(processValidatorResult(s));
        } catch (Exception e) {
            log.error("validator 실행 도중 에러 발생 : {}", e.getMessage());
        } finally {
            process.destroy();
        }

        log.info("validator 검사 완료");
        return validator.toJSONString();
    }

    private void operateLighthouse(String homepage) throws IOException, InterruptedException {
        log.info("lighthouse 검사 수행");
        ProcessBuilder pb = new ProcessBuilder("sh", "lighthouse.sh", homepage);
        pb.redirectErrorStream(true);
        pb.directory(new File(filePath));
        Process process = pb.start();

        int exitCode = process.waitFor();
        assert exitCode == 0;
        process.destroy();
        log.info("lighthouse 검사 완료");
    }

    private boolean operateRobots(String homepage) {
        log.info("robots.txt 검사 수행");

        URI uri = UriComponentsBuilder
                .fromUriString(homepage)
                .path("/robots.txt")
                .encode()
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        String robots = restTemplate.getForObject(uri, String.class);

        // robots.txt가 없거나, 완전 허용에 해당하는 경우
        return robots == null || (robots.contains("User-agent") && !robots.contains("disallow"));
    }

    private JSONObject processValidatorResult(String s) {
        JSONObject jsonObject = new JSONObject();
        String[] tokens = s.split(":");
        String type = "warning";

        if (!tokens[3].contains(type)) type = "error";
        String desc = tokens[4];

        jsonObject.put("type", type);
        jsonObject.put("description", desc);

        return jsonObject;
    }

    private JSONObject parseJson(String fileName) throws IOException, ParseException {
        log.info("json Name : {}", fileName);
        return (JSONObject) new JSONParser().parse(new FileReader(outputPath + fileName + "_output.json"));
    }

    // 두 날짜의 기간 차이를 달 기준으로 비교하여 반환하는 함수
    private boolean isinMonth(LocalDate requestedDate, LocalDate recentRequestDate) {
        if (requestedDate.isBefore(recentRequestDate))
            throw new RuntimeException("요청날짜가 저장된 날짜보다 먼저일 수 없습니다.");

        // 기간 차이가 1달 이내라면 true 반환
        Period period = Period.between(recentRequestDate, requestedDate);
        return period.getMonths() == 0;
    }

    private void initPath() {
        filePath = isWindow ? env.getProperty("windowsFilePath") : env.getProperty("macFilePath");
        vnuCommand = "java -jar " + filePath + env.getProperty("vnuPath") + " ";
        outputPath = filePath + env.getProperty("outputPath");
    }
}
