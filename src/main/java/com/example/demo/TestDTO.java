package com.example.demo;

public class TestDTO {
    private Long no;
    private String keyword;
    private int mobile;
    private int pc;
    private int total;

    // 생성자, 게터, 세터 등 필요한 메서드 추가
    public TestDTO() {
        // 기본 생성자
    }

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

    public int getMobile() {
        return mobile;
    }

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    // toString 등 필요한 메서드 추가
    @Override
    public String toString() {
        return "TestDTO{" +
                "no=" + no +
                ", keyword='" + keyword + '\'' +
                ", mobile=" + mobile +
                ", pc=" + pc +
                ", total=" + total +
                '}';
    }
}