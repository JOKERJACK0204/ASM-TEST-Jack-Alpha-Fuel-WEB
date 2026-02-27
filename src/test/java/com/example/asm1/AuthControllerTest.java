package com.example.asm1;

import com.example.asm1.entity.User;
import com.example.asm1.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.example.asm1.controller.AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    // ===== LOGIN_01 – ADMIN LOGIN =====
    @Test
    void LOGIN_01_AdminLoginSuccess() throws Exception {
        User admin = new User();
        admin.setRole("ADMIN");

        when(authService.login("admin@gmail.com", "123"))
                .thenReturn(admin);
        when(authService.getCartCount(admin)).thenReturn(0);

        mockMvc.perform(post("/auth/login")
                        .param("email", "admin@gmail.com")
                        .param("password", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products/crud"));
    }

    // ===== LOGIN_02 – USER LOGIN =====
    @Test
    void LOGIN_02_UserLoginSuccess() throws Exception {
        User user = new User();
        user.setRole("USER");

        when(authService.login("user@gmail.com", "123"))
                .thenReturn(user);
        when(authService.getCartCount(user)).thenReturn(1);

        mockMvc.perform(post("/auth/login")
                        .param("email", "user@gmail.com")
                        .param("password", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    // ===== LOGIN_03 – EMAIL NOT FOUND =====
    @Test
    void LOGIN_03_EmailNotFound() throws Exception {
        when(authService.login("abc@gmail.com", "123"))
                .thenThrow(new RuntimeException("EMAIL_NOT_FOUND"));

        mockMvc.perform(post("/auth/login")
                        .param("email", "abc@gmail.com")
                        .param("password", "123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/login"));
    }

    // ===== LOGIN_04 – WRONG PASSWORD =====
    @Test
    void LOGIN_04_WrongPassword() throws Exception {
        when(authService.login("admin@gmail.com", "999"))
                .thenThrow(new RuntimeException("PASSWORD_WRONG"));

        mockMvc.perform(post("/auth/login")
                        .param("email", "admin@gmail.com")
                        .param("password", "999"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/login"));
    }

    // ===== LOGIN_05 – EMAIL & PASSWORD EMPTY =====
    @Test
    void LOGIN_05_EmptyEmailPassword() throws Exception {
        when(authService.login("", ""))
                .thenThrow(new RuntimeException("EMAIL_PASSWORD_EMPTY"));

        mockMvc.perform(post("/auth/login")
                        .param("email", "")
                        .param("password", ""))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/login"));
    }

    // ===== LOGIN_06 – EMAIL EMPTY =====
    @Test
    void LOGIN_06_EmailEmpty() throws Exception {
        when(authService.login("", "123"))
                .thenThrow(new RuntimeException("EMAIL_PASSWORD_EMPTY"));

        mockMvc.perform(post("/auth/login")
                        .param("email", "")
                        .param("password", "123"))
                .andExpect(model().attributeExists("error"));
    }

    // ===== LOGIN_07 – PASSWORD EMPTY =====
    @Test
    void LOGIN_07_PasswordEmpty() throws Exception {
        when(authService.login("admin@gmail.com", ""))
                .thenThrow(new RuntimeException("EMAIL_PASSWORD_EMPTY"));

        mockMvc.perform(post("/auth/login")
                        .param("email", "admin@gmail.com")
                        .param("password", ""))
                .andExpect(model().attributeExists("error"));
    }

    // ===== LOGIN_08 – SQL INJECTION =====
    @Test
    void LOGIN_08_SQLInjection() throws Exception {
        when(authService.login("' OR '1'='1", "123"))
                .thenThrow(new RuntimeException("EMAIL_NOT_FOUND"));

        mockMvc.perform(post("/auth/login")
                        .param("email", "' OR '1'='1")
                        .param("password", "123"))
                .andExpect(model().attributeExists("error"));
    }

    // ===== LOGIN_09 – EMAIL UPPERCASE =====
    @Test
    void LOGIN_09_EmailUppercase() throws Exception {
        when(authService.login("ADMIN@GMAIL.COM", "123"))
                .thenThrow(new RuntimeException("EMAIL_NOT_FOUND"));

        mockMvc.perform(post("/auth/login")
                        .param("email", "ADMIN@GMAIL.COM")
                        .param("password", "123"))
                .andExpect(model().attributeExists("error"));
    }

    // ===== LOGIN_10 – PASSWORD WITH SPACE =====
    @Test
    void LOGIN_10_PasswordWithSpace() throws Exception {
        when(authService.login("admin@gmail.com", "123 "))
                .thenThrow(new RuntimeException("PASSWORD_WRONG"));

        mockMvc.perform(post("/auth/login")
                        .param("email", "admin@gmail.com")
                        .param("password", "123 "))
                .andExpect(model().attributeExists("error"));
    }

    // ===== LOGIN_11 – SESSION CREATED =====
    @Test
    void LOGIN_11_SessionCreated() throws Exception {
        User user = new User();
        user.setRole("USER");

        when(authService.login("user@gmail.com", "123"))
                .thenReturn(user);
        when(authService.getCartCount(user)).thenReturn(5);

        mockMvc.perform(post("/auth/login")
                        .param("email", "user@gmail.com")
                        .param("password", "123"))
                .andExpect(request().sessionAttribute("user", user))
                .andExpect(request().sessionAttribute("cartCount", 5));
    }

    // ===== LOGIN_12 – MULTIPLE LOGIN =====
    @Test
    void LOGIN_12_MultipleLogin() throws Exception {
        User user = new User();
        user.setRole("USER");

        when(authService.login("user@gmail.com", "123"))
                .thenReturn(user);
        when(authService.getCartCount(user)).thenReturn(1);

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/auth/login")
                            .param("email", "user@gmail.com")
                            .param("password", "123"))
                    .andExpect(status().is3xxRedirection());
        }
    }

    // ===== LOGIN_13 – ADMIN CART COUNT =====
    @Test
    void LOGIN_13_AdminCartCount() throws Exception {
        User admin = new User();
        admin.setRole("ADMIN");

        when(authService.login("admin@gmail.com", "123"))
                .thenReturn(admin);
        when(authService.getCartCount(admin)).thenReturn(10);

        mockMvc.perform(post("/auth/login")
                        .param("email", "admin@gmail.com")
                        .param("password", "123"))
                .andExpect(request().sessionAttribute("cartCount", 10));
    }

    // ===== LOGIN_14 – REDIRECT CHECK =====
    @Test
    void LOGIN_14_RedirectCheck() throws Exception {
        User user = new User();
        user.setRole("USER");

        when(authService.login("user@gmail.com", "123"))
                .thenReturn(user);
        when(authService.getCartCount(user)).thenReturn(0);

        mockMvc.perform(post("/auth/login")
                        .param("email", "user@gmail.com")
                        .param("password", "123"))
                .andExpect(status().is3xxRedirection());
    }

    // ===== LOGIN_15 – ERROR VIEW =====
    @Test
    void LOGIN_15_ErrorViewReturned() throws Exception {
        when(authService.login("abc@gmail.com", "123"))
                .thenThrow(new RuntimeException("EMAIL_NOT_FOUND"));

        mockMvc.perform(post("/auth/login")
                        .param("email", "abc@gmail.com")
                        .param("password", "123"))
                .andExpect(view().name("auth/login"));
    }
}