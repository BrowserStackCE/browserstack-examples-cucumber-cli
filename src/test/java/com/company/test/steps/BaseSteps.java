package com.company.test.steps;

import com.browserstack.junit.WebDriverRunner;
import org.openqa.selenium.WebDriver;


public class BaseSteps {

    public WebDriver getWebDriver(){
        return WebDriverRunner.getWebDriver();
    }

    public String getUrl(){
        return WebDriverRunner.getTestEndpoint();
    }

}
