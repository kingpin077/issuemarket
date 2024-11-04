/** 사용자 회원가입 시 요청을 위한 DTO **/

package com.example.demo.DTO;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserDTO {
    private String userCode;
    private String userId;
    private String userPwd;
    private String userName;
    private boolean gender;
    private String role;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate userBirth;

    {
        this.role = "USER";
    }

}