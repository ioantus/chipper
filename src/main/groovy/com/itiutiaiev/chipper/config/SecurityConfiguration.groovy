package com.itiutiaiev.chipper.config

import com.itiutiaiev.chipper.security.JwtAuthenticationFilter
import com.itiutiaiev.chipper.service.impl.UserDetailsServiceImpl
import jakarta.annotation.Resource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfiguration {

    @Resource
    private UserDetailsServiceImpl userDetailsService
    @Resource
    private JwtAuthenticationFilter authenticationFilter

    @Bean
    PasswordEncoder passwordEncoder() {
        new BCryptPasswordEncoder()
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf{csrf -> csrf.disable()}
                .authorizeHttpRequests { authorize ->
                    authorize.requestMatchers("/api/v1/user/register", "/api/v1/user/login").permitAll()
                    authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    authorize.anyRequest().authenticated()
                }.httpBasic(Customizer.withDefaults())
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
        return http.build()
    }

    @Bean
    AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider()
        authenticationProvider.setUserDetailsService(userDetailsService)
        authenticationProvider.setPasswordEncoder(passwordEncoder())
        ProviderManager providerManager = new ProviderManager(authenticationProvider)
        providerManager.setEraseCredentialsAfterAuthentication(true)
        providerManager
    }

}
