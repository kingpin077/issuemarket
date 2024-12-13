package com.example.demo.Service;

import com.sun.management.OperatingSystemMXBean;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;

@Service
public class ChartService {
    public double getCpuUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        return osBean.getSystemCpuLoad() * 100;
}

    public double getMemoryUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        // 전체 메모리와 사용 중인 메모리 가져오기
        long totalMemory = osBean.getTotalPhysicalMemorySize(); // 전체 메모리 (bytes)
        long freeMemory = osBean.getFreePhysicalMemorySize();   // 사용 가능한 메모리 (bytes)
        long usedMemory = totalMemory - freeMemory;            // 사용 중인 메모리 (bytes)


        // 사용률 계산
        return ((double) usedMemory / totalMemory) * 100;      // 메모리 사용률 (%)
    }

}
