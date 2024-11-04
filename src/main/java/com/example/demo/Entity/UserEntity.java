package com.example.demo.Entity;

import javax.persistence.*;

import com.example.demo.DTO.UserDTO;
import lombok.*;

import java.time.LocalDate;


@NoArgsConstructor
@Entity
@Getter@Setter
@Table(name="user_table")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_code", unique = true)
    private int userCode;

    @Column(name="user_id")
    private String userId;

    @Column(name="user_pwd")
    private String userPwd;

    @Column(name="user_name")
    private String userName;

    @Column(name="gender")
    private boolean gender;

    @Column(name="role")
    private String role;

    @Column(name = "user_birth")
    private LocalDate userBirth;

    @Builder
    public static UserEntity toUserEntity(UserDTO userDTO) {
        UserEntity userEntity = new UserEntity();

        userEntity.userId = userDTO.getUserId();
        userEntity.userPwd = userDTO.getUserPwd();
        userEntity.userName = userDTO.getUserName();
        userEntity.gender = userDTO.isGender();
        userEntity.role = userDTO.getRole();
        userEntity.userBirth = userDTO.getUserBirth();

        return userEntity;
    }

}
