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

    // ===== ADMIN LOGIN =====
    @Test
    void loginSuccess_Admin() throws Exception {

        User admin = new User();
        admin.setRole("ADMIN");

        when(authService.login("admin@gmail.com", "123"))
                .thenReturn(admin);

        when(authService.getCartCount(admin))
                .thenReturn(0);

        mockMvc.perform(post("/auth/login")
                        .param("email", "admin@gmail.com")
                        .param("password", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products/crud"));
    }

    // ===== USER LOGIN =====
    @Test
    void loginSuccess_User() throws Exception {

        User user = new User();
        user.setRole("USER");

        when(authService.login("joker@gmail.com", "123"))
                .thenReturn(user);

        when(authService.getCartCount(user))
                .thenReturn(0);

        mockMvc.perform(post("/auth/login")
                        .param("email", "joker@gmail.com")
                        .param("password", "123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    // ===== EMAIL SAI =====
    @Test
    void loginFail_EmailNotFound() throws Exception {

        when(authService.login("abc@gmail.com", "123"))
                .thenThrow(new RuntimeException("EMAIL_NOT_FOUND"));

        mockMvc.perform(post("/auth/login")
                        .param("email", "abc@gmail.com")
                        .param("password", "123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/login"));
    }

    // ===== PASSWORD SAI =====
    @Test
    void loginFail_WrongPassword() throws Exception {

        when(authService.login("admin@gmail.com", "999"))
                .thenThrow(new RuntimeException("PASSWORD_WRONG"));

        mockMvc.perform(post("/auth/login")
                        .param("email", "admin@gmail.com")
                        .param("password", "999"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/login"));
    }
}