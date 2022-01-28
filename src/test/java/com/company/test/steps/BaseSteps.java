package com.company.test.steps;

import com.browserstack.junit.WebDriverRunner;
import org.openqa.selenium.WebDriver;


public class BaseSteps {

    public WebDriver getWebDriver(){
        return WebDriverRunner.getWebDriver();
    }

    public String getUrl(){
        System.out.println("Test URL: " + WebDriverRunner.getTestEndpoint());
        return WebDriverRunner.getTestEndpoint(); // "https://bstackdemo.com";
    }

}
