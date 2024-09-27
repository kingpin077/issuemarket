package com.example.demo.DTO;

public class TestDTO {
    private Long no;
    private String keyword;
    private int mobile;
    private int pc;
    private int total;

    // 추가된 필드
    private String articleUrl;   // 기사 URL
    private String articleTitle; // 기사 제목

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

    // 새로 추가된 필드의 게터와 세터
    public String getArticleUrl() {
        return articleUrl;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
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
                ", articleUrl='" + articleUrl + '\'' +
                ", articleTitle='" + articleTitle + '\'' +
                '}';
    }
}
