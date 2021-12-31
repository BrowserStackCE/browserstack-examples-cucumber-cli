package com.browserstack.util.report;

import com.browserstack.util.report.model.*;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;

import java.io.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;


public class CustomReporter {
    private final String targetDir;
    private final String reportDir;
    private final String reportTitle;

    private final List<Feature> reportFeatures;

    public CustomReporter(String reportTitle, List<Feature> reportFeatures) {

        this.targetDir = "target";
        this.reportTitle = reportTitle;
        this.reportDir = targetDir + "/reports";
        this.reportFeatures = reportFeatures;
    }

    public void create() throws IOException {
        createReportDirectories();
        generateHtmlReport();
    }

    private void generateHtmlReport() throws IOException {
        final long elapsedMill = 0L;

        String duration = String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(elapsedMill),
                TimeUnit.MILLISECONDS.toSeconds(elapsedMill) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(elapsedMill)));

        final String featureScenarioLabel = "Scenarios";

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
        reportData.put("rerun", "0");
        reportData.put("timestamp", Instant.now().toString());
        reportData.put("duration", duration);
        reportData.put("threads", "threads");
        reportData.put("run_level", "scenarios");
        reportData.put("cucumber_report", "");
        reportData.put("os_name", System.getProperty("os.name"));
        reportData.put("os_arch", System.getProperty("os.arch"));
        reportData.put("java_version", System.getProperty("java.version"));
        reportData.put("tags", "tags");
        reportData.put("features", "features");
        reportData.put("results", results);
        reportData.put("modals", modals);

        File thisFile = new File(reportDir + "/" + reportTitle + ".html");

        BufferedWriter writer = new BufferedWriter(new FileWriter(thisFile, false));

        final InputStream in = getClass().getResourceAsStream("/report/templates/index.mustache");
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

    private void createReportDirectories() throws IOException {
        final File targetDir = new File(this.targetDir);

        if (!targetDir.exists()) {
            if (!targetDir.mkdirs()) {
                throw new IOException(String.format("Unable to create the '%s' directory", targetDir));
            }
        }

        final File reportDir = new File(this.reportDir);

        if (!reportDir.exists()) {
            if (!reportDir.mkdir()) {
                throw new IOException("Unable to create the '../courgette-report' directory");
            }
        }
    }
}


class HtmlReportBuilder {

    private static final String PASSED = "Passed";
    private static final String FAILED = "Failed";
    private static final String SUCCESS = "success";
    private static final String DANGER = "danger";
    private static final String WARNING = "warning";
    private static final String DATA_TARGET = "data_target";
    private static final String FEATURE_NAME = "feature_name";
    private static final String FEATURE_BADGE = "feature_badge";
    private static final String FEATURE_RESULT = "feature_result";
    private static final String FEATURE_SCENARIOS = "feature_scenarios";
    private static final String SCENARIO_NAME = "scenario_name";
    private static final String SCENARIO_BADGE = "scenario_badge";
    private static final String SCENARIO_RESULT = "scenario_result";
    private static final String SCENARIO_TAGS = "scenario_tags";
    private static final String MODAL_TARGET = "modal_target";
    private static final String MODAL_HEADING = "modal_heading";
    private static final String MODAL_FEATURE_LINE = "modal_feature_line";
    private static final String MODAL_BODY = "modal_body";
    private static final String STEP_KEYWORD = "step_keyword";
    private static final String STEP_NAME = "step_name";
    private static final String STEP_DURATION = "step_duration";
    private static final String STEP_BADGE = "step_badge";
    private static final String STEP_RESULT = "step_result";
    private static final String STEP_DATATABLE = "step_datatable";
    private static final String DATATABLE = "datatable";
    private static final String STEP_EXCEPTION = "step_exception";
    private static final String EXCEPTION = "exception";
    private static final String STEP_OUPUT = "step_output";
    private static final String OUTPUT = "output";
    private static final String STEP_EMBEDDING_TEXT = "step_embedding_text";
    private static final String TEXT = "text";
    private static final String STEP_EMBEDDING_IMAGE = "step_embedding_image";
    private static final String IMAGE_ID = "img_id";
    private static final String ROW_INFO = "row_info";
    private static final String TAG = "tag";

    private List<Feature> featureList;

    private Mustache featureTemplate;
    private Mustache modalTemplate;
    private Mustache modalStepTemplate;
    private Mustache modalEnvironmentTemplate;
    private Mustache modalRowTemplate;
    private Mustache scenarioTemplate;
    private Mustache scenarioTagTemplate;

    private HtmlReportBuilder(List<Feature> featureList) {

        this.featureList = featureList;

        this.featureTemplate = readTemplate("/report/templates/feature.mustache");
        this.modalTemplate = readTemplate("/report/templates/modal.mustache");
        this.modalStepTemplate = readTemplate("/report/templates/modal_step.mustache");
        this.modalEnvironmentTemplate = readTemplate("/report/templates/modal_environment.mustache");
        this.modalRowTemplate = readTemplate("/report/templates/modal_row.mustache");
        this.scenarioTemplate = readTemplate("/report/templates/scenario.mustache");
        this.scenarioTagTemplate = readTemplate("/report/templates/scenario_tag.mustache");
    }

