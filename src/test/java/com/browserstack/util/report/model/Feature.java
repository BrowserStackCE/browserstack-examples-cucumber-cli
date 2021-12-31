package com.browserstack.util.report.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class Feature {
    @JsonProperty("id")
    private String featureId;
    private String name;
    private String uri;
    @JsonProperty("elements")
    private List<Scenario> scenarios = new ArrayList<>();
    private List<Tag> tags = new ArrayList<>();


    public Feature() {

    }


    public String getFeatureId() {
        return featureId;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public List<Scenario> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<Scenario> scenarios) {
        this.scenarios = scenarios;
    }

    public boolean passed() {
        return scenarios.stream().allMatch(Scenario::passed);
    }

    @Override
    public String toString() {
        return "Feature{" +
                "featureId='" + featureId + '\'' +
                ", name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                ", scenarios=" + scenarios +
                ", tags=" + tags +
                '}';
    }
}