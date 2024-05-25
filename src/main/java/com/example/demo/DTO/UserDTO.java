package com.example.demo.DTO;
import lombok.*;
import com.example.demo.Entity.UserEntity;

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

    public static UserDTO toUserDTO(UserEntity userEntity) {
        UserDTO userDTO = new UserDTO();

        userDTO.setUserId(userEntity.getUserId());
        userDTO.setUserPwd(userEntity.getUserPwd());
        userDTO.setUserName(userEntity.getUserName());
        userDTO.setGender(userEntity.isGender());

        return userDTO;
    }
}
