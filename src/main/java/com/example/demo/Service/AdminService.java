/** 관리자 페이지 구현을 위한 로직 Service **/
package com.example.demo.Service;
import com.example.demo.Entity.UserEntity;
import com.example.demo.Repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // 페이징 처리된 사용자 목록 가져오기
    public Page<UserEntity> getAllUsersPage(int page) {
        Pageable pageable = PageRequest.of(page, 10); // 한 페이지에 10명씩 표시
        return userRepository.findAll(pageable);
    }
}