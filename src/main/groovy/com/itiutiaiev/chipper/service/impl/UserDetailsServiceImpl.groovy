package com.itiutiaiev.chipper.service.impl

import jakarta.annotation.Resource
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserServiceImpl userService

    @Override
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        def user = userService.findByEmail(email)
        new User(user.email, user.password, [new SimpleGrantedAuthority("USER")])
    }
}
