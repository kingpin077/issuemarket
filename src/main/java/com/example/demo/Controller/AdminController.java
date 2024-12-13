/** 관리자 페이지 조작을 위한 Controller **/

package com.example.demo.Controller;

import com.example.demo.DTO.CpuUsageData;
import com.example.demo.Entity.CpuEntity;
import com.example.demo.Entity.DailyVisitor;
import com.example.demo.Entity.UserEntity;
import com.example.demo.Repository.CpuUsageRepository;
import com.example.demo.Repository.DailyVisitorRepository;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.ActiveVisitorService;
import com.example.demo.Service.AdminService;
import com.example.demo.Service.ChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RequiredArgsConstructor
@Controller
public class AdminController {

    private final AdminService adminService;
    private final CpuUsageRepository cpuUsageRepository;
    private final ChartService chartService;
    private final ActiveVisitorService activeVisitorService;
    private final DailyVisitorRepository dailyVisitorRepository;

    @GetMapping("/admin")
    public String admin_page(Model model) {
        // 사용자 목록을 가져와서 모델에 추가
        List<UserEntity> userList = adminService.getAllUsers(); // 사용자 목록을 가져오는 메서드 호출
        model.addAttribute("users", userList); // 모델에 사용자 목록 추가
        return "admin"; // admin.html을 반환
    }

    // 사용자 목록 페이지
    @GetMapping("/admin/users") // 새로운 메소드 추가
    public String userList(Model model) {
        List<UserEntity> users = adminService.getAllUsers(); // 모든 사용자 목록 가져오기
        model.addAttribute("users", users); // 모델에 사용자 목록 추가
        return "user_list"; // user_list.html을 반환
    }

    @GetMapping("/admin/chart")
    public String chart_page() {
        return "chart"; // chart.html을 반환
    }

    // cpu 사용률 조회
    @GetMapping("/admin/cpu-usage-latest")
    @ResponseBody
    public List<CpuUsageData> getLatestCpuUsageData() {
        // 데이터베이스에서 최근 18개의 CpuEntity를 가져옴
        List<CpuEntity> entityList = cpuUsageRepository.findTop18ByOrderByTimestampDesc();

        // CpuEntity를 CpuUsageData로 변환
        return entityList.stream()
                .map(entity -> {
                    CpuUsageData dto = new CpuUsageData();
                    dto.setTimestamp(entity.getTimestamp());
                    dto.setCpuUsage(entity.getCpuUsage());
                    return dto;
                })
                .sorted(Comparator.comparing(CpuUsageData::getTimestamp)) // 시간순으로 정렬
                .toList();
    }

    @GetMapping("/admin/cpu-usage")
    @ResponseBody
    public Map<String, Object> getCurrentCpuUsage() {
        double cpuUsage = chartService.getCpuUsage(); // ChartService에서 CPU 사용률 가져오기
        // 현재 CPU 사용률과 현재 시간 반환
        Map<String, Object> response = new HashMap<>();
        response.put("currentUsage", cpuUsage);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping("/admin/active-visitors")
    @ResponseBody
    public Map<String, Integer> getVisitorStats(HttpServletRequest request) {
        activeVisitorService.trackVisitor(request); // 방문자 추적
        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalVisitors", activeVisitorService.getTotalVisitorCount());
        stats.put("activeVisitors", activeVisitorService.getActiveVisitorCount());
        return stats;
    }

    @GetMapping("/admin/memory-usage")
    @ResponseBody
    public Map<String, Object> getMemoryUsage() {
        double memoryUsage = chartService.getMemoryUsage();

        // 메모리 사용률 응답
        Map<String, Object> response = new HashMap<>();
        response.put("memoryUsage", memoryUsage);
        return response;
    }

    @GetMapping("/admin/daily-visitors")
    @ResponseBody
    public List<Map<String, Object>> getDailyVisitors() {
        List<Map<String, Object>> dailyVisitors = new ArrayList<>();
        // 데이터를 생성하는 로직
        List<DailyVisitor> stats = dailyVisitorRepository.findAll();

        for (DailyVisitor stat : stats) {
            Map<String, Object> map = new HashMap<>();
            map.put("date", stat.getDate()); // 날짜
            map.put("totalVisitors", stat.getTotalVisitors()); // 방문자 수
            dailyVisitors.add(map);
        }

        return dailyVisitors;
    }
}
