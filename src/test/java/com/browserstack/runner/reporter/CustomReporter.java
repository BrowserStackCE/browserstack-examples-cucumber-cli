package com.browserstack.runner.reporter;

import com.browserstack.runner.reporter.model.Embedding;
import com.browserstack.runner.reporter.model.Feature;
import com.browserstack.runner.reporter.model.Scenario;
import com.browserstack.runner.reporter.utils.HtmlReportBuilder;
import com.browserstack.runner.reporter.utils.ReporterConstants;
import com.browserstack.runner.CucumberCLIRunner;
import com.browserstack.runner.utils.RunnerUtils;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CustomReporter {

    private final String reportTitle;
    private final List<Feature> reportFeatures;
    private final Instant platformCompleteTime;

    String SOURCE_ASSETS_DIRECTORY = "target/test-classes/reporter";

    public CustomReporter(String reportTitle, List<Feature> reportFeatures, Instant platformCompleteTime) {

        this.reportTitle = reportTitle;
        this.reportFeatures = reportFeatures;
        this.platformCompleteTime = platformCompleteTime;
    }

    public void create(CucumberCLIRunner customParallelRunner) throws IOException {

        RunnerUtils.createDirectories(ReporterConstants.CUSTOM_HTML_REPORTS_DIR);


        copyAssetsDirectory(SOURCE_ASSETS_DIRECTORY + "/css", ReporterConstants.CUSTOM_HTML_REPORTS_DIR + "/css");
        copyAssetsDirectory(SOURCE_ASSETS_DIRECTORY + "/js", ReporterConstants.CUSTOM_HTML_REPORTS_DIR + "/js");
        generateHtmlReport(customParallelRunner);
    }

    public void copyAssetsDirectory(String sourceDirectoryLocation, String destinationDirectoryLocation) {

        File sourceDirectory = new File(sourceDirectoryLocation);
        File destinationDirectory = new File(destinationDirectoryLocation);
        try {
            FileUtils.copyDirectory(sourceDirectory, destinationDirectory);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void generateHtmlReport(CucumberCLIRunner customParallelRunner) throws IOException {
        final long elapsedMill = (platformCompleteTime.minus(customParallelRunner.getSessionStartTime().toEpochMilli(), ChronoUnit.MILLIS)).toEpochMilli();

        String duration = String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(elapsedMill),
                TimeUnit.MILLISECONDS.toSeconds(elapsedMill) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedMill)));

        final String featureScenarioLabel = "Scenarios";

        int rerunScenarioCount =  reportFeatures.stream().flatMap(feature ->
             feature.getScenarios().stream().filter(scenario -> (scenario.isRerun()))
            ).collect(Collectors.toList()).size();


        int total, passed, failed;

            List<Scenario> scenarioList = reportFeatures.stream().flatMap(f -> f.getScenarios().stream()).collect(Collectors.toList());
            total = scenarioList.size();
            passed = (int) scenarioList.stream().filter(Scenario::passed).count();

        failed = total - passed;


        final HtmlReportBuilder htmlReportBuilder = HtmlReportBuilder.create(reportFeatures);

        final List<String> results = htmlReportBuilder.getHtmlTableFeatureRows();

        final List<String> modals = htmlReportBuilder.getHtmlModals();

        final HashMap<String, Object> reportData = new HashMap<>();
        reportData.put("reportTitle", reportTitle);
        reportData.put("label", featureScenarioLabel);
        reportData.put("total", total);
        reportData.put("passed", passed);
        reportData.put("failed", failed);
        reportData.put("rerun", rerunScenarioCount);
        reportData.put("timestamp", Instant.now().toString());
        reportData.put("duration", duration);
        reportData.put("pools", customParallelRunner.getCustomMaxRunnersCount());
        reportData.put("threads", customParallelRunner.getCucumberMaxRunnersCount());
        reportData.put("run_level", "Scenarios");
        reportData.put("os_name", System.getProperty("os.name"));
        reportData.put("os_arch", System.getProperty("os.arch"));
        reportData.put("java_version", System.getProperty("java.version"));
        reportData.put("cucumber_version", System.getProperty("cucumber.version"));
        reportData.put("tags", !customParallelRunner.getCucumberRunTags().isEmpty() ? customParallelRunner.getCucumberRunTags() : "<None>");
        reportData.put("features", customParallelRunner.getFeatureFilePath());
        reportData.put("results", results);
        reportData.put("modals", modals);

        File thisFile = new File(ReporterConstants.CUSTOM_HTML_REPORTS_DIR + "/" + reportTitle + ".html");

        BufferedWriter writer = new BufferedWriter(new FileWriter(thisFile, false));

        final InputStream in = getClass().getClassLoader().getResourceAsStream(String.format("%s/index.mustache", ReporterConstants.MUSTACHE_TEMPLATES_DIR));
        assert in != null;
        final BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        final Mustache report = new DefaultMustacheFactory().compile(reader, "");
        report.execute(writer, reportData);

        createImageScript(writer, reportFeatures);

        writer.close();
    }

    private void createImageScript(Writer writer, List<Feature> reportFeatures) throws IOException {

        final List<Embedding> embeddings = new ArrayList<>();

        reportFeatures.stream().map(Feature::getScenarios)
                .flatMap(Collection::stream)
                .flatMap(t -> t.getBefore().stream())
                .flatMap(t -> t.getEmbeddings().stream())
                .forEach(embeddings::add);

        reportFeatures.stream().map(Feature::getScenarios)
                .flatMap(Collection::stream)
                .flatMap(t -> t.getSteps().stream())
                .flatMap(t -> t.getEmbeddings().stream())
                .forEach(embeddings::add);

        reportFeatures.stream().map(Feature::getScenarios)
                .flatMap(Collection::stream)
                .flatMap(t -> t.getAfter().stream())
                .flatMap(t -> t.getEmbeddings().stream())
                .forEach(embeddings::add);

        final List<Embedding> imageEmbeddings = embeddings.stream().filter(e -> e.getMimeType().startsWith("image")).collect(Collectors.toList());

        writer.write("\n<script>\n");

        for (Embedding embedding : imageEmbeddings) {
            writer.write("document.getElementById('");
            writer.write(embedding.getEmbeddingId());
            writer.write("').src='data:image;base64,");
            writer.write(embedding.getData());
            writer.write("'\n\n");
        }

        writer.write("</script>");
    }
}
