package com.example.demo.Repository;

import com.example.demo.Entity.DailyVisitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailyVisitorRepository extends JpaRepository<DailyVisitor, LocalDate> {
    Optional<DailyVisitor> findByDate(LocalDate date);
}