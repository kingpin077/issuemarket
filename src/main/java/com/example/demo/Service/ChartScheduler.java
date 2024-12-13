package com.example.demo.Service;

import com.example.demo.Entity.CpuEntity;
import com.example.demo.Repository.CpuUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ChartScheduler {
    private final ChartService chartService; // CPU 사용률 가져오는 서비스
    private final CpuUsageRepository cpuUsageRepository; // 데이터베이스 레포지토리

    @Scheduled(fixedRate = 600000, initialDelay = 600000) // 10분마다 실행, 시작 후 10분 대기
    public void collectCpuUsage() {
        // ChartService를 통해 현재 CPU 사용률 가져오기
        double cpuUsage = chartService.getCpuUsage();

        // CpuUsageData 객체 생성 후 데이터베이스에 저장
        CpuEntity data = new CpuEntity();
        data.setTimestamp(LocalDateTime.now());
        data.setCpuUsage(cpuUsage);

        cpuUsageRepository.save(data); // 데이터베이스에 저장
    }
}
