package com.example.demo;

import javax.persistence.*;

@Entity
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

    // 생성자
    public TestEntity() {
    }

    public TestEntity(String keyword, int pc, int mobile, int total, String tag) {
        this.keyword = keyword;
        this.pc = pc;
        this.mobile = mobile;
        this.total = total;
        this.tag = tag;
    }

    // Getter 및 Setter 메서드
    public Long getNo() {
        return no;
    }

    public void setNo(Long no) {
        this.no = no;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public int getMobile() {
        return mobile;
    }

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}