package com.itiutiaiev.chipper.security

import com.itiutiaiev.chipper.service.impl.UserDetailsServiceImpl
import jakarta.annotation.Resource
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private JwtUtil jwtUtil
    @Resource
    private UserDetailsServiceImpl userDetailsService

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request)

        // Validate Token
        if(token && !token.empty && jwtUtil.isTokenValid(token)){
            String email = jwtUtil.extractEmail(token)
            UserDetails userDetails = userDetailsService.loadUserByUsername(email)
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            )
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request))
            SecurityContextHolder.getContext().setAuthentication(authenticationToken)
        }
        filterChain.doFilter(request, response)
    }

    private static String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization")
        if(bearerToken && !bearerToken.empty && bearerToken?.startsWith("bearer ")){
            bearerToken.substring(7, bearerToken.length())
        } else null
    }
}
