package com.company.test.steps;

import org.openqa.selenium.WebDriver;


public class StepData {

    private WebDriver webDriver;
    private String url;

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void setWebDriver(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
