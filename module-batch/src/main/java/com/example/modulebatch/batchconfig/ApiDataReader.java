package com.example.modulebatch.batchconfig;

import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.exception.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiDataReader implements ItemStreamReader<String> {

    /*
    BatchConfig 설정에 들어가는 클래스
    api 요청후 응답데이터를 받아오는 Reader 클래스 null 을 리턴하면 요청종료
    */

    // 환경변수 설정값이 없으면 빈 문자열을 주입
    @Value("${ITEM_API_KEY:}")
    private String apiKey;

    @Value("${API_PORT:}")
    private String apiPort;

    @Value("${API_URL:}")
    private String apiUrl;

    private Queue<String> jsonQueue = new LinkedList<>();
    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    // 매 실행시 요청 파라미터 초기화
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {

        int totalPage = getTotalPage(); // 총 페이지 수 설정
        int pageSize = 1000; // 페이지 범위 사이즈

        // api 설정이 주입되지않으면 예외반환
        if (!apiKey.isBlank() && !apiPort.isBlank() && !apiUrl.isBlank()) {
            /**
             * Flux.range(0, (totalPage + pageSize - 1) / pageSize)
             * Flux 는 Reactor 함수로 매개 변수인 0 과 [(totalPage + pageSize - 1) / pageSize] 사이의 범위의 스트림을 생성
             * 예) Flux.range(1,5) 의 경우 1~5 범위의 스트림이 생성됨
             * 아래 코드의 경우 페이지범위의 스트림이 생성되어 각 생성된 페이지 범위의 요청을 비동기방식으로 요청함
             */
            Flux<String> jsonData = Flux.range(0, (totalPage + pageSize - 1) / pageSize)
                    .flatMap(page -> {
                        int startIndex = page * pageSize + 1;
                        int endIndex = startIndex + pageSize - 1;

                        // 페이지 범위만큼 api 요청
                        return webClient.get()
                                .uri(uriBuilder -> uriBuilder
                                        .path("/" + startIndex)
                                        .path("/" + endIndex)
                                        .build())
                                .retrieve()
                                .bodyToMono(String.class);
                    });
            // 받아온 데이터가 없을경우 예외반환
            try {
                jsonQueue = new LinkedList<>(Objects.requireNonNull(jsonData.collectList().block()));
            } catch (NullPointerException e) {
                throw new CustomException(ErrorCode.API_CONNECTION_ERROR);
            }
        } else {
            throw new CustomException(ErrorCode.API_CONFIGURATION_NOT_FOUND);
        }
    }

    /**
     * 응답데이터를 프로세스 영역에 전달
     * @return Json 응답데이터
     */
    @Override
    public String read() {

        // 반환할 데이터가 없으면 중단
        if (jsonQueue.isEmpty()) {
            return null;
        }

        return jsonQueue.poll();
    }

    // 총페이지를 구하기 위해 첫페이지 api 요청
    private int getTotalPage() {

        int totalPage;

        // 첫 페이지만 요청
        String jsonData = webClient.get()
                .uri("/1/1")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (StringUtils.isBlank(jsonData)) {
            throw new CustomException(ErrorCode.API_CONNECTION_ERROR);
        }

        // 응답데이터에서 총 페이지수 추출
        try {
            JsonNode nodeTemp = objectMapper.readTree(jsonData);
            totalPage = nodeTemp.path(apiUrl).path("totalCnt").asInt();
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.API_PARSE_ERROR);
        }
        return totalPage;
    }

}
