package com.cinelog.CineLog.controller;

import com.cinelog.CineLog.dto.AuthResponse;
import com.cinelog.CineLog.dto.LoginRequestDto;
import com.cinelog.CineLog.dto.SignUpDto;
import com.cinelog.CineLog.repository.UserRepo;
import com.cinelog.CineLog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepo userRepo;

    @PostMapping("/signup")
    public ResponseEntity<?> signUpUser(@Valid @RequestBody SignUpDto signUpDto){
        try {
            boolean success = userService.signUpUser(signUpDto);
            if (!success) {
                // signUpUser returns false when user already exists
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User with this email already exists");
            }
            // User created successfully
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User created successfully");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid email or password format");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto){
        try {
            AuthResponse response = userService.loginUser(loginRequestDto);
            if (response == null) {
                // loginUser returns null when credentials are invalid
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Invalid email or password");
            }
            // Login successful
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid email or password format");
        }
    }
}
