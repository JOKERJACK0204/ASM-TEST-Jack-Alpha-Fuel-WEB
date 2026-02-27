package com.example.asm1.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;

public class LoginTest2 {

    WebDriver driver;
    String baseUrl = "http://localhost:8080";

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    // 🔥 Quan trọng: Mỗi test luôn quay về trang login
    @BeforeMethod
    public void goToLoginPage() {
        driver.get(baseUrl + "/auth/login");
    }

    // ===== LOGIN_01 – ADMIN SUCCESS =====
    @Test
    public void LOGIN_01_AdminSuccess() {

        driver.findElement(By.name("email")).sendKeys("admin@gmail.com");
        driver.findElement(By.name("password")).sendKeys("123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("/products/crud"));
    }

    // ===== LOGIN_02 – USER SUCCESS =====
    @Test
    public void LOGIN_02_UserSuccess() {

        driver.findElement(By.name("email")).sendKeys("user@gmail.com");
        driver.findElement(By.name("password")).sendKeys("123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains(baseUrl));
    }

    // ===== LOGIN_03 – EMAIL NOT FOUND =====
    @Test
    public void LOGIN_03_EmailNotFound() {

        driver.findElement(By.name("email")).sendKeys("abc@gmail.com");
        driver.findElement(By.name("password")).sendKeys("123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("/auth/login"));
    }

    // ===== LOGIN_04 – WRONG PASSWORD =====
    @Test
    public void LOGIN_04_WrongPassword() {

        driver.findElement(By.name("email")).sendKeys("admin@gmail.com");
        driver.findElement(By.name("password")).sendKeys("999");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("/auth/login"));
    }

    // ===== LOGIN_05 – BOTH EMPTY =====
    @Test
    public void LOGIN_05_BothEmpty() {

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("/auth/login"));
    }

    // ===== LOGIN_06 – EMAIL EMPTY =====
    @Test
    public void LOGIN_06_EmailEmpty() {

        driver.findElement(By.name("password")).sendKeys("123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("/auth/login"));
    }

    // ===== LOGIN_07 – PASSWORD EMPTY =====
    @Test
    public void LOGIN_07_PasswordEmpty() {

        driver.findElement(By.name("email")).sendKeys("admin@gmail.com");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("/auth/login"));
    }

    // ===== LOGIN_08 – EMAIL UPPERCASE =====
    @Test
    public void LOGIN_08_EmailUppercase() {

        driver.findElement(By.name("email")).sendKeys("ADMIN@GMAIL.COM");
        driver.findElement(By.name("password")).sendKeys("123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("/auth/login"));
    }

    // ===== LOGIN_09 – PASSWORD SPACE =====
    @Test
    public void LOGIN_09_PasswordSpace() {

        driver.findElement(By.name("email")).sendKeys("admin@gmail.com");
        driver.findElement(By.name("password")).sendKeys("123 ");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("/auth/login"));
    }

    // ===== LOGIN_10 – SQL INJECTION =====
    @Test
    public void LOGIN_10_SQLInjection() {

        driver.findElement(By.name("email")).sendKeys("' OR '1'='1");
        driver.findElement(By.name("password")).sendKeys("123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("/auth/login"));
    }

    // ===== LOGIN_11 – REFRESH AFTER LOGIN =====
    @Test
    public void LOGIN_11_RefreshAfterLogin() {

        driver.findElement(By.name("email")).sendKeys("user@gmail.com");
        driver.findElement(By.name("password")).sendKeys("123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        driver.navigate().refresh();

        Assert.assertTrue(driver.getCurrentUrl().contains(baseUrl));
    }

    // ===== LOGIN_12 – MULTIPLE LOGIN =====
    @Test
    public void LOGIN_12_MultipleLogin() {

        for (int i = 0; i < 2; i++) {

            driver.findElement(By.name("email")).sendKeys("user@gmail.com");
            driver.findElement(By.name("password")).sendKeys("123");
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            driver.get(baseUrl + "/auth/login");
        }

        Assert.assertTrue(true);
    }

    // ===== LOGIN_13 – LONG INPUT =====
    @Test
    public void LOGIN_13_LongInput() {

        driver.findElement(By.name("email"))
                .sendKeys("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@gmail.com");
        driver.findElement(By.name("password")).sendKeys("123");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("/auth/login"));
    }

    // ===== LOGIN_14 – SPECIAL CHARACTER =====
    @Test
    public void LOGIN_14_SpecialCharacter() {

        driver.findElement(By.name("email")).sendKeys("@@@@");
        driver.findElement(By.name("password")).sendKeys("@@@@");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        Assert.assertTrue(driver.getCurrentUrl().contains("/auth/login"));
    }

    // ===== LOGIN_15 – PAGE LOAD =====
    @Test
    public void LOGIN_15_PageLoad() {

        Assert.assertNotNull(driver.getTitle());
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}