    public static HtmlReportBuilder create(List<Feature> featureList) {

        return new HtmlReportBuilder(featureList);
    }

    public List<String> getHtmlTableFeatureRows() {
        final List<String> featureRows = new ArrayList<>(featureList.size());
        featureList.forEach(feature -> featureRows.add(createFeatureRow(feature)));
        return featureRows;
    }

    public List<String> getHtmlModals() {
        final int modalCapacity = (int) featureList.stream().map(Feature::getScenarios).count() + 1;

        final List<String> modals = new ArrayList<>(modalCapacity);

        modals.add(createEnvironmentInfoModal());

        featureList
                .forEach(feature -> {
                    List<Scenario> scenarios = feature.getScenarios();
                    scenarios.forEach(scenario -> modals.add(createScenarioModal(feature, scenario)));
                });

        return modals;
    }

    private String createFeatureRow(Feature feature) {

        final LinkedHashMap<String, Object> featureData = new LinkedHashMap<>();

        String featureId = feature.getFeatureId();
        String featureName = feature.getName();
        String featureBadge = feature.passed() ? SUCCESS : DANGER;
        String featureResult = featureBadge.equals(SUCCESS) ? PASSED : FAILED;

        LinkedList<String> scenarioRows = new LinkedList<>();
        createScenarios(feature, scenarioRows);

        featureData.put(DATA_TARGET, featureId);
        featureData.put(FEATURE_NAME, featureName);
        featureData.put(FEATURE_BADGE, featureBadge);
        featureData.put(FEATURE_RESULT, featureResult);
        featureData.put(FEATURE_SCENARIOS, scenarioRows);

        return createFromTemplate(featureTemplate, featureData);
    }

    private void createScenarios(Feature feature, LinkedList<String> scenarioRows) {
        feature.getScenarios().forEach(scenario -> {
            if (!scenario.getKeyword().equalsIgnoreCase("Background")) {
                scenarioRows.add(createScenarioRow(feature.getFeatureId(), scenario));
            }
        });
    }

    private List<String> createScenarioTags(Scenario scenario) {

        final LinkedList<String> scenarioTags = new LinkedList<>();

        scenario.getTags().forEach(tag -> {
            final LinkedHashMap<String, Object> scenarioTagData = new LinkedHashMap<>();
            scenarioTagData.put(TAG, tag.getName());
            scenarioTags.add(createFromTemplate(scenarioTagTemplate, scenarioTagData));
        });

        return scenarioTags;
    }

    private String createScenarioRow(String featureId, Scenario scenario) {

        final LinkedHashMap<String, Object> scenarioData = new LinkedHashMap<>();

        String scenarioId = scenario.getScenarioId();
        String scenarioName = scenario.getName();
        String scenarioBadge = scenario.passed() ? SUCCESS : DANGER;
        String scenarioResult = scenarioBadge.equals(SUCCESS) ? PASSED : FAILED;

        scenarioData.put(DATA_TARGET, featureId);
        scenarioData.put(MODAL_TARGET, scenarioId);
        scenarioData.put(SCENARIO_NAME, scenarioName);
        scenarioData.put(SCENARIO_BADGE, scenarioBadge);
        scenarioData.put(SCENARIO_RESULT, scenarioResult);
        scenarioData.put(SCENARIO_TAGS, createScenarioTags(scenario));

        return createFromTemplate(scenarioTemplate, scenarioData);
    }

    private String createScenarioModal(Feature feature, Scenario scenario) {
        final String featureName = feature.getUri().substring(feature.getUri().lastIndexOf("/") + 1);

        final LinkedHashMap<String, Object> modalData = new LinkedHashMap();

        modalData.put(MODAL_TARGET, scenario.getScenarioId());
        modalData.put(MODAL_HEADING, scenario.getName());
        modalData.put(MODAL_FEATURE_LINE, featureName + " - line " + scenario.getLine());

        List<String> modalBody = new ArrayList<>();

        scenario.getBefore().forEach(hook -> modalBody.add(createRowFromHook(hook)));
        scenario.getSteps().forEach(step -> modalBody.add(createRowFromStep(step)));
        scenario.getAfter().forEach(hook -> modalBody.add(createRowFromHook(hook)));

        modalData.put(MODAL_BODY, modalBody);

        return createFromTemplate(modalTemplate, modalData);
    }

