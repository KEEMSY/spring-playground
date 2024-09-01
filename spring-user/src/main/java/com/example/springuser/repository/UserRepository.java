package com.example.springuser.repository;

import com.example.springuser.entity.SocialProvider;
import com.example.springuser.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByKakaoId(Long kakaoId);

    Optional<User> findBySocialIdAndSocialProvider(String socialId, SocialProvider provider);
}
