package com.cinelog.CineLog.util;

import com.cinelog.CineLog.entity.User;
import com.cinelog.CineLog.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header =request.getHeader("Authorization");
        String username=null;
        String jwt=null;
        if(header!=null&&header.startsWith("Bearer ")){
            jwt=header.substring(7);
            try{

                username=jwtUtil.getEmailFromToken(jwt);
            } catch (Exception e) {
                System.out.println("invalid token");
            }

        }
        if(username!=null&& SecurityContextHolder.getContext().getAuthentication()==null){
            User userOptional=userService.findUserByEmail(username);

            if(jwtUtil.isTokenValid(jwt)&&userOptional!=null){
                UsernamePasswordAuthenticationToken authToken=
                        new UsernamePasswordAuthenticationToken(username, null, List.of());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request,response);
    }
}
