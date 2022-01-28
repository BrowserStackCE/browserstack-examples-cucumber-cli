package com.company.test.runner;

import com.browserstack.junit.WebDriverOptions;
import com.browserstack.junit.WebDriverRunner;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(WebDriverRunner.class)
@WebDriverOptions(
        thread = 25,
        rerun = true,
        cucumberOptions = @CucumberOptions(
                features = "src/test/resources/features",
                glue = "com.company.test",

                plugin = {"pretty",
                        "com.browserstack.report.CustomReportListener:custom/reports",
                        "com.browserstack.rerun.RerunExecutionManager:2"},
                monochrome = true
        )
)
public class RunCucumberTest {

}

