package com.cpsc449.gamebacklog.service;

import com.cpsc449.gamebacklog.dto.AuthResponse;
import com.cpsc449.gamebacklog.dto.LoginRequest;
import com.cpsc449.gamebacklog.dto.RegisterRequest;
import com.cpsc449.gamebacklog.entity.User;
import com.cpsc449.gamebacklog.exception.DuplicateEmailException;
import com.cpsc449.gamebacklog.exception.InvalidCredentialsException;
import com.cpsc449.gamebacklog.repository.UserRepository;
import com.cpsc449.gamebacklog.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException("Email is already registered");
        }

        User user = new User(
                req.getUsername().trim(),
                email,
                passwordEncoder.encode(req.getPassword())
        );
        userRepository.save(user);

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getEmail(),
                user.getUsername(), jwtUtil.getExpirationMs());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        String email = req.getEmail().trim().toLowerCase();

        // Generic message — never reveal whether email or password was wrong.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getId(), user.getEmail(),
                user.getUsername(), jwtUtil.getExpirationMs());
    }
}
