package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
    // pc 필드를 기준으로 내림차순으로 데이터를 가져오는 메소드
    List<TestEntity> findTop10ByOrderByPcDesc();

    List<TestEntity> findTop10ByOrderByTotalDesc();

    List<TestEntity> findTop10ByTagOrderByTotalDesc(String tag);

}
