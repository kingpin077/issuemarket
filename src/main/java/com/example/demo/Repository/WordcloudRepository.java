package com.example.demo.Repository;

import com.example.demo.Entity.Serch_infoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WordcloudRepository extends JpaRepository<Serch_infoEntity, Long> {
    //전체사용자 데이터
    @Query("SELECT f.keyword, COUNT(f.keyword) AS count FROM Serch_infoEntity f GROUP BY f.keyword ORDER BY count(f.keyword) DESC")
    List<Object[]> findKeywordCount();

    //로그인 사용자 데이터
    @Query("SELECT f.keyword, COUNT(f.keyword) AS count FROM Serch_infoEntity f WHERE f.userId <> 'anonymousUser'  GROUP BY f.keyword ORDER BY count(f.keyword) DESC")
    List<Object[]> loginuser();

    //여성사용자 데이터
    @Query("SELECT f.keyword, COUNT(f.keyword) AS count FROM Serch_infoEntity f WHERE f.gender = false  GROUP BY f.keyword ORDER BY count(f.keyword) DESC")
    List<Object[]> femaleuser();

    //남성사용자 데이터
    @Query("SELECT f.keyword, COUNT(f.keyword) AS count FROM Serch_infoEntity f WHERE f.gender = true  GROUP BY f.keyword ORDER BY count(f.keyword) DESC")
    List<Object[]> maleuser();
}
