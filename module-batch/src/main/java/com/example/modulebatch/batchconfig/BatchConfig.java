package com.example.modulebatch.batchconfig;

import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.domain.item.entity.Item;
import com.example.lastproject.domain.item.repository.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    /**
     * Job => 전체 배치처리 과정을 추상화한 클래스 : 배치의 실행 단위로 전체 배치 작업을 정의
     * Step => Job 의 세부 실행 단위, Job 은 여러개의 Step 을 가질수 있음
     * 각 Step 은 Reader, Processor, Writer 로 이루어져 있음
     * Reader : 외부 데이터(파일, DB, API)를 읽어오며 읽어온 데이터를 Processor 에 전달
     * Processor : 읽어온 데이터를 가공하거나 변환후 Writer 에 전달
     * Writer : 처리된 데이터를 DB에 저장함
     * Batch 의 로직 흐름[청크기반] : JobLauncher => Job => Step(Reader => Processor => Writer)
     * 청크 기반의 배치처리의 장점 : 데이터를 청크단위로 묶어서 처리하여 최적화가능, 대용량 데이터에도 안정적인 처리가능
     */

    @Value("${API_URL:}")
    private String apiUrl;

    private final JobRepository jobRepository;
    private final ItemRepository itemRepository;
    private final ApiDataReader apiDataReader;
    private final PlatformTransactionManager platformTransactionManager;
    // 중복 데이터 검증시 사용되는 데이터
    private final Set<String> itemCache = new HashSet<>();

    // Job 설정
    @Bean
    public Job itemApiJob() {

        return new JobBuilder("itemApiJob", jobRepository)
                // api 요청후 데이터 파싱을 거쳐 DB에 저장해주는 Step
                .start(itemApiStep())
                // 중복 데이터 검증에 사용되는 Hash set 에 업데이트해주는 Step
                .next(checkItemCacheEmpty())
                .build();
    }

    // Step 설정
    @Bean
    public Step itemApiStep() {

        return new StepBuilder("itemApiStep", jobRepository)
                /**
                 * String 리더클래스 반환타입, List<Item> 프로세싱클래스 반환타입
                 * platformTransactionManager => 청크진행시 실패했을때 다시처리할수있도록 세팅
                 */
                .<String, List<Item>>chunk(1, platformTransactionManager)
                // api 요청 리더 클래스 주입
                .reader(synchronizedApiDataReader())
                // 응답 데이터 엔티티 파싱 클래스 주입
                .processor(jsonToEntityProcessor())
                // 엔티티 저장 라이터 클래스 주입
                .writer(itemWriter())
                // 병렬처리 설정 주입
                .taskExecutor(taskExecutor())
                .build();
    }

    /**
     * 병렬처리로 부터 데이터 일관성을 지키기 위해 첫 업데이트의 경우 캐시 업데이트는 별도의 Step 으로 분리함
     *
     * @return itemApiStep 다음으로 실행되는 Step 클래스
     */
    @Bean
    public Step checkItemCacheEmpty() {

        return new StepBuilder("checkItemCache", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    if (itemCache.isEmpty()) {
                        itemCache.addAll(itemRepository.findAllByProductNames());
                    }
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }


    /**
     * ApiDataReader 클래스를 SynchronizedItemStreamReader 클래스로 감싸 데이터의 일관성을 보장해줌
     * ApiDataReader 클래스의 read() 메서드에 하나의 쓰레드만 접근할 수 있게 해주는 빈 클래스
     *
     * @return ApiDataReader 클래스를 SynchronizedItemStreamReader 클래스로 감싸서 반환
     */
    @Bean
    public ItemReader<String> synchronizedApiDataReader() {
        SynchronizedItemStreamReader<String> synchronizedItemReader = new SynchronizedItemStreamReader<>();
        synchronizedItemReader.setDelegate((ItemStreamReader<String>) apiDataReader);
        return synchronizedItemReader;
    }

    // Json 응답데이터를 엔티티로 파싱하는 Processor 클래스
    @Bean
    public ItemProcessor<String, List<Item>> jsonToEntityProcessor() {

        return new ItemProcessor<String, List<Item>>() {
            @Override
            public List<Item> process(String jsonData) throws Exception {

                try {
                    log.info("데이터 파싱 시작");
                    // 데이터 파싱을 위한 제이슨 객체 생성
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonResponse = (JSONObject) jsonParser.parse(jsonData);

                    // 제이슨 응답데이터를 제이슨 리스트로 파싱
                    JSONObject jsonBody = (JSONObject) jsonResponse.get(apiUrl);
                    JSONArray jsonRowList = (JSONArray) jsonBody.get("row");

                    // 파싱한 데이터를 담을 리스트
                    List<Item> items = new ArrayList<>();

                    // 제이슨리스트에서 Item 엔티티객체로 파싱
                    for (Object row : jsonRowList) {
                        JSONObject jsonItem = (JSONObject) row;
                        // 품목분류명 추출
                        String category = (String) jsonItem.get("STD_PRDLST_NM");
                        // 품목명 추출
                        String productName = (String) jsonItem.get("STD_SPCIES_NM");

                        // 저장된 Hash set 데이터로 중복 검사
                        if (itemCache.isEmpty()) {
                            Item item = new Item(category, productName);
                            items.add(item);
                        } else if (!itemCache.contains(productName)) {
                            Item item = new Item(category, productName);
                            items.add(item);
                            itemCache.add(productName); // Hash set 에 저장
                        }
                    }
                    log.info("데이터 파싱 종료");

                    return items;
                } catch (ParseException e) {
                    throw new CustomException(ErrorCode.API_PARSE_ERROR);
                }
            }
        };
    }

    // 데이터 파싱한 엔티티를 저장하는 Writer 메서드
    @Bean
    public ItemWriter<List<Item>> itemWriter() {

        return new ItemWriter<List<Item>>() {
            @Override
            public void write(Chunk<? extends List<Item>> chunk) throws Exception {
                for (List<Item> items : chunk) {
                    itemRepository.saveAll(items);
                }
            }
        };
    }

    // 병렬처리 설정
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 최소 스레드수
        executor.setCorePoolSize(8);
        // 최대 스레드수
        executor.setMaxPoolSize(10);
        // 대기 큐 용량
        executor.setQueueCapacity(100);
        executor.initialize();
        return executor;
    }

    // 서버 시작시 기존 데이터베이스에서 데이터를 가져와 Hash set 에 저장
    @PostConstruct
    public void loadDataToCache() {
        itemCache.addAll(itemRepository.findAllByProductNames());
    }

}
