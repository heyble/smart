package com.smart.future.login.filter;

import com.smart.future.common.constant.SmartCode;
import com.smart.future.common.util.JsonUtil;
import com.smart.future.common.vo.ResponseVO;
import com.smart.future.login.util.TokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFilter extends BasicAuthenticationFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(JWTLoginFilter.class);

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
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            response.setHeader("Expires","1");
            response.getWriter().println(JsonUtil.toString(ResponseVO.error(SmartCode.LoginError.AUTH_EXPIRE, "身份认证已过期")));
            return;
        } catch (JwtException e) {
            LOGGER.error("Authentication 认证失败", e);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(JsonUtil.toString(ResponseVO.error(SmartCode.LoginError.AUTH_ERROR, "Authentication 认证失败")));
            return;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        super.doFilterInternal(request, response, chain);
    }
}