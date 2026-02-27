package com.example.asm1.interceptor;

import com.example.asm1.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // ❌ Chưa đăng nhập
        if (user == null) {
            response.sendRedirect("/auth/login");
            return false;
        }

        // ❌ Không phải ADMIN
        if (!"ADMIN".equals(user.getRole())) {
            response.sendRedirect("/");
            return false;
        }

        return true; // ✅ ADMIN thì cho vào
    }
}
