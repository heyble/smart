package com.smart.future.login.filter;

import com.smart.future.login.util.TokenUtil;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter{

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

//    @Override
//    public void doFilter(ServletRequest request,
//                         ServletResponse response,
//                         FilterChain filterChain) throws IOException, ServletException {
//
//        Authentication authentication = null;
//        try {
//            authentication = TokenUtil.getAuthentication(((HttpServletRequest)request).getHeader(TokenUtil.HEADER_STRING));
//        } catch (JwtException e) {
//            throw new BadCredentialsException("Authentication 认证失败");
//        }
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        filterChain.doFilter(request,response);
//    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        Authentication authentication = null;
        try {
            authentication = TokenUtil.getAuthentication(((HttpServletRequest) request).getHeader(TokenUtil.HEADER_STRING));
        } catch (JwtException e) {
            throw new BadCredentialsException("Authentication 认证失败");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        super.doFilterInternal(request, response, chain);
    }
}