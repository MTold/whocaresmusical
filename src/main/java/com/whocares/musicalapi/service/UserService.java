package com.whocares.musicalapi.service;

import com.whocares.musicalapi.dto.request.LoginRequest;
import com.whocares.musicalapi.dto.request.RegisterRequest;
import com.whocares.musicalapi.dto.response.AuthResponse;
import com.whocares.musicalapi.entity.User;
import com.whocares.musicalapi.repository.UserRepository;
import com.whocares.musicalapi.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthResponse registerUser(RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("用户名已存在");
        }

        // Check if email already exists
        if (registerRequest.getEmail() != null && 
            userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }

        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        user.setGender(registerRequest.getGender());
        
        // Parse birthday string to LocalDate
        if (registerRequest.getBirthday() != null && !registerRequest.getBirthday().isEmpty()) {
            try {
                user.setBirthday(LocalDate.parse(registerRequest.getBirthday()));
            } catch (Exception e) {
                // If parsing fails, leave birthday as null
                user.setBirthday(null);
            }
        }
        
        user.setUserImage(registerRequest.getUserImage());

        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(savedUser.getUsername());

        return new AuthResponse(
            token,
            savedUser.getUserId(),
            savedUser.getUsername(),
            savedUser.getEmail(),
            savedUser.getUserImage()
        );
    }

    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new RuntimeException("用户不存在"));

        String token = jwtTokenProvider.generateToken(user.getUsername());

        return new AuthResponse(
            token,
            user.getUserId(),
            user.getUsername(),
            user.getEmail(),
            user.getUserImage()
        );
    }

    public User getCurrentUser(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    public User updateUser(User updatedUser, String username) {
        User user = getCurrentUser(username);
        
        // 更新用户信息（不包括用户名和密码）
        if (updatedUser.getEmail() != null) {
            user.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPhone() != null) {
            user.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getGender() != null) {
            user.setGender(updatedUser.getGender());
        }
        if (updatedUser.getBirthday() != null) {
            user.setBirthday(updatedUser.getBirthday());
        }
        if (updatedUser.getUserImage() != null) {
            user.setUserImage(updatedUser.getUserImage());
        }
        
        return userRepository.save(user);
    }
}