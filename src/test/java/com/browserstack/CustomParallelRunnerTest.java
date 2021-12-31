package com.browserstack;

import com.browserstack.util.report.CustomReporter;
import com.browserstack.util.report.model.Feature;
import com.browserstack.util.test.Utility;
import com.browserstack.webdriver.config.Platform;
import com.browserstack.webdriver.core.WebDriverFactory;
import io.cucumber.core.cli.CommandlineOptions;
import io.cucumber.core.cli.Main;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.browserstack.util.report.Utility.getFeatureList;

public class CustomParallelRunnerTest {

    public static List<Platform> platformList = new ArrayList<>();
    public static WebDriverFactory webDriverFactory = WebDriverFactory.getInstance();
    private static final String featureFilePath = "/src/test/resources/com/browserstack" ;


    public static void main(String[] args) throws IOException {
        int threadCount = Integer.parseInt(System.getProperty("parallel.threads", "5"));
        ExecutorService pool = Executors.newFixedThreadPool(threadCount);

        String absolutePath = Paths.get("").toAbsolutePath().toString();
        List<String> files = Utility.findFiles(Paths.get(absolutePath + featureFilePath), "feature");

        webDriverFactory.getPlatforms().forEach(platform -> {
            platformList.add(platform);
            files.forEach(featureFile ->
                    pool.submit(new Task(featureFile, platform)));
        });

        pool.shutdown();
        boolean isTerminated = false;
        try {
            isTerminated = pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (isTerminated) {

            File basePath = new File("target/platforms");
            FileFilter directoryFilter = File::isDirectory;

            File[] directoriesList = basePath.listFiles(directoryFilter);

            if (directoriesList != null) {
                for (File dir : directoriesList) {
                    List<Feature> listFeatures = getFeatureList(dir);

//                    listFeatures.get(0).getScenarios();
//                    listFeatures.get(0).setScenarios();
                    String reportTitle = dir.getName();
                    new CustomReporter(reportTitle, listFeatures).create();
                }
            }
        }
    }

    private static class Task extends CustomParallelRunnerTest implements Runnable {

        private final String featureFile;
        private final Platform platform;

        public Task(String featureFile, Platform platform) {
            this.featureFile = featureFile;
            this.platform = platform;
        }

        @Override
        public void run() {

            final String jsonReportFile = String.format("json:target/platforms/%s/%s.json"
                    , platform.getName()
                    , featureFile.substring(featureFile.lastIndexOf("/") + 1, featureFile.lastIndexOf("."))
                    );

            String[] argv = new String[]{
                    CommandlineOptions.GLUE, "com.browserstack.steps",
                    featureFile,
                    CommandlineOptions.PLUGIN,
                    jsonReportFile,
//                    CommandlineOptions.PLUGIN,
//                    "timeline:target/" + platform.getName(),
                    CommandlineOptions.THREADS,
                    "5",
//                    "PlatformName",
//                    platform.getName()

            };

            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            Main.run(argv, contextClassLoader);
            // pool-1
            // pool-2
            // pool-3

            // platform - X

            /// cucumber-runner-1-thread-1 (scenario_1 - X)
            /// cucumber-runner-1-thread-2 (scenario_1 - Y)
            /// cucumber-runner-1-thread-3 (scenario_1 - Z)
            /// cucumber-runner-2-thread-1 (scenario_2 - X)
            /// cucumber-runner-2-thread-2 (scenario_2 - Y)
            /// cucumber-runner-3-thread-3 (scenario_2 - Z)

        }
    }
}
