package com.example.demo.Service;

import com.example.demo.DTO.UserDTO;
import com.example.demo.Entity.UserEntity;
import com.example.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder  passwordEncoder;

    public void createUser(UserDTO userDTO) throws Exception {

        if (userRepository.existsByUserId(userDTO.getUserId())) {
            throw new Exception("User ID already exists");
        }
        userDTO.setUserPwd(passwordEncoder.encode(userDTO.getUserPwd()));
        UserEntity userEntity = UserEntity.toUserEntity(userDTO);
        userRepository.save(userEntity);
    }
}
