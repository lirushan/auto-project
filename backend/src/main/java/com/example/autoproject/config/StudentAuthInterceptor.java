package com.example.autoproject.config;

import com.example.autoproject.common.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class StudentAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) {
        String token = req.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            resp.setStatus(401);
            return false;
        }
        try {
            Claims claims = jwtUtil.parseToken(token.substring(7));
            if (!"student".equals(claims.get("role"))) {
                resp.setStatus(403);
                return false;
            }
            req.setAttribute("studentId", Long.valueOf(claims.getSubject()));
            req.setAttribute("studentName", claims.get("username"));
            return true;
        } catch (Exception e) {
            resp.setStatus(401);
            return false;
        }
    }
}
