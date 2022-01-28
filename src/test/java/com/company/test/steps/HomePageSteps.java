package com.company.test.steps;

import com.company.test.utils.TestUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class HomePageSteps extends BaseSteps {


    @And("I add two products to cart")
    public void iAddProductsToCart() {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#\\31 > .shelf-item__buy-btn"))).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.float-cart__close-btn"))).click();
        getWebDriver().findElement(By.cssSelector("#\\32 > .shelf-item__buy-btn")).click();
    }

    @And("I click on Buy Button")
    public void iClickOnBuyButton() {
        getWebDriver().findElement(By.cssSelector(".buy-btn")).click();
    }

    @And("I press the Apple Vendor Filter")
    public void iPressTheAppleVendorFilter() {
        getWebDriver().findElement(By.cssSelector(".filters-available-size:nth-child(2) .checkmark")).click();
    }

    @And("I order by lowest to highest")
    public void iOrderByLowestToHighest() {
        WebElement dropdown = getWebDriver().findElement(By.cssSelector("select"));
        dropdown.findElement(By.cssSelector("option[value = 'lowestprice']")).click();
    }

    @Then("I should see user {string} logged in")
    public void iShouldUserLoggedIn(String user) {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), 5);
        try {
            String loggedInUser = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".username"))).getText();
            Assert.assertEquals(user, loggedInUser);
        } catch (NoSuchElementException e) {
            throw new AssertionError(user + " is not logged in");
        }
    }

    @Then("I should see product images loaded")
    public void iShouldSeeProductImagesLoaded() {
        try {
            WebDriverWait wait = new WebDriverWait(getWebDriver(), 5);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout")));
            String src = getWebDriver().findElement(By.cssSelector("img[alt='iPhone 12']")).getAttribute("src");
            Assert.assertNotEquals("", src);
        } catch (NoSuchElementException e) {
            throw new AssertionError("Error in page load");
        }
    }

    @Then("I should see {int} items in the list")
    public void iShouldSeeItemsInTheList(int productCount) {
        try {
            WebDriverWait webDriverWait = new WebDriverWait(getWebDriver(), 5);
            webDriverWait.until(waitWebDriver -> waitWebDriver.findElements(By.cssSelector(".spinner")).isEmpty());
            List<String> values = getWebDriver().findElements(By.cssSelector(".shelf-item__title")).stream().map(WebElement::getText).collect(Collectors.toList());
            Assert.assertEquals(productCount, values.size());
        } catch (NoSuchElementException e) {
            throw new AssertionError("Error in page load");
        }
    }

    @Then("I should see prices in ascending order")
    public void iShouldSeePricesInAscendingOrder() {
        try {
            WebDriverWait webDriverWait = new WebDriverWait(getWebDriver(), 5);
            webDriverWait.until(waitWebDriver -> waitWebDriver.findElements(By.cssSelector(".spinner")).isEmpty());
            List<WebElement> priceWebElement = getWebDriver().findElements(By.cssSelector(".shelf-item__price > div.val > b"));
            Assert.assertTrue(TestUtils.isAscendingOrder(priceWebElement, priceWebElement.size()));
        } catch (NoSuchElementException e) {
            throw new AssertionError("Error in page load");
        }
    }

}
