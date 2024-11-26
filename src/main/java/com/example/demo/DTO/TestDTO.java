package com.example.demo.DTO;

import com.example.demo.Entity.TestEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TestDTO {
    private Long no;
    private String keyword;
    private int mobile;
    private int pc;
    private int total;

    // 추가된 필드
    private String articleUrl;   // 기사 URL
    private String articleTitle; // 기사 제목
    private List<Map<String, Object>> ratioResults= new ArrayList<>(); // ratioResults 추가

    // Entity -> DTO 변환 메서드
    public static TestDTO toTestDTO(TestEntity testEntity) {
        return TestDTO.builder()
                .no(testEntity.getNo())
                .keyword(testEntity.getKeyword())
                .mobile(testEntity.getMobile())
                .pc(testEntity.getPc())
                .total(testEntity.getTotal())
                .build();
    }
}
