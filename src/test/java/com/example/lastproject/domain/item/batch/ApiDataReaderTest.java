package com.example.lastproject.domain.item.batch;

import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.config.ApiDataReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApiDataReaderTest {

    @Mock
    WebClient webClient;

    @Mock
    ObjectMapper objectMapper;

    @Spy
    ApiDataReader apiDataReader = Mockito.spy(new ApiDataReader(objectMapper, webClient));

    MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // 가짜 api 요청 URL
        webClient = WebClient.builder()
                // 기본 url 설정
                .baseUrl(mockWebServer.url("/").toString())
                .build();
    }

    /**
     * api 요청시 총페이지수에 맞게 요청이 반복되는지 테스트
     */
    @Test
    @DisplayName("read 메서드 호출회수 테스트")
    void api요청이_예상수만큼_요청되는지_확인한다() {

        // given
        String jsonData = "{ \"result\": \"success\" }"; // 요청 응답값 주입
        Flux<String> fluxString = Flux.just(jsonData); //
        List<String> listString = fluxString.collectList().block();
        Queue<String> jsonQueue = new LinkedList<>(List.of(jsonData, jsonData));
        ReflectionTestUtils.setField(apiDataReader, "jsonQueue", jsonQueue);

        // 가짜서버에 api 요청
        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonData)
                .addHeader("Content-Type", "application/json"));

        // when
        while (apiDataReader.read() != null) {
        }  // 반환할 값이 없을때까지 메서드 호출

        // then
        verify(apiDataReader, times(3)).read();
    }

    @Test
    @DisplayName("제이슨 반환테스트")
    void 제이슨_데이터를_정상적으로_반환한다() {

        // given
        String jsonData = "{ \"result\": \"success\" }";
        Queue<String> jsonQueue = new LinkedList<>(List.of(jsonData, jsonData));
        ReflectionTestUtils.setField(apiDataReader, "jsonQueue", jsonQueue);

        //when
        String result = apiDataReader.read();

        //then
        assertEquals(jsonData, result);
    }

    @Test
    @DisplayName("api 설정 주입 테스트")
    void API_설정이_주입되지_않으면_예외를_반환한다() {

        // given
        String jsonData = "{ \"result\": \"success\" }";
        Queue<String> jsonQueue = new LinkedList<>(List.of(jsonData, jsonData));
        ReflectionTestUtils.setField(apiDataReader, "jsonQueue", jsonQueue);
        ReflectionTestUtils.setField(apiDataReader, "apiKey", "");
        ReflectionTestUtils.setField(apiDataReader, "apiPort", "");
        ReflectionTestUtils.setField(apiDataReader, "apiUrl", "");

        // 가짜서버에 api 요청
        mockWebServer.enqueue(new MockResponse()
                .setBody(jsonData)
                .addHeader("Content-Type", "application/json"));

        // when
        CustomException exception = assertThrows(CustomException.class, () -> apiDataReader.open(new ExecutionContext()));

        // then
        assertEquals("API 환경설정 값을 찾을 수 없습니다.", exception.getMessage());
    }

}

