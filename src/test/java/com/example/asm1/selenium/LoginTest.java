package com.example.asm1.selenium;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;

public class LoginTest {

    WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("http://localhost:8080/auth/login");
    }

    @Test
    public void testLoginSuccess() {
        driver.findElement(By.name("email"))
                .sendKeys("admin@gmail.com");

        driver.findElement(By.name("password"))
                .sendKeys("123");

        driver.findElement(By.cssSelector("button[type='submit']"))
                .click();

        String currentUrl = driver.getCurrentUrl();

        assert currentUrl.contains("/");
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}