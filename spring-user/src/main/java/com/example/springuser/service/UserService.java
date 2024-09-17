package com.example.springuser.service;

import com.example.springuser.dto.SignupRequest;
import com.example.springuser.dto.UserInfoDto;
import com.example.springuser.entity.User;
import com.example.springuser.entity.UserRole;
import com.example.springuser.jwt.JwtUtil;
import com.example.springuser.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // ADMIN_TOKEN
    // 관리자 권한을 부여할 수 있는 관리자 페이지를 따로 만들어야 함
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    public void signup(SignupRequest requestDto) {
        String username = requestDto.getUsername();
        String password = passwordEncoder.encode(requestDto.getPassword());

        // 회원 중복 확인
        Optional<User> checkUsername = userRepository.findByUsername(username);
        if (checkUsername.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }

        // email 중복확인
        String email = requestDto.getEmail();
        Optional<User> checkEmail = userRepository.findByEmail(email);
        if (checkEmail.isPresent()) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        // 사용자 ROLE 확인
        UserRole role = UserRole.USER;
        if (requestDto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(requestDto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRole.ADMIN;
        }

        // 사용자 등록
        User user = new User(username, password, email, role);
        userRepository.save(user);
    }

    @Transactional
    public void followUser(Long followerId, Long followedId) {
        validateFollowRequest(followerId, followedId);
        User follower = userRepository.findById(followerId).orElseThrow(() -> new EntityNotFoundException("Follower not found"));
        User followed = userRepository.findById(followedId).orElseThrow(() -> new EntityNotFoundException("Followed user not found"));

        if (!follower.getFollowing().contains(followed)) {
            follower.getFollowing().add(followed);
            followed.getFollowers().add(follower);
            userRepository.save(follower);
            userRepository.save(followed);
        }
    }

    @Transactional
    public void unfollowUser(Long followerId, Long followedId) {
        validateFollowRequest(followerId, followedId);
        User follower = userRepository.findById(followerId).orElseThrow(() -> new EntityNotFoundException("Follower not found"));
        User followed = userRepository.findById(followedId).orElseThrow(() -> new EntityNotFoundException("Followed user not found"));

        if (follower.getFollowing().contains(followed)) {
            follower.getFollowing().remove(followed);
            followed.getFollowers().remove(follower);
            userRepository.save(follower);
            userRepository.save(followed);
        }
    }

    @Transactional(readOnly = true)
    public List<UserInfoDto> getFollowers(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return user.getFollowers().stream()
                .map(follower -> new UserInfoDto(
                        follower.getId(),
                        follower.getUsername(),
                        follower.getRole() == UserRole.ADMIN))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserInfoDto> getFollowing(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return user.getFollowing().stream()
                .map(following -> new UserInfoDto(following.getId(), following.getUsername(), following.getRole() == UserRole.ADMIN))
                .toList();
    }

    private static void validateFollowRequest(Long followerId, Long followedId) {
        // 검증 로직
        if (followerId.equals(followedId)) {
            throw new IllegalArgumentException("자기 자신을 팔로우(혹은 언팔로우) 할 수 없습니다.");
        }
    }
}