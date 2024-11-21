package com.example.modulebatch.batchconfig;

import com.example.lastproject.common.enums.ErrorCode;
import com.example.lastproject.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemBatchScheduleConfig {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;

    /**
     * 매주 월요일 오전 4시에 품목업데이트 실행
     */
    @Scheduled(cron = "0 0 4 * * MON", zone = "Asia/Seoul")
    public void runItemApiJob() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = dateFormat.format(new Date());

        JobParameters jobParameters = new JobParametersBuilder().addString("date", date).toJobParameters();

        log.info("{} API 요청 실행", date);

        try {
            jobLauncher.run(jobRegistry.getJob("itemApiJob"), jobParameters);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.JOB_EXECUTION_ERROR);
        }
    }

    /**
     * 서버 준비가 완료되면 품목업데이트 실행
     */
    @EventListener(ApplicationReadyEvent.class)
    private void init() {
        runItemApiJob();
    }
}
