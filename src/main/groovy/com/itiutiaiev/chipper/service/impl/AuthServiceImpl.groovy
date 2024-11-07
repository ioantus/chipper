package com.itiutiaiev.chipper.service.impl

import com.itiutiaiev.chipper.model.dto.LoginDto
import com.itiutiaiev.chipper.security.JwtUtil
import com.itiutiaiev.chipper.service.AuthService
import jakarta.annotation.Resource
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl implements AuthService {

    @Resource
    private AuthenticationManager authenticationManager
    @Resource
    private JwtUtil jwtUtil

    @Override
    String authenticate(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.email,
                loginDto.password
        ))
        SecurityContextHolder.getContext().setAuthentication(authentication)
        jwtUtil.createToken(loginDto.email)
    }
}
