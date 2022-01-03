package com.company.test.hooks;

import com.browserstack.runner.listener.ThreadObjects;
import com.company.test.steps.StepData;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import static com.browserstack.runner.CucumberCLIRunner.webDriverFactory;
import static com.browserstack.runner.listener.TestEventListener.threadIdObjectsMap;


public class TestHooks {

    public static final String LOG_PLATFORM_FOR_REPORT = "Platform: %s" ;
    private final StepData stepData;

    ThreadObjects threadObjects = threadIdObjectsMap.get(Thread.currentThread().getId());

    public TestHooks(StepData stepData) {
        this.stepData = stepData;
    }

    @Before
    public void setup(Scenario scenario) {

        scenario.log(String.format(LOG_PLATFORM_FOR_REPORT, threadObjects.getPlatform().getName()));

        stepData.setWebDriver(threadObjects.getDriver());
        stepData.setUrl(webDriverFactory.getTestEndpoint());

    }


    @After
    public void teardown(Scenario scenario) {
        scenario.log(String.format(LOG_PLATFORM_FOR_REPORT, threadObjects.getPlatform().getName()));

        if (scenario.isFailed()) {

            final byte[] screenshot = ((TakesScreenshot) threadObjects.getDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", scenario.getId() + "-" + threadObjects.getPlatform().getName());

        }
    }
}
