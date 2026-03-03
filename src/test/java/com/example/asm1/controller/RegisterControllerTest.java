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

    // ===== TC_REG_01 – ĐĂNG KÝ HỢP LỆ =====
    @Test
    void TC_REG_01_DangKyHopLe() throws Exception {
        // Giả lập: Điền đúng thông tin thì đăng ký thành công (Không quăng lỗi gì cả)
        doNothing().when(authService).register("Valid Name", "valid@gmail.com", "validPass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "Valid Name")
                .param("email", "valid@gmail.com")
                .param("password", "validPass123"))
                .andExpect(status().is3xxRedirection()) // Trọng tài check: Phải chuyển hướng trang
                .andExpect(redirectedUrl("/auth/login")); // Trọng tài check: Phải chuyển về trang đăng nhập
    }

    // ===== TC_REG_02 – BỎ TRỐNG HỌ TÊN =====
    @Test
    void TC_REG_02_BoTrongHoTen() throws Exception {
        // Giả lập: Bỏ trống tên thì tầng Service sẽ quăng lỗi
        doThrow(new RuntimeException("USERNAME_EMPTY")).when(authService).register("", "test@gmail.com", "pass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "")
                .param("email", "test@gmail.com")
                .param("password", "pass123"))
                .andExpect(model().attributeExists("error")) // Kiểm tra xem có gửi biến lỗi ra view không
                .andExpect(view().name("auth/register"));    // Trả lại đúng trang đăng ký để nhập lại
    }

    // ===== TC_REG_03 – BỎ TRỐNG MẬT KHẨU =====
    @Test
    void TC_REG_03_BoTrongMatKhau() throws Exception {
        // Giả lập: Không nhập pass thì báo lỗi
        doThrow(new RuntimeException("PASSWORD_EMPTY")).when(authService).register("Test Name", "test@gmail.com", "");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "Test Name")
                .param("email", "test@gmail.com")
                .param("password", ""))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_04 – MẬT KHẨU QUÁ NGẮN (< 6 ký tự) =====
    @Test
    void TC_REG_04_MatKhauQuaNgan() throws Exception {
        // Giả lập: Pass chỉ có 5 chữ số thì báo lỗi bảo mật yếu
        doThrow(new RuntimeException("PASSWORD_TOO_SHORT")).when(authService).register("Test Name", "test@gmail.com", "12345");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "Test Name")
                .param("email", "test@gmail.com")
                .param("password", "12345"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_05 – SAI ĐỊNH DẠNG EMAIL =====
    @Test
    void TC_REG_05_SaiDinhDangEmail() throws Exception {
        // Giả lập: Email thiếu chữ .com thì từ chối
        doThrow(new RuntimeException("INVALID_EMAIL_FORMAT")).when(authService).register("Test Name", "abc@", "pass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "Test Name")
                .param("email", "abc@")
                .param("password", "pass123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_06 – HỌ TÊN BỊ TRÙNG LẶP =====
    @Test
    void TC_REG_06_HoTenDaTonTai() throws Exception {
        doThrow(new RuntimeException("USERNAME_EXISTS")).when(authService).register("ExistingUser", "new@gmail.com", "pass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "ExistingUser")
                .param("email", "new@gmail.com")
                .param("password", "pass123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_07 – EMAIL ĐÃ TỒN TẠI (TRÙNG EMAIL) =====
    @Test
    void TC_REG_07_EmailDaTonTai() throws Exception {
        // Giả lập tầng Service quăng lỗi vì email này đã có người đăng ký
        doThrow(new RuntimeException("EMAIL_EXISTS")).when(authService).register("Test Name", "existing@gmail.com", "pass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "Test Name")
                .param("email", "existing@gmail.com")
                .param("password", "pass123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Email đã tồn tại")) // Bắt đúng chữ Controller in ra
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_08 – THIẾU MẬT KHẨU XÁC NHẬN =====
    @Test
    void TC_REG_08_ThieuXacNhanMatKhau() throws Exception {
        // Giả lập lỗi thiếu Confirm Password
        doThrow(new RuntimeException("MISSING_CONFIRM_PASSWORD")).when(authService).register("Test Name", "test@gmail.com", "pass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "Test Name")
                .param("email", "test@gmail.com")
                .param("password", "pass123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_09 – MẬT KHẨU VÀ XÁC NHẬN KHÔNG KHỚP =====
    @Test
    void TC_REG_09_MatKhauKhongKhop() throws Exception {
        doThrow(new RuntimeException("PASSWORD_MISMATCH")).when(authService).register("Test Name", "test@gmail.com", "pass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "Test Name")
                .param("email", "test@gmail.com")
                .param("password", "pass123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_10 – HỌ TÊN CHỨA KÝ TỰ ĐẶC BIỆT =====
    @Test
    void TC_REG_10_HoTenChuaKyTuDacBiet() throws Exception {
        doThrow(new RuntimeException("INVALID_USERNAME_FORMAT")).when(authService).register("@#$%^", "test@gmail.com", "pass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "@#$%^")
                .param("email", "test@gmail.com")
                .param("password", "pass123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_11 – BỎ TRỐNG EMAIL =====
    @Test
    void TC_REG_11_BoTrongEmail() throws Exception {
        // Kịch bản: Người dùng quên nhập Email
        doThrow(new RuntimeException("EMAIL_EMPTY")).when(authService).register("Test Name", "", "pass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "Test Name")
                .param("email", "")
                .param("password", "pass123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_12 – BỎ TRỐNG TOÀN BỘ FORM =====
    @Test
    void TC_REG_12_BoTrongToanBoForm() throws Exception {
        // Kịch bản: Người dùng lười, không nhập gì cả mà bấm Đăng ký luôn
        doThrow(new RuntimeException("ALL_FIELDS_EMPTY")).when(authService).register("", "", "");

        mockMvc.perform(post("/auth/register")
                .param("fullname", "")
                .param("email", "")
                .param("password", ""))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_13 – MẬT KHẨU QUÁ DÀI =====
    @Test
    void TC_REG_13_MatKhauQuaDai() throws Exception {
        // Kịch bản: Cố tình nhập mật khẩu dài 60 ký tự để phá DB
        String longPassword = "a".repeat(60); // Code tự tạo ra chuỗi 60 chữ 'a'
        doThrow(new RuntimeException("PASSWORD_TOO_LONG")).when(authService).register("Test Name", "test@gmail.com", longPassword);

        mockMvc.perform(post("/auth/register")
                .param("fullname", "Test Name")
                .param("email", "test@gmail.com")
                .param("password", longPassword))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_14 – HỌ TÊN QUÁ DÀI =====
    @Test
    void TC_REG_14_HoTenQuaDai() throws Exception {
        // Kịch bản: Cố tình nhập Họ Tên siêu dài (150 ký tự)
        String longName = "N".repeat(150); // Tạo ra chuỗi 150 chữ 'N'
        doThrow(new RuntimeException("FULLNAME_TOO_LONG")).when(authService).register(longName, "test@gmail.com", "pass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", longName)
                .param("email", "test@gmail.com")
                .param("password", "pass123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }

    // ===== TC_REG_15 – MÃ ĐỘC XSS VÀO HỌ TÊN =====
    @Test
    void TC_REG_15_MaDocXssTrongHoTen() throws Exception {
        // Kịch bản: Hacker cố tình nhập mã độc JavaScript vào ô Họ tên để phá web
        String xssPayload = "<script>alert('Hacked')</script>";
        doThrow(new RuntimeException("INVALID_FULLNAME_FORMAT")).when(authService).register(xssPayload, "test@gmail.com", "pass123");

        mockMvc.perform(post("/auth/register")
                .param("fullname", xssPayload)
                .param("email", "test@gmail.com")
                .param("password", "pass123"))
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("auth/register"));
    }
}