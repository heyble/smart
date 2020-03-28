package com.smart.future.login.filter;

import com.smart.future.common.exception.SmartApplicationException;
import com.smart.future.common.util.JsonUtil;
import com.smart.future.login.util.TokenUtil;
import com.smart.future.user.vo.UserVO;
import com.smart.future.common.vo.ResponseVO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

    public JWTLoginFilter(String url, AuthenticationManager authManager) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authManager);
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException, IOException, ServletException {
        //从json中获取username和password
        String body = StreamUtils.copyToString(req.getInputStream(), StandardCharsets.UTF_8);
        UserVO userVO = null;
        try {
            userVO = JsonUtil.readValue(body, UserVO.class);
        } catch (SmartApplicationException e) {
            throw new BadCredentialsException("用户名和密码不能为空");
        }
        if (userVO.getPhoneId() == null || userVO.getPassword() == null) {
            throw new BadCredentialsException("用户名和密码不能为空");
        }
        //封装到token中提交
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                userVO.getPhoneId(), userVO.getPassword());
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        String authentication = TokenUtil.generateAuthentication(auth);

        res.setContentType("application/json");
        res.setStatus(HttpServletResponse.SC_OK);
        res.getOutputStream().println(JsonUtil.toString(ResponseVO.okWithData(TokenUtil.TOKEN_PREFIX + authentication)));
    }


    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(JsonUtil.toString(ResponseVO.error(500, failed.getMessage())));
    }
}