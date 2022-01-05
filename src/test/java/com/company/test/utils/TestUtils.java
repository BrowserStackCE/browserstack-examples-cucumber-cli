package com.company.test.utils;


import com.browserstack.runner.CucumberCLIRunner;
import com.browserstack.runner.RunCucumberTest;
import com.browserstack.runner.reporter.utils.ReportUtil;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class TestUtils {

    private static final String LOCATION_SCRIPT_FORMAT = "navigator.geolocation.getCurrentPosition = function(success){\n" +
            "    var position = { \"coords\":{\"latitude\":\"%s\",\"longitude\":\"%s\"}};\n" +
            "    success(position);\n" +
            "}" ;
    private static final String OFFER_LATITUDE = "19" ;
    private static final String OFFER_LONGITUDE = "72" ;

    private TestUtils() {
    }

    public static boolean isAscendingOrder(List<WebElement> priceWebElement, int length) {
        if (priceWebElement == null || length < 2)
            return true;
        if (Integer.parseInt(priceWebElement.get(length - 2).getText()) > Integer.parseInt(priceWebElement.get(length - 1).getText()))
            return false;
        return isAscendingOrder(priceWebElement, length - 1);
    }

    public static void mockGPS(WebDriver webDriver) {
        String locationScript = String.format(LOCATION_SCRIPT_FORMAT, OFFER_LATITUDE, OFFER_LONGITUDE);
        ((JavascriptExecutor) webDriver).executeScript(locationScript);
    }

    public static void main(String[] args) {
        System.setProperty("capabilities.config", "conf/capabilities-parallel-browsers.yml");
        (new ReportUtil()).create(new CucumberCLIRunner(RunCucumberTest.class));
    }
}
