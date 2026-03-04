package com.example.asm1.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

public class RegisterTest {

    WebDriver driver;
    String baseUrl = "http://localhost:8080/auth/register";

    @BeforeEach // Đã đổi từ BeforeMethod
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        // Mở trang đăng ký trước mỗi Test Case
        driver.get(baseUrl);
    }

    // ===== REGISTER_01: ĐĂNG KÝ HỢP LỆ =====
    @Test
    public void REGISTER_01_ValidRegistration() throws InterruptedException {
        driver.findElement(By.name("fullname")).sendKeys("Nguyen Van A");
        driver.findElement(By.name("email")).sendKeys("nguyenvana123@gmail.com");
        driver.findElement(By.name("password")).sendKeys("matkhau123");
        
        driver.findElement(By.xpath("//button[contains(text(), 'Register')]")).click();
        Thread.sleep(1000);

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("/auth/login"), "Lỗi: Không chuyển hướng về trang Login");
    }

    // ===== REGISTER_02: BỎ TRỐNG HỌ TÊN (HTML5 chặn) =====
    @Test
    public void REGISTER_02_UsernameEmpty() {
        driver.findElement(By.name("email")).sendKeys("test@gmail.com");
        driver.findElement(By.name("password")).sendKeys("123456");
        driver.findElement(By.xpath("//button[contains(text(), 'Register')]")).click();

        WebElement fullnameInput = driver.findElement(By.name("fullname"));
        String validationMsg = fullnameInput.getAttribute("validationMessage");
        Assertions.assertFalse(validationMsg.isEmpty(), "Lỗi: Trình duyệt không chặn trường hợp bỏ trống tên");
    }

    // ===== REGISTER_03: BỎ TRỐNG MẬT KHẨU (HTML5 chặn) =====
    @Test
    public void REGISTER_03_PasswordEmpty() {
        driver.findElement(By.name("fullname")).sendKeys("Tester");
        driver.findElement(By.name("email")).sendKeys("test@gmail.com");
        driver.findElement(By.xpath("//button[contains(text(), 'Register')]")).click();

        WebElement passInput = driver.findElement(By.name("password"));
        String validationMsg = passInput.getAttribute("validationMessage");
        Assertions.assertFalse(validationMsg.isEmpty(), "Lỗi: Trình duyệt không chặn bỏ trống mật khẩu");
    }

    // ===== REGISTER_04: MẬT KHẨU QUÁ NGẮN =====
    @Test
    public void REGISTER_04_PasswordTooShort() throws InterruptedException {
        driver.findElement(By.name("fullname")).sendKeys("Tester");
        driver.findElement(By.name("email")).sendKeys("tester1@gmail.com");
        driver.findElement(By.name("password")).sendKeys("123"); 
        driver.findElement(By.xpath("//button[contains(text(), 'Register')]")).click();
        Thread.sleep(1000);

        WebElement errorText = driver.findElement(By.className("text-danger"));
        Assertions.assertTrue(errorText.isDisplayed(), "Lỗi: Không hiển thị thông báo lỗi mật khẩu yếu");
    }

    // ===== REGISTER_05: SAI ĐỊNH DẠNG EMAIL (HTML5 chặn) =====
    @Test
    public void REGISTER_05_InvalidEmailFormat() {
        driver.findElement(By.name("fullname")).sendKeys("Tester");
        driver.findElement(By.name("email")).sendKeys("abcgmail.com"); 
        driver.findElement(By.name("password")).sendKeys("123456");
        driver.findElement(By.xpath("//button[contains(text(), 'Register')]")).click();

        WebElement emailInput = driver.findElement(By.name("email"));
        String validationMsg = emailInput.getAttribute("validationMessage");
        Assertions.assertTrue(validationMsg.contains("@"), "Lỗi: Trình duyệt không báo lỗi sai định dạng email");
    }

    // ===== REGISTER_06: TÊN ĐĂNG NHẬP ĐÃ TỒN TẠI =====
    @Test
    public void REGISTER_06_UsernameExists() throws InterruptedException {
        driver.findElement(By.name("fullname")).sendKeys("Admin");
        driver.findElement(By.name("email")).sendKeys("newemail1@gmail.com");
        driver.findElement(By.name("password")).sendKeys("123456");
        driver.findElement(By.xpath("//button[contains(text(), 'Register')]")).click();
        Thread.sleep(1000);

        WebElement errorText = driver.findElement(By.className("text-danger"));
        Assertions.assertTrue(errorText.isDisplayed(), "Lỗi: Không hiển thị thông báo trùng tên");
    }

    // ===== REGISTER_07: EMAIL ĐÃ TỒN TẠI =====
    @Test
    public void REGISTER_07_EmailExists() throws InterruptedException {
        driver.findElement(By.name("fullname")).sendKeys("User Moi");
        driver.findElement(By.name("email")).sendKeys("admin@gmail.com");
        driver.findElement(By.name("password")).sendKeys("123456");
        driver.findElement(By.xpath("//button[contains(text(), 'Register')]")).click();
        Thread.sleep(1000);

        WebElement errorText = driver.findElement(By.className("text-danger"));
        Assertions.assertTrue(errorText.getText().contains("đã tồn tại"), "Lỗi: Không báo trùng email");
    }

    // ===== REGISTER_08: EMAIL RỖNG (HTML5 chặn) =====
    @Test
    public void REGISTER_08_EmailEmpty() {
        driver.findElement(By.name("fullname")).sendKeys("Tester");
        driver.findElement(By.name("password")).sendKeys("123456");
        driver.findElement(By.xpath("//button[contains(text(), 'Register')]")).click();

        WebElement emailInput = driver.findElement(By.name("email"));
        Assertions.assertFalse(emailInput.getAttribute("validationMessage").isEmpty(), "Lỗi: Không chặn bỏ trống email");
    }

    // ===== REGISTER_09: BỎ TRỐNG TOÀN BỘ FORM =====
    @Test
    public void REGISTER_09_AllFieldsEmpty() {
        driver.findElement(By.xpath("//button[contains(text(), 'Register')]")).click();

        WebElement fullnameInput = driver.findElement(By.name("fullname"));
        Assertions.assertFalse(fullnameInput.getAttribute("validationMessage").isEmpty(), "Lỗi: Cho phép submit form trống");
    }

    // ===== REGISTER_10: KÝ TỰ ĐẶC BIỆT TRONG TÊN =====
    @Test
    public void REGISTER_10_SpecialCharsInUsername() throws InterruptedException {
        driver.findElement(By.name("fullname")).sendKeys("@#$$%^");
        driver.findElement(By.name("email")).sendKeys("special@gmail.com");
        driver.findElement(By.name("password")).sendKeys("123456");
        driver.findElement(By.xpath("//button[contains(text(), 'Register')]")).click();
        Thread.sleep(1000);

        WebElement errorText = driver.findElement(By.className("text-danger"));
        Assertions.assertTrue(errorText.isDisplayed(), "Lỗi: Không chặn ký tự đặc biệt ở Họ Tên");
    }

    // ===== REGISTER_11: MẬT KHẨU QUÁ DÀI =====
    @Test
    public void REGISTER_11_PasswordTooLong() throws InterruptedException {
        String longPass = "a".repeat(100); 
        driver.findElement(By.name("fullname")).sendKeys("Tester");
        driver.findElement(By.name("email")).sendKeys("longpass@gmail.com");
        driver.findElement(By.name("password")).sendKeys(longPass);
        driver.findElement(By.xpath("//button[contains(text(), 'Register')]")).click();
        Thread.sleep(1000);

        Assertions.assertFalse(driver.getPageSource().contains("Exception"), "Lỗi: Web bị crash (sập) do password quá dài");
    }

    // ===== REGISTER_12: HỌ TÊN QUÁ DÀI =====
    @Test
    public void REGISTER_12_FullnameTooLong() throws InterruptedException {
        String longName = "A".repeat(150); 
        driver.findElement(By.name("fullname")).sendKeys(longName);
        driver.findElement(By.name("email")).sendKeys("longname@gmail.com");
        driver.findElement(By.name("password")).sendKeys("123456");
        driver.findElement(By.xpath("//button[contains(text(), 'Register')]")).click();
        Thread.sleep(1000);

        Assertions.assertFalse(driver.getPageSource().contains("Exception"), "Lỗi: Web bị crash do tên quá dài");
    }

    // ===== REGISTER_13: XSS INJECTION =====
    @Test
    public void REGISTER_13_XssInjection() throws InterruptedException {
        String xssCode = "<script>alert('Hacked')</script>";
        driver.findElement(By.name("fullname")).sendKeys(xssCode);
        driver.findElement(By.name("email")).sendKeys("xss@gmail.com");
        driver.findElement(By.name("password")).sendKeys("123456");
        driver.findElement(By.xpath("//button[contains(text(), 'Register')]")).click();
        Thread.sleep(1000);

        try {
            driver.switchTo().alert().accept();
            Assertions.fail("BUG BẢO MẬT: Web bị dính lỗi XSS Injection!");
        } catch (Exception e) {
            System.out.println("Tốt! Web không bị thực thi mã độc XSS.");
        }
    }

    // ===== REGISTER_14: SQL INJECTION =====
    @Test
    public void REGISTER_14_SqlInjection() throws InterruptedException {
        driver.findElement(By.name("fullname")).sendKeys("Tester");
        driver.findElement(By.name("email")).sendKeys("' OR '1'='1");
        driver.findElement(By.name("password")).sendKeys("123456");
        
        WebElement btn = driver.findElement(By.xpath("//button[contains(text(), 'Register')]"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", btn);
        Thread.sleep(1000);

        String currentUrl = driver.getCurrentUrl();
        Assertions.assertTrue(currentUrl.contains("register"), "BUG BẢO MẬT: Đăng ký thành công với SQL Injection");
    }

    // ===== REGISTER_15: ĐĂNG KÝ NHIỀU TÀI KHOẢN LIÊN TIẾP =====
    @Test
    public void REGISTER_15_MultipleRegistrations() throws InterruptedException {
        driver.findElement(By.name("fullname")).sendKeys("User One");
        driver.findElement(By.name("email")).sendKeys("userone@gmail.com");
        driver.findElement(By.name("password")).sendKeys("123456");
        driver.findElement(By.xpath("//button[contains(text(), 'Register')]")).click();
        Thread.sleep(1000);
        
        driver.get(baseUrl);
        Thread.sleep(500);

        driver.findElement(By.name("fullname")).sendKeys("User Two");
        driver.findElement(By.name("email")).sendKeys("usertwo@gmail.com");
        driver.findElement(By.name("password")).sendKeys("123456");
        driver.findElement(By.xpath("//button[contains(text(), 'Register')]")).click();
        Thread.sleep(1000);

        Assertions.assertTrue(driver.getCurrentUrl().contains("login"), "Lỗi: Không thể đăng ký nhiều tài khoản liên tiếp");
    }

    @AfterEach // Đã đổi từ AfterMethod
    public void tearDown() throws InterruptedException {
        Thread.sleep(1500); 
        if (driver != null) {
            driver.quit();
        }
    }
}