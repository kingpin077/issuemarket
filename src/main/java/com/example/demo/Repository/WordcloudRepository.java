package com.example.demo.Repository;

import com.example.demo.Entity.Serch_infoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WordcloudRepository extends JpaRepository<Serch_infoEntity, Long> {
    @Query("SELECT f.keyword, COUNT(f.keyword) AS count FROM Serch_infoEntity f GROUP BY f.keyword ORDER BY count(f.keyword) DESC")
    List<Object[]> findKeywordCount();
}
