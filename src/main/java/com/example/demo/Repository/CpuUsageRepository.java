package com.example.demo.Repository;

import com.example.demo.DTO.CpuUsageData;
import com.example.demo.Entity.CpuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CpuUsageRepository extends JpaRepository<CpuEntity, Long> {
    // 최근 18개의 데이터를 반환 (시간순으로 정렬)
    @Query("SELECT c FROM CpuEntity c ORDER BY c.timestamp DESC")
    List<CpuEntity> findTop18ByOrderByTimestampDesc();
}