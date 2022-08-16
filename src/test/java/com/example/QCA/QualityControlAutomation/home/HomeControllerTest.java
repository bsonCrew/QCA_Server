package com.example.QCA.QualityControlAutomation.home;

import com.example.QCA.QualityControlAutomation.home.dto.ControlRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
// Spring REST docs 자동 설정을 위함
@AutoConfigureRestDocs
class HomeControllerTest {

    // MockMvc 객체 생성
    @Autowired
    private MockMvc mockMvc;

//    @Autowired
//    private ObjectMapper objectMapper;

    @DisplayName("Top 5 리스트 반환 테스트")
    @Test
    void listTest() throws Exception {
        mockMvc.perform(
                // 요청에 대한 정보 입력
                get("/api/list")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                // 응답이 ok인지 테스트
                .andExpect(status().isOk())
                // 응답이 JSON인지 테스트
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(
                        document(
                                "list-get",

                            responseFields(
                                    fieldWithPath("status").description("상태 코드"),
                                    fieldWithPath("message").description("반환 메세지"),
                                    fieldWithPath("data").type(JsonFieldType.ARRAY).description("결과"),
                                    fieldWithPath("data[].label").type(JsonFieldType.STRING).description("웹사이트 명"),
                                    fieldWithPath("data[].homepage").type(JsonFieldType.STRING).description("웹사이트 주소"),
                                    fieldWithPath("data[].totalScore").type(JsonFieldType.NUMBER).description("웹사이트의 결과 총합")
                                )
                    )
                )
        ;
    }

//    @DisplayName("검사할 url 요청 테스트")
//    @Test
//    void controlTest() throws Exception {
//        String url = "www.naver.com";
//        ControlRequest controlRequest = new ControlRequest();
//        controlRequest.setUrl(url);
//
//        mockMvc.perform(
//                post("/api/control")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(controlRequest))
//        )
//                .andExpect(status().isOk())
//                .andDo(
//                        document("control-post",
//                            responseFields(
//                                    fieldWithPath("url").description("검사 요청한 url"),
//                                    fieldWithPath("name").description("검사 요청한 url에 해당하는 웹사이트 이름"),
//                                    fieldWithPath("compatibilityScore").description("웹 호환성 점수"),
//                                    fieldWithPath("accessibilityScore").description("웹 접근성 점수"),
//                                    fieldWithPath("opennessScore").description("웹 개방성 점수"),
//                                    fieldWithPath("opennessScore").description("웹 접속성 점수"),
//                                    fieldWithPath("opennessScore").description("웹 편의성 점수"),
//                                    fieldWithPath("totalScore").description("총 점수")
//                            )
//                        )
//        );
//    }
}
