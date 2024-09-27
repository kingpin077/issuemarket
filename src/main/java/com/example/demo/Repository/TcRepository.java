package com.example.demo.Repository;

import com.example.demo.Entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TcRepository extends JpaRepository<TestEntity, Long> {
}