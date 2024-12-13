package com.example.demo.Entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "daily_visitors")
@NoArgsConstructor
public class DailyVisitor {
    @Id
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "total_visitors")
    private int totalVisitors;

    public DailyVisitor(LocalDate date, int totalVisitors) {
        this.date = date;
        this.totalVisitors = totalVisitors;
    }
}