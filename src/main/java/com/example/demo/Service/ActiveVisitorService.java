//IP 기반 방문자 수를 관리하는 서비스 클래스

package com.example.demo.Service;

import com.example.demo.Entity.DailyVisitor;
import com.example.demo.Repository.DailyVisitorRepository;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ActiveVisitorService {
    private final Set<String> allVisitors = ConcurrentHashMap.newKeySet(); // 누적 방문자
    private final Map<String, LocalDateTime> activeVisitors = new ConcurrentHashMap<>(); // 실시간 방문자 (IP와 마지막 활동 시간)
    private final DailyVisitorRepository dailyVisitorRepository;

    public ActiveVisitorService(DailyVisitorRepository dailyVisitorRepository) {
        this.dailyVisitorRepository = dailyVisitorRepository;
    }

    private static final int TIMEOUT_MINUTES = 5; // 비활동 시간 제한 (5분)

    // 방문자 추적
    public void trackVisitor(HttpServletRequest request) {
        String clientIp = getClientIp(request);
        allVisitors.add(clientIp); // 누적 방문자 추적
        activeVisitors.put(clientIp, LocalDateTime.now()); // 실시간 방문자 활동 시간 갱신
    }

    // 누적 방문자 수 반환
    public int getTotalVisitorCount() {
        return allVisitors.size();
    }

    // 현재 날짜의 누적 방문자 수를 DB에 저장
    public void saveDailyVisitors() {
        LocalDate today = LocalDate.now();
        int totalVisitors = getTotalVisitorCount();

        // 기존에 오늘 날짜 데이터가 있다면 업데이트, 없으면 새로 삽입
        DailyVisitor dailyVisitor = dailyVisitorRepository.findByDate(today)
                .orElse(new DailyVisitor(today, 0));
        dailyVisitor.setTotalVisitors(totalVisitors);
        dailyVisitorRepository.save(dailyVisitor);

        // 새 날짜가 시작되었으므로 메모리 누적 방문자 초기화
        allVisitors.clear();
    }

    // 실시간 방문자 수 반환
    public int getActiveVisitorCount() {
        cleanupInactiveVisitors(); // 비활동 방문자 정리
        return activeVisitors.size();
    }

    // 비활동 방문자 제거
    private void cleanupInactiveVisitors() {
        LocalDateTime now = LocalDateTime.now();
        activeVisitors.entrySet().removeIf(entry ->
                entry.getValue().isBefore(now.minusMinutes(TIMEOUT_MINUTES))
        );
    }

    // 클라이언트 IP 추출
    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null) {
            return forwarded.split(",")[0];
        }
        return request.getRemoteAddr();
    }

    // 주기적으로 비활동 방문자 정리
    @PostConstruct
    public void startCleanupTask() {
        Thread cleanupThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(60000); // 1분마다 실행
                    cleanupInactiveVisitors();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        cleanupThread.setDaemon(true);
        cleanupThread.start();
    }

    // 매일 자정에 방문자 수를 저장
    @PostConstruct
    public void scheduleDailySaveTask() {
        Thread dailySaveThread = new Thread(() -> {
            while (true) {
                try {
                    // 자정까지 남은 시간 계산
                    long millisUntilMidnight = calculateMillisUntilMidnight();
                    Thread.sleep(millisUntilMidnight);

                    // 누적 방문자 수 저장
                    saveDailyVisitors();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        dailySaveThread.setDaemon(true);
        dailySaveThread.start();
    }

    // 자정까지 남은 시간 계산
    private long calculateMillisUntilMidnight() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        long millisUntilMidnight = tomorrow.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                - System.currentTimeMillis();
        return millisUntilMidnight;
    }
}