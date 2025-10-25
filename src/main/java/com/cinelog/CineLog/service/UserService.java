package com.cinelog.CineLog.service;

import com.cinelog.CineLog.dto.AuthResponse;
import com.cinelog.CineLog.dto.LoginRequestDto;
import com.cinelog.CineLog.dto.SignUpDto;
import com.cinelog.CineLog.entity.User;
import com.cinelog.CineLog.repository.UserRepo;
import com.cinelog.CineLog.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean signUpUser(SignUpDto signUpDto){
        if(isUserExistsByEmail(signUpDto.getEmail())){
         return false;
        }
        User user= new User();
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        userRepo.save(user);
        return true;
    }
    public User findUserByEmail(String email){
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if(optionalUser.isEmpty()){
            return null;
        }
        return optionalUser.get();
    }
    public boolean isUserExistsByEmail(String email){
        return userRepo.existsByEmail(email);
    }
    public AuthResponse loginUser(LoginRequestDto loginRequestDto){
        if(!isUserExistsByEmail(loginRequestDto.getEmail())) {
            return null;
        }
        User user =findUserByEmail(loginRequestDto.getEmail());
        if(!passwordEncoder.matches(loginRequestDto.getPassword(),user.getPassword())){
            return null;
        }
        AuthResponse authResponse= new AuthResponse();
        Map<String,Object> map= new HashMap<>();
        authResponse.setToken(jwtUtil.createToken(map, loginRequestDto.getEmail()));
        return authResponse;
    }

}
