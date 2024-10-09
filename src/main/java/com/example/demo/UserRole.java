/** 사용자 역할 관리 **/

package com.example.demo;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ADMIN"),
    USER("USER");
    private String value;

    UserRole(String value) {
        this.value = value;
    }


}
