package com.browserstack.util.report.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Scenario {
    @JsonProperty("id")
    private String scenarioId;
    private String name;
    private int line;
    private String keyword;
    private List<Hook> before = new ArrayList<>();;
    private List<Hook> after = new ArrayList<>();;
    private List<Step> steps = new ArrayList<>();;
    private List<Tag> tags = new ArrayList<>();
    @JsonProperty("start_timestamp")
    private String startTimestamp;

    public Scenario() {

    }


    public String getScenarioId() {
        return scenarioId;
    }

    public String getName() {
        return name;
    }

    public String getKeyword() {
        return keyword;
    }

    public int getLine() {
        return line;
    }

    public List<Hook> getBefore() {
        return before;
    }

    public List<Hook> getAfter() {
        return after;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public String getStartTimestamp() {
        return startTimestamp;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public boolean passed() {
         return before.stream().allMatch(Hook::passed)
                && after.stream().allMatch(Hook::passed)
                 && steps.stream().allMatch(Step::passed);
    }

    @Override
    public String toString() {
        return "Scenario{" +
                "scenarioId='" + scenarioId + '\'' +
                ", name='" + name + '\'' +
                ", line=" + line +
                ", keyword='" + keyword + '\'' +
                ", before=" + before +
                ", after=" + after +
                ", steps=" + steps +
                ", tags=" + tags +
                ", startTimestamp='" + startTimestamp + '\'' +
                '}';
    }
}
