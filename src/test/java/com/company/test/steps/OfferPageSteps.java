package com.company.test.steps;

import io.cucumber.java.en.Then;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.Assert.assertNotNull;

public class OfferPageSteps extends BaseSteps {

    @Then("I should see Offer elements")
    public void iShouldSeeOfferElements() {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), 5);
        wait.until(ExpectedConditions.urlContains("offers"));
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".p-6")));
            Assert.assertNotNull(element);
        } catch (NoSuchElementException e) {
            throw new AssertionError("There are no offers in your region.");
        }
    }

}