    private String createRowFromHook(Hook hook) {

        final LinkedHashMap<String, Object> hookData = new LinkedHashMap<>();

        String stepStatusBadge = statusBadge.apply(hook.getResult());

        String stepResult = statusLabel.apply(hook.getResult());

        hookData.put(STEP_NAME, hook.getLocation());
        hookData.put(STEP_DURATION, hook.getResult().getDuration());
        hookData.put(STEP_BADGE, stepStatusBadge);
        hookData.put(STEP_RESULT, stepResult);

        if (hook.getResult().getErrorMessage() != null) {
            addNestedMap(hookData, STEP_EXCEPTION, EXCEPTION, hook.getResult().getErrorMessage());
        }

        if (!hook.getOutput().isEmpty()) {
            addNestedMap(hookData, STEP_OUPUT, OUTPUT, hook.getOutput());
        }

        hook.getEmbeddings().forEach(embedding -> {

            if (embedding.getMimeType().equals("text/html")) {
                String htmlData = new String(Base64.getDecoder().decode(embedding.getData()));
                addNestedMap(hookData, STEP_EMBEDDING_TEXT, TEXT, htmlData);

            } else if (embedding.getMimeType().startsWith("image")) {
                addNestedMap(hookData, STEP_EMBEDDING_IMAGE, IMAGE_ID, embedding.getEmbeddingId());
            }
        });

        return createFromTemplate(modalStepTemplate, hookData);
    }

    private String createRowFromStep(Step step) {

        final LinkedHashMap<String, Object> stepData = new LinkedHashMap<>();

        String stepStatusBadge = statusBadge.apply(step.getResult());

        String stepResult = statusLabel.apply(step.getResult());

        stepData.put(STEP_KEYWORD, step.getKeyword());
        stepData.put(STEP_NAME, step.getName());
        stepData.put(STEP_DURATION, step.getResult().getDuration());
        stepData.put(STEP_BADGE, stepStatusBadge);
        stepData.put(STEP_RESULT, stepResult);

        if (step.getRowData() != null) {
            addNestedMap(stepData, STEP_DATATABLE, DATATABLE, step.getRowData());
        }

        if (step.getResult().getErrorMessage() != null) {
            addNestedMap(stepData, STEP_EXCEPTION, EXCEPTION, step.getResult().getErrorMessage());
        }

        if (!step.getOutput().isEmpty()) {
            addNestedMap(stepData, STEP_OUPUT, OUTPUT, step.getOutput());
        }

        step.getEmbeddings().forEach(embedding -> {

            if (embedding.getMimeType().equals("text/html")) {
                String htmlData = new String(Base64.getDecoder().decode(embedding.getData()));

                addNestedMap(stepData, STEP_EMBEDDING_TEXT, TEXT, htmlData);

            } else if (embedding.getMimeType().startsWith("image")) {
                addNestedMap(stepData, STEP_EMBEDDING_IMAGE, IMAGE_ID, embedding.getEmbeddingId());
            }
        });

        return createFromTemplate(modalStepTemplate, stepData);
    }

    private String createModalRow(String rowInfo) {

        final LinkedHashMap<String, Object> rowInfoData = new LinkedHashMap();
        rowInfoData.put(ROW_INFO, rowInfo);

        return createFromTemplate(modalRowTemplate, rowInfoData);
    }

    private String createEnvironmentInfoModal() {
        final List<String> envData = new ArrayList<>();

        //final String envInfo = courgetteProperties.getCourgetteOptions().environmentInfo().trim();

//        final String[] values = envInfo.split(";");
//
//        for (String value : values) {
//            String[] keyValue = value.trim().split("=");
//
//            if (keyValue.length == 2) {
//                envData.add(keyValue[0].trim() + " = " + keyValue[1].trim());
//            }
//        }

        if (envData.isEmpty()) {
            envData.add("No additional environment information provided.");
        }

        final LinkedHashMap<String, Object> environmentInfoData = new LinkedHashMap<>();

        final LinkedList<String> rowInfo = new LinkedList();
        envData.forEach(info -> rowInfo.add(createModalRow(info)));

        environmentInfoData.put(MODAL_BODY, rowInfo);

        return createFromTemplate(modalEnvironmentTemplate, environmentInfoData);
    }

    private static Function<Result, String> statusLabel = (result) -> result.getStatus().substring(0, 1).toUpperCase() + result.getStatus().substring(1);

    private static Function<Result, String> statusBadge = (result) -> {
        String status = result.getStatus();
        return status.equalsIgnoreCase(PASSED) ? SUCCESS : status.equalsIgnoreCase(FAILED) ? DANGER : WARNING;
    };

    private String createFromTemplate(Mustache template, Object data) {
        Writer writer = new StringWriter();
        template.execute(writer, data);
        return writer.toString();
    }

    private static void addNestedMap(HashMap<String, Object> source, String sourceKey,
                                     String childKey, Object childValue) {

        HashMap<String, Object> map = new HashMap<>();
        map.put(childKey, childValue);

        source.put(sourceKey, map);
    }

    private Mustache readTemplate(String template) {
        StringBuilder templateContent = new StringBuilder();

        try {
            final InputStream in = getClass().getResourceAsStream(template);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                templateContent.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new DefaultMustacheFactory().compile(new StringReader(templateContent.toString()), "");
    }
}
