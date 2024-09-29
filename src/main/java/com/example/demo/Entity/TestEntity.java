package com.example.demo.Entity;

import javax.persistence.*;

import com.example.demo.DTO.TestDTO;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter @Setter
@Builder
@Table(name = "csvjson")
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    private String keyword;
    private int pc;
    private int mobile;
    private int total;
    private String tag;

    // DTO를 Entity로 변환하는 메서드
    public static TestEntity toTestEntity(TestDTO testDTO) {
        return TestEntity.builder()
                .keyword(testDTO.getKeyword())
                .pc(testDTO.getPc())
                .mobile(testDTO.getMobile())
                .total(testDTO.getTotal())
                .build();
    }
}
