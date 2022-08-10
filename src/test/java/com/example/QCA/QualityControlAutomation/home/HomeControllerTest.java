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
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@AutoConfigureMockMvc
// Spring REST docs 자동 설정을 위함
@AutoConfigureRestDocs
class HomeControllerTest {

    // MockMvc 객체 생성
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Top 5 리스트 반환 테스트")
    @Test
    void listTest() throws Exception {
        mockMvc.perform(
                // 요청에 대한 정보 입력
                get("/api/list")
        )
                // 응답이 ok인지 테스트
                .andExpect(status().isOk())
                // 응답값 출력
                .andDo(print());
    }

    @DisplayName("검사할 url 요청 테스트")
    @Test
    void controlTest() throws Exception {
        String url = "www.naver.com";
        ControlRequest controlRequest = new ControlRequest();
        controlRequest.setUrl(url);

        mockMvc.perform(
                post("/api/control")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(controlRequest))
        )
                .andExpect(status().isOk())
                .andDo(print());
    }
}
