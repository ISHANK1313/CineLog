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
            if (userService.signUpUser(signUpDto)) {
                return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST + " the user already exits... ");
            }
            return ResponseEntity.ok().body(HttpStatus.CREATED + " User Created...");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST + " the format of email or password is wrong...");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDto loginRequestDto){
        try {
            AuthResponse response = userService.loginUser(loginRequestDto);
            if (response == null) {
                return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST + " the User already Exists...");
            }
            return ResponseEntity.ok().body(response);
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(HttpStatus.BAD_REQUEST + " wrong email or password format ...");
        }
    }

}
