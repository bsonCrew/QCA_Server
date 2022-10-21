package com.example.QCA.QualityControlAutomation.home;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
// Spring REST docs 자동 설정을 위함
@AutoConfigureRestDocs
@SpringBootTest
class ControlControllerTest {

    // MockMvc 객체 생성
    @Autowired
    private MockMvc mockMvc;

    @DisplayName("최근 진단한 URL 리스트 반환")
    @Test
    void listTest() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        // 요청에 대한 정보 입력
                        .get("/api/list")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                // 응답이 ok인지 테스트
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "list-get",
                            responseFields(
                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("반환 메세지"),
                                    fieldWithPath("data").type(JsonFieldType.ARRAY).description("결과"),
                                    fieldWithPath("data[].label").type(JsonFieldType.STRING).description("웹사이트 명").optional(),
                                    fieldWithPath("data[].homepage").type(JsonFieldType.STRING).description("웹사이트 주소").optional(),
                                    fieldWithPath("data[].audits").type(JsonFieldType.STRING).description("웹사이트에 대한 lighthouse 검사 결과").optional(),
                                    fieldWithPath("data[].validator").type(JsonFieldType.STRING).description("W3C validator 결과").optional(),
                                    fieldWithPath("data[].robot").type(JsonFieldType.STRING).description("robots.txt 파싱 결과").optional(),
                                    fieldWithPath("data[].recentRequestedDate").type(JsonFieldType.STRING).description("최근 검사 요청 날짜").optional()
                                )
                    )
                )
        ;
    }

    @DisplayName("URL 진단 요청 - requestNewVal이 true인 경우")
    @Test
    @Transactional
    void controlTestWithTrue() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/api/control")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"url\": \"https://www.gokams.or.kr\", \n\"requestedDate\": \"" + LocalDate.now() + "\", \n\"requestNewVal\": \"" + true + "}")
        )
                .andExpect(status().isOk())
                .andDo(
                        document("control-post",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("url").description("검사 요청 주소"),
                                        fieldWithPath("requestedDate").description("검사 요청 날짜")
                                ),
                                responseFields(
                                    fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("반환 메세지"),
                                    fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과"),
                                    fieldWithPath("data.label").type(JsonFieldType.STRING).description("웹사이트 명"),
                                    fieldWithPath("data.homepage").type(JsonFieldType.STRING).description("웹사이트 주소"),
                                    fieldWithPath("data.audits").type(JsonFieldType.STRING).description("웹사이트에 대한 lighthouse 검사 결과"),
                                    fieldWithPath("data.validator").type(JsonFieldType.STRING).description("W3C validator 결과"),
                                    fieldWithPath("data.robot").type(JsonFieldType.STRING).description("robots.txt 파싱 결과"),
                                    fieldWithPath("data.recentRequestedDate").type(JsonFieldType.STRING).description("최근 검사 요청 날짜")
                            )
                        )
        );
    }

    @DisplayName("URL 진단 요청 - requestNewVal이 false인 경우")
    @Test
    @Transactional
    void controlTestWithFalse() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/control")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"url\": \"https://www.gokams.or.kr\", \n\"requestedDate\": \"" + LocalDate.now() + "\", \n\"requestNewVal\": \"" + false + "}")
                )
                .andExpect(status().isOk())
                .andDo(
                        document("control-post",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("url").description("검사 요청 주소"),
                                        fieldWithPath("requestedDate").description("검사 요청 날짜")
                                ),
                                responseFields(
                                        fieldWithPath("status").type(JsonFieldType.NUMBER).description("상태 코드"),
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("반환 메세지"),
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("결과"),
                                        fieldWithPath("data.label").type(JsonFieldType.STRING).description("웹사이트 명"),
                                        fieldWithPath("data.homepage").type(JsonFieldType.STRING).description("웹사이트 주소"),
                                        fieldWithPath("data.audits").type(JsonFieldType.STRING).description("웹사이트에 대한 lighthouse 검사 결과"),
                                        fieldWithPath("data.validator").type(JsonFieldType.STRING).description("W3C validator 결과"),
                                        fieldWithPath("data.robot").type(JsonFieldType.STRING).description("robots.txt 파싱 결과"),
                                        fieldWithPath("data.recentRequestedDate").type(JsonFieldType.STRING).description("최근 검사 요청 날짜")
                                )
                        )
                );
    }
}
