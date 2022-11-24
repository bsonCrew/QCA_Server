package com.example.QCA.QualityControlAutomation.control.application.service;

import com.example.QCA.QualityControlAutomation.control.domain.ControlResult;
import com.example.QCA.QualityControlAutomation.common.InitInfo;
import com.example.QCA.QualityControlAutomation.control.presentation.dto.ControlRequest;
import com.example.QCA.QualityControlAutomation.control.domain.repository.ControlRepository;
import com.example.QCA.QualityControlAutomation.response.CommonResponse;
import com.example.QCA.QualityControlAutomation.response.ResponseService;
import com.example.QCA.QualityControlAutomation.common.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.*;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ControlService {

    private final ControlRepository controlRepository;
    private final ResponseService responseService;
    private final Environment env;

    @Autowired
    public ControlService(ControlRepository controlRepository, ResponseService responseService, Environment env) {
        this.controlRepository = controlRepository;
        this.responseService = responseService;
        this.env = env;
        initPath();
        this.pb = new ProcessBuilder();
        this.pb.redirectErrorStream(true);
        this.pb.directory(new File(utilPath));
    }

    private String utilPath;

    private String vnuCommand;

    private String outputPath;

    private final ProcessBuilder pb;

    /**
    * DB에 전자정부 웹사이트 목록을 저장할 때 사용하는 메소드
    * */
    public void setLabelAndHomepage() throws Exception {
        DataUtil dataUtil = new DataUtil();
        List<InitInfo> initInfoList = dataUtil.getLabelAndHomepage();
        List<ControlResult> controlResultList = new ArrayList<>();

        for (InitInfo initInfo : initInfoList) {
            controlResultList.add(new ControlResult(initInfo.getLabel(), initInfo.getHomepage()));
        }

        controlRepository.saveAll(controlResultList);
    }

    /**
    * 최근 진단한 웹사이트 조회 시 사용하는 메소드
     * @return CommonResponse
    * */
    public CommonResponse findList() {
        log.info("-----findList() 실행, DB 조회-----");
        List<ControlResult> list = controlRepository.findTop5List();
        log.info("-----DB 조회 완료-----");

        return responseService.getListResponse(list);
    }

    /**
    * 웹사이트 진단 요청 시 사용하는 메소드
     * @return CommonResponse
    * */
    @Transactional
    public CommonResponse findControlResult(ControlRequest controlRequest) throws Exception {
        log.info("-----findControlResult() 실행-----");

        LocalDate requestedDate = controlRequest.getRequestedDate();
        // 날짜가 null인지와 오늘보다 뒷 날짜인지 확인
        // 같은 날이어도 통과
        checkDateValidation(requestedDate);

        ControlResult controlResult = computeControlResult(controlRequest, requestedDate);
        controlRepository.save(controlResult);
        return responseService.getSingleResponse(controlResult);
    }

    /*
    * private methods
    * */

    private ControlResult computeControlResult(ControlRequest controlRequest, LocalDate requestedDate) throws Exception {
        String homepage = removeSlash(controlRequest.getUrl());
        String domain = parseHomepage(homepage);
        boolean requestNewVal = controlRequest.isRequestNewVal();
        String jsonName = (requestedDate + String.valueOf(homepage.hashCode())).replace("-", "_");

        log.info("요청 정보 ==> homepage : {}, requestedDate : {}, requestNewVal : {}", homepage, requestedDate, requestNewVal);

        // 단지 요청날짜가 있냐, 없냐의 차이고, 있다면 1달 이내인지 비교해야 함
        // 우선 입력 URL로 DB 조회
        Optional<ControlResult> findResult = controlRepository.findByHomepage(homepage);

        // 조회된 값이 없는 경우
        if (findResult.isEmpty()) {
            // 도메인을 추출해서 DB에 조회
            log.info("domain : {}", domain);

            findResult = controlRepository.findByHomepage(domain);

            if (findResult.isEmpty()) {
                throw new NoSuchElementException("유효하지 않은 URL입니다.");
            }
            return operateAllControl(findResult.get().getLabel(), homepage, domain, jsonName, requestedDate);
        }

        // 조회된 값이 있는 경우
        // label, homepage, date는 존재
        // date는 검사를 수행하게 되면 변경
        ControlResult controlResult = findResult.get();
        LocalDate recentRequestDate = controlResult.getRecentRequestedDate();
            label = findResult.get().getLabel();
            result = operateAllControl(label, homepage, domain, jsonName, requestedDate);

            controlRepository.save(result);
            return responseService.getSingleResponse(result);
        }
        
        // 조회된 값이 있음
        // label, homepage, date는 존재
        // date는 검사를 수행하게 되면 변경
        ControlResult controlResult = findResult.get();
        label = controlResult.getLabel();
        LocalDate recentRequestDate = controlResult.getRecentRequestedDate();

        // 이미 진단된 결과 날짜보다 더 앞인 경우인지 확인
        // 같은 날이면 requestNewVal에 따라 진행하기에 같은 날도 통과
        checkDateValidation(requestedDate, recentRequestDate);

        // 새로 검사하는 경우는 requestNewVal이 True이거나, DB에 검사한 날짜가 없거나, 검사한 지 1달이 넘은 경우이다.
        if (requestNewVal || recentRequestDate == null || !isinMonth(requestedDate, recentRequestDate)) {
            result = operateAllControl(label, homepage, domain, jsonName, requestedDate);
        } else {
            result = controlResult;
        }

        controlRepository.save(result);
        return responseService.getSingleResponse(result);
    }

        // 이미 진단된 결과 날짜보다 더 앞인 경우인지 확인
        // 같은 날이면 requestNewVal에 따라 진행하기에 같은 날도 통과
        checkDateValidation(requestedDate, recentRequestDate);

        // 새로 검사하는 경우는 requestNewVal이 True이거나, DB에 검사한 날짜가 없거나, 검사한 지 1달이 넘은 경우이다.
        if (requestNewVal || recentRequestDate == null || !isinMonth(requestedDate, recentRequestDate)) {
            return operateAllControl(controlResult.getLabel(), homepage, domain, jsonName, requestedDate);
        }
        return controlResult;
    }
    private ControlResult operateAllControl(String label, String homepage, String domain, String jsonName, LocalDate requestedDate) throws Exception {
        log.info("-----진단 시작-----");
        String validator = operateValidator(homepage);
        String robot = operateRobots(domain);
        operateLighthouse(homepage, jsonName);
        String audits = parseJson(jsonName).get("audits").toString();
        log.info("-----진단 완료-----");

        return new ControlResult(label, homepage, audits, validator, robot, requestedDate);
    }

    private String operateValidator(String homepage) throws IOException {
        log.info("validator 검사 수행");
        Runtime runtime = Runtime.getRuntime();
        JSONArray validator = new JSONArray();
        Process process = runtime.exec(vnuCommand + homepage);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String s = br.readLine();

            if (s != null) {
                validator = processValidatorResult(s);
            }
        } catch (Exception e) {
            log.error("validator 실행 도중 에러 발생 : {}", e.getMessage());
        } finally {
            process.destroy();
        }

        log.info("validator 검사 완료");
        return validator.toJSONString();
    }

    private void operateLighthouse(String homepage, String jsonName) throws IOException, InterruptedException {
        log.info("lighthouse 검사 수행");
        Process process = pb.command("sh", "lighthouse.sh", homepage, jsonName).start();
        int exitCode = process.waitFor();
        process.destroy();

        if (exitCode != 0) {
            log.info("exitCode : {}", exitCode);
            throw new UnsupportedOperationException("lighthouse 검사 중 문제가 발생했습니다.");
        }

        log.info("lighthouse 검사 완료");
    }

    private String operateRobots(String homepage) {
        log.info("robots.txt 검사 수행");

        String result = requestRobots(homepage);

        log.info("robots.txt 검사 완료");

        if (result == null) {
            return null;
        }
        return parseRobot(result.split("\r\n"));
    }

    private String requestRobots(String homepage) {
        URI uri = UriComponentsBuilder
                .fromUriString(homepage)
                .path("/robots.txt")
                .encode()
                .build()
                .toUri();

        try {
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(uri, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException exception) {
            log.info("robots.txt 파싱 중 에러 발생 : {}", exception.getMessage());
            return null;
        }
    }

    private JSONArray processValidatorResult(String s) throws ParseException {
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(s);
        return (JSONArray) jsonObject.get("messages");
    }

    // 입력 homepage의 도메인을 추출하여 반환
    private static String parseHomepage(String homepage) {
        Pattern urlPattern = Pattern.compile("^(https?):\\/\\/([^:\\/\\s]+)");
        Matcher m = urlPattern.matcher(homepage);
        if (m.find()) {
            return m.group(1) + "://" + m.group(2);
        }
        return "";
    }

    private JSONObject parseJson(String fileName) throws IOException, ParseException {
        log.info("json Name : {}", fileName);

        String json = outputPath + fileName + "_output.json";
        JSONObject jsonObject = (JSONObject) new JSONParser().parse(new FileReader(json));
        removeJson(json);

        return jsonObject;
    }

    private String parseRobot(String[] robotContent) {
        if (robotContent.length == 1) {
            robotContent = robotContent[0].split("\n");
        }
        return makeArray(robotContent).toJSONString();
    }

    private JSONArray makeArray(String[] robots) {
        JSONArray jsonArray = new JSONArray();
        for (String robot : robots) {
            if (Objects.equals(robot, "") || robot.charAt(0) == '#') {
                continue;
            }
            // : 기준으로 split
            String type = robot.split(":")[0];
            String value = robot.substring(type.length() + 1).replace(" ", "");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", type);
            jsonObject.put("value", value);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    // 진단 요청 날짜가 null인지 확인
    private void checkDateValidation(LocalDate date) {
        log.info("진단 요청 날짜({})에 대한 유효성 확인", date);

        if (date == null) {
            throw new DateTimeException("진단 요청날짜가 없습니다.");
        }

        log.info("진단 요청 날짜 유효성 확인 완료");
    }

    // 진단 요청 날짜가 이미 진단된 결과의 날짜보다 앞인지 확인
    private void checkDateValidation(LocalDate requestedDate, LocalDate recentRequestDate) {
        if (recentRequestDate == null) {
            return;
        }
        if (requestedDate.isBefore(recentRequestDate)) {
            throw new DateTimeException("요청날짜가 저장된 날짜보다 먼저일 수 없습니다.");
        }
    }

    // 맨 뒤 '/' 제거
    private String removeSlash(String url) {
        StringBuilder tmp = new StringBuilder(url);

        if (tmp.length() == 0) {
            throw new StringIndexOutOfBoundsException("문자열이 없습니다.");
        }
        if (tmp.charAt(tmp.length() - 1) == '/') {
            tmp.deleteCharAt(tmp.length() - 1);
        }

        return tmp.toString();
    }

    private void removeJson(String json) throws IOException {
        File file = new File(json);

        if (!file.exists()) {
            log.info("lighthouse json 파일을 찾을 수 없거나, 삭제할 수 없음");
            return;
        }

        Files.delete(Path.of(json));
        log.info("lighthouse json 파일 삭제");
    }

    // 두 날짜의 기간 차이를 달 기준으로 비교하여 반환하는 함수
    private boolean isinMonth(LocalDate requestedDate, LocalDate recentRequestDate) {
        // 기간 차이가 1달 이내라면 true 반환
        Period period = Period.between(recentRequestDate, requestedDate);
        return period.getMonths() == 0;
    }

    private void initPath() {
        String filePath = env.getProperty("filePath");
        utilPath = filePath + env.getProperty("utilPath");
        vnuCommand = "java -jar " + filePath + env.getProperty("vnuPath") + " --format json ";
        outputPath = filePath + env.getProperty("outputPath");
    }
}