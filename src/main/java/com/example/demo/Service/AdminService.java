/** 관리자 페이지 구현을 위한 로직 Service **/

package com.example.demo.Service;

import com.example.demo.Entity.UserEntity;
import com.example.demo.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    // 권한이 USER인 일반회원 전체 조회
    public List<UserEntity> getAllUsers() {
        return userRepository.findAllByRole("USER");
    }
}
