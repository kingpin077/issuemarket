/** 사용자 회원가입 시 요청을 위한 DTO **/

package com.example.demo.DTO;
import lombok.*;

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

    {
        this.role = "USER";
    }

}


