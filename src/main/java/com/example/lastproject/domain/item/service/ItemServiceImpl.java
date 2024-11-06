package com.example.lastproject.domain.item.service;

import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.exception.CustomException;
import com.example.lastproject.domain.item.dto.response.ItemResponse;
import com.example.lastproject.domain.item.entity.Item;
import com.example.lastproject.domain.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;
    private final ItemRepository itemRepository;

    /**
     * @param keyword 검색할 키워드
     * @return 조회결과
     */
    public List<ItemResponse> searchItems(String keyword) {

        List<ItemResponse> results = itemRepository.searchItemsByKeywordInCategory(keyword);
        // 조회되는 품목이 없으면 예외문 반환
        if (results.isEmpty()) {
            throw new CustomException(ErrorCode.ITEM_NOT_FOUND);
        }
        return results;
    }

    // 트랜젝션 비활성화
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void getItemFromOpenApi() {

        // 오픈 API 요청후 응답받은 데이터를 item 엔티티에 저장
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(jobRegistry.getJob("itemApiJob"), jobParameters);
        } catch (NoSuchJobException | JobExecutionAlreadyRunningException | JobRestartException |
                 JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            throw new CustomException(ErrorCode.JOB_EXECUTION_ERROR);
        }
    }

    // 재사용 잦은 코드 메서드 분리
    public Item validateItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new CustomException(ErrorCode.ITEM_NOT_FOUND));
    }

}
