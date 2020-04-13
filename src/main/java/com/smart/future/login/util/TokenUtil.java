package com.smart.future.login.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Date;
import java.util.List;

public class TokenUtil {
    static final long EXPIRATION_TIME = 432_000_000;     // 5天
    static final String SECRET = "P@ssw02d";            // JWT密码
    public static final String TOKEN_PREFIX = "Bearer ";        // Token前缀
    public static final String HEADER_STRING = "Authorization";// 存放Token的Header Key

    // JWT生成方法
    public static String generateAuthentication(Authentication auth) {
        String roleArrayString = auth.getAuthorities().toString();
        String roles = roleArrayString.substring(1,roleArrayString.length()-1);

        // 生成JWT
        return Jwts.builder()
                // 保存权限（角色）
                .claim("roles", roles)
                // 用户名写入标题
                .setSubject(auth.getName())
                // 有效期设置
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                // 签名设置
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    // JWT验证方法
    public static Authentication getAuthentication(String token) {
        if (token != null) {
            // 解析 Token
            Claims claims = Jwts.parser()
                    // 验签
                    .setSigningKey(SECRET)
                    // 去掉 Bearer
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();

            // if (claims.getExpiration().getTime() >= System.currentTimeMillis()) {
            //     throw new JWTAuthenticationExpiredException(SmartCode.LoginError.AUTH_EXPIRE, "身份认证已过期");
            // }


            // 拿用户名
            String user = claims.getSubject();

            // 得到 权限（角色）
            List<GrantedAuthority> authorities =  AuthorityUtils.commaSeparatedStringToAuthorityList((String) claims.get("roles"));

            // 返回验证令牌
            return user != null
                    ? new UsernamePasswordAuthenticationToken(user, null, authorities)
                    : null;
        }
        return null;
    }
}
