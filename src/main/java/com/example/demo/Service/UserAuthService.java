/** 사용자 인증을 위한 로직 Service **/

package com.example.demo.Service;

import com.example.demo.DTO.UserDTO;
import com.example.demo.Entity.UserEntity;
import com.example.demo.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAuthService {
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

    // 모든 사용자 정보를 가져오는 메서드
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll(); // 사용자 정보를 리스트로 반환
    }

    // 페이징 처리된 사용자 목록 가져오기
    public Page<UserEntity> getAllUsersPage(int page) {
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지에 10명씩 표시
        return userRepository.findAll(pageable);
    }

}
