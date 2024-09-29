package com.example.demo.Entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="filter")
public class Serch_infoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;
    private LocalDateTime date;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    public LocalDateTime getDate() {return date;}
    public void setDate(LocalDateTime date)
    {this.date = date;}
}
