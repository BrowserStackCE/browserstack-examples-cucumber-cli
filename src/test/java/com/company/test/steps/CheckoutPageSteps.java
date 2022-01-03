package com.company.test.steps;

import io.cucumber.java.en.And;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CheckoutPageSteps {

    private final StepData stepData;

    public CheckoutPageSteps(StepData stepData) {
        this.stepData = stepData;
    }

    @And("I type {string} in Post Code")
    public void iTypeInPostCode(String postCode) {
        stepData.getWebDriver().findElement(By.cssSelector(".dynamic-form-field--postCode #provinceInput")).sendKeys(postCode);
    }

    @And("I click on Checkout Button")
    public void iClickOnCheckoutButton() {
        stepData.getWebDriver().findElement(By.id("checkout-shipping-continue")).click();
        WebDriverWait wait = new WebDriverWait(stepData.getWebDriver(), 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#checkout-app > div > div > div > div > a > button"))).click();
    }

    @And("I enter shipping details {string}, {string}, {string}, {string} and {string}")
    public void iEnterShippingDetailsAnd(String first, String last, String address, String province, String postCode) {
        WebDriverWait wait = new WebDriverWait(stepData.getWebDriver(), 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("firstNameInput"))).sendKeys(first);
        stepData.getWebDriver().findElement(By.id("lastNameInput")).sendKeys(last);
        stepData.getWebDriver().findElement(By.id("addressLine1Input")).sendKeys(address);
        stepData.getWebDriver().findElement(By.id("provinceInput")).sendKeys(province);
        stepData.getWebDriver().findElement(By.id("postCodeInput")).sendKeys(postCode);
    }
}
