package com.browserstack.steps;

import com.browserstack.CustomParallelRunnerTest;
import com.browserstack.util.test.ScenarioObjects;
import com.browserstack.webdriver.config.Platform;
import com.browserstack.webdriver.core.WebDriverFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.java.*;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeStep;


public class TestHooks {

    private static final WebDriverFactory webDriverFactory = WebDriverFactory.getInstance();
    public static final Map<String, Stack<Platform>> scenarioIdPlatformStackMap = new ConcurrentHashMap<>();
    private static String driverType;

    private final StepData stepData;
    private WebDriver webDriver;
    private Platform platform;

    public TestHooks(StepData stepData) {
        this.stepData = stepData;
    }

    @Before
    public void setup(Scenario scenario) {
        String scenarioId = scenario.getUri().toString() + "_" + scenario.getLine();
        Stack<Platform> scenarioPlatformStack = scenarioIdPlatformStackMap.get(scenarioId);

       // System.out.println("Setup - Test case id: " + scenarioId + "__" + Thread.currentThread().getId());

        if (scenarioPlatformStack == null) {
            List<Platform> shufflePlatforms = new ArrayList<>();
            shufflePlatforms.addAll(CustomParallelRunnerTest.platformList);
            //Collections.shuffle(shufflePlatforms);

            Stack<Platform> platformStack = new Stack<>();
            platformStack.addAll(shufflePlatforms);

            scenarioIdPlatformStackMap.put(scenarioId, platformStack);
            scenarioPlatformStack = scenarioIdPlatformStackMap.get(scenarioId);
        }

        platform = scenarioPlatformStack.pop();
        System.out.println("Platform is" + platform.getName() + ", Scenario id: " + scenarioId);
        webDriver = webDriverFactory.createWebDriverForPlatform(platform, scenario.getName());
        stepData.setWebDriver(webDriver);

        driverType = webDriverFactory.getDriverType().toString();

        stepData.setUrl(webDriverFactory.getTestEndpoint());
        if (platform.getRealMobile() != null && !platform.getRealMobile()) {
            webDriver.manage().window().maximize();
        }


//        scenario.log(String.format("Current scenario and platform details: %s, - %s", scenario.getId(), platform.getName()));
//        final byte[] screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
//        scenario.attach(screenshot, "image/png", scenario.getId() + "-" + platform.getName());
    }

//    @BeforeStep
//    public void before(Scenario scenario) {
//
//        scenario.log(String.format("Current step, scenario and platform details: %s, - %s", scenario.getId(), platform.getName()));
//        final byte[] screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
//        scenario.attach(screenshot, "image/png", scenario.getId() + "-" + platform.getName());
//
//    }
//
//
//    @AfterStep
//    public void after(Scenario scenario) {
//
//        scenario.log(String.format("Current step, scenario and platform details: %s, - %s", scenario.getId(), platform.getName(), webDriver. ));
//        final byte[] screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
//        scenario.attach(screenshot, "image/png", scenario.getId() + "-" + platform.getName());
//
//    }


    @After
    public void teardown(Scenario scenario) {
        //if (scenario.isFailed()) {
        scenario.log(String.format("Current scenario and platform details: %s, - %s", scenario.getId(), platform.getName()));
        final byte[] screenshot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
        scenario.attach(screenshot, "image/png", scenario.getId() + "-" + platform.getName());

        markAndCloseWebDriver(scenario);

        //}
    }

    public void markAndCloseWebDriver(Scenario scenario) {

        //WebDriver webDriver = TestHooks.threadIdDriverMap.get(Thread.currentThread().getId());
        try {

            if ("cloudDriver".equalsIgnoreCase(driverType)) {
                String status = "passed" ;
                String reason = "Test Passed" ;
                if (scenario.isFailed()) {
                    status = "failed" ;
                    reason = "Test Failed" ;//testCaseFinished.getResult().getError().toString();
                }
                String script = createExecutorScript(status, reason);
                if (StringUtils.isNotEmpty(script)) {
                    ((JavascriptExecutor) webDriver).executeScript(script);
                }
            }
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }
    }

    private String createExecutorScript(String status, String reason) {
        final ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.createObjectNode();
        ObjectNode argumentsNode = objectMapper.createObjectNode();

        // Read only the first line of the error message
        reason = reason.split("\n")[0];
        // Limit the error message to only 255 characters
        if (reason.length() >= 255) {
            reason = reason.substring(0, 255);
        }
        // Replacing all the special characters with whitespace
        reason = reason.replaceAll("^[^a-zA-Z0-9]", " ");

        argumentsNode.put("status", status);
        argumentsNode.put("reason", reason);

        rootNode.put("action", "setSessionStatus");
        rootNode.set("arguments", argumentsNode);
        String executorStr;
        try {
            executorStr = objectMapper.writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            throw new Error("Error creating JSON object for Marking tests", e);
        }
        return "browserstack_executor: " + executorStr;
    }

}
