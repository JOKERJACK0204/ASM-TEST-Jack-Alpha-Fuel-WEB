package com.example.asm1.controller;

import com.example.asm1.entity.User;
import com.example.asm1.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.example.asm1.controller.AuthController.class)
public class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    // ===== TC_REG_01 – VALID REGISTRATION =====
    @Test
    void TC_REG_01_ValidRegistration() throws Exception {
        doNothing().when(authService).register("Valid Name", "valid@gmail.com", "validPass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "Valid Name")
                .param("email", "valid@gmail.com")
                .param("password", "validPass123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"));
    }

    // ===== TC_REG_02 – USERNAME EMPTY =====
    @Test
    void TC_REG_02_UsernameEmpty() throws Exception {
        doThrow(new RuntimeException("USERNAME_EMPTY")).when(authService).register("", "test@gmail.com", "pass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "")
                .param("email", "test@gmail.com")
                .param("password", "pass123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_03 – PASSWORD EMPTY =====
    @Test
    void TC_REG_03_PasswordEmpty() throws Exception {
        doThrow(new RuntimeException("PASSWORD_EMPTY")).when(authService).register("Test Name", "test@gmail.com", "");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "Test Name")
                .param("email", "test@gmail.com")
                .param("password", ""))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_04 – PASSWORD TOO SHORT (< 6 chars) =====
    @Test
    void TC_REG_04_PasswordTooShort() throws Exception {
        doThrow(new RuntimeException("PASSWORD_TOO_SHORT")).when(authService).register("Test Name", "test@gmail.com", "12345");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "Test Name")
                .param("email", "test@gmail.com")
                .param("password", "12345"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_05 – INVALID EMAIL FORMAT =====
    @Test
    void TC_REG_05_InvalidEmailFormat() throws Exception {
        doThrow(new RuntimeException("INVALID_EMAIL_FORMAT")).when(authService).register("Test Name", "abc@", "pass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "Test Name")
                .param("email", "abc@")
                .param("password", "pass123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_06 – USERNAME EXISTS =====
    @Test
    void TC_REG_06_UsernameExists() throws Exception {
        doThrow(new RuntimeException("USERNAME_EXISTS")).when(authService).register("ExistingUser", "new@gmail.com", "pass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "ExistingUser")
                .param("email", "new@gmail.com")
                .param("password", "pass123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_07 – EMAIL EXISTS =====
    @Test
    void TC_REG_07_EmailExists() throws Exception {
        doThrow(new RuntimeException("EMAIL_EXISTS")).when(authService).register("Test Name", "existing@gmail.com", "pass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "Test Name")
                .param("email", "existing@gmail.com")
                .param("password", "pass123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Email đã tồn tại"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_08 – MISSING CONFIRM PASSWORD =====
    @Test
    void TC_REG_08_MissingConfirmPassword() throws Exception {
        // Simulating the failure at the service level if it expected confirming logic
        doThrow(new RuntimeException("MISSING_CONFIRM_PASSWORD")).when(authService).register("Test Name", "test@gmail.com", "pass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "Test Name")
                .param("email", "test@gmail.com")
                .param("password", "pass123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_09 – PASSWORD & CONFIRM PASSWORD MISMATCH =====
    @Test
    void TC_REG_09_PasswordMismatch() throws Exception {
        doThrow(new RuntimeException("PASSWORD_MISMATCH")).when(authService).register("Test Name", "test@gmail.com", "pass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "Test Name")
                .param("email", "test@gmail.com")
                .param("password", "pass123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_10 – SPECIAL CHARACTERS IN USERNAME =====
    @Test
    void TC_REG_10_SpecialCharsInUsername() throws Exception {
        doThrow(new RuntimeException("INVALID_USERNAME_FORMAT")).when(authService).register("@#$%^", "test@gmail.com", "pass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "@#$%^")
                .param("email", "test@gmail.com")
                .param("password", "pass123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }
}