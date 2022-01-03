package com.company.test.steps;

import io.cucumber.java.en.Then;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static org.junit.Assert.assertNotEquals;

public class OrderPageSteps {

    private final StepData stepData;

    public OrderPageSteps(StepData stepData) {
        this.stepData = stepData;
    }

    @Then("I should see elements in list")
    public void iShouldSeeElementsInList() {
        WebDriverWait wait = new WebDriverWait(stepData.getWebDriver(), 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#logout")));
        WebElement element;
        try {
            element = stepData.getWebDriver().findElement(By.cssSelector("#__next > main > div > div"));
            List<WebElement> orders = element.findElements(By.tagName("div"));
            assertNotEquals(0, orders.size());
        } catch (NoSuchElementException e) {
            throw new AssertionError("There are no orders");
        }
    }

}
