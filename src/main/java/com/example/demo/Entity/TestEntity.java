package com.example.demo.Entity;

import javax.persistence.*;
import lombok.*;

// Lombok을 활용하여 Getter, Setter 및 생성자 자동 생성
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
