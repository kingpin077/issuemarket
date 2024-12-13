package com.example.demo.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CpuUsageData {
    private LocalDateTime timestamp;
    private double cpuUsage;

    public CpuUsageData(LocalDateTime timestamp, double cpuUsage) {
        this.timestamp = timestamp;
        this.cpuUsage = cpuUsage;
    }
}
