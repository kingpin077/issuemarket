package com.example.demo.Repository;

import com.example.demo.Entity.TestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
    // 새로운 페이징 메서드
    @Query("SELECT t FROM TestEntity t ORDER BY t.pc DESC")
    Page<TestEntity> findAllByOrderByPcDesc(Pageable pageable);

    @Query("SELECT t FROM TestEntity t ORDER BY t.total DESC")
    Page<TestEntity> findAllByOrderByTotalDesc(Pageable pageable);

    // 태그별 페이징 조회
    @Query("SELECT t FROM TestEntity t WHERE t.tag = :tag ORDER BY t.total DESC")
    Page<TestEntity> findAllByTagOrderByTotalDesc(String tag, Pageable pageable);



    // pc검색량 순위에 따라서 상위 10개의 데이터를 내림차순으로 가져옴
    List<TestEntity> findTop10ByOrderByPcDesc();

    //총 검색량 순위에 따라서 상위 10개의 데이터를 내림차순으로 가져옴
    List<TestEntity> findTop10ByOrderByTotalDesc();

    // 총 검색량 순위에 따라 상위 50개의 데이터를 내림차순으로 가져옴
    List<TestEntity> findTop50ByOrderByTotalDesc();

    // keyword와 total을 가져와 total 내림차순으로 정렬
    @Query("SELECT t.keyword, t.total FROM TestEntity t ORDER BY t.total DESC")
    List<Object[]> findKeywordsAndTotalByOrderByTotalDesc();

    //태그에 따라서 상위 10개의 데이터를 내림차순으로 가져옴
    List<TestEntity> findTop10ByTagOrderByTotalDesc(String tag);


}
