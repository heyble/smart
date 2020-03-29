package com.smart.future.login.config;

import com.smart.future.common.constant.RoleCode;
import com.smart.future.login.JwtUserService;
import com.smart.future.login.exception.JwtAuthenticationEntryPoint;
import com.smart.future.login.filter.JWTAuthenticationFilter;
import com.smart.future.login.filter.JWTLoginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private JwtUserService jwtUserService;

    // 设置 HTTP 验证规则
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 放行路径
        String[] permitAllMatchers = new String[]{"/css/**", "/js/**", "/img/**", "/user/register", "/storage/**", "/*"};

        http.cors() // 开启跨域支持
                .and()
                .authorizeRequests()
                // 放行请求
                .antMatchers(permitAllMatchers).permitAll()
                .antMatchers("/user/findAll").hasAnyRole(RoleCode.ROOT.getCode(),RoleCode.ADMINISTRATOR.getCode())
                .antMatchers("/user/**").hasAnyRole(RoleCode.getAllCode())
                .and()
                // 表单登录方式关闭
                .formLogin()
                .disable()
                .logout()
                .permitAll()
                .and()
                .authorizeRequests()
                // 任何请求
                .anyRequest()
                // 需要身份认证
                .authenticated()
                .and()
                // 关闭跨站请求防护
                .csrf().disable()
                // 前后端分离采用JWT 不需要session
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 统一异常处理
                .exceptionHandling()
                // 403 异常
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .and()
                .addFilterBefore(new JWTLoginFilter("/login", authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                // 添加一个过滤器验证其他请求的Token是否合法
                .addFilterBefore(new JWTAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserService).passwordEncoder(new BCryptPasswordEncoder());//加密
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}