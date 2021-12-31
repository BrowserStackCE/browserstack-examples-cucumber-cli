package com.browserstack.util.report.model;

import java.util.ArrayList;
import java.util.List;

import static io.cucumber.core.backend.Status.*;

public class Step {
    private String name;
    private String keyword;
    private Result result;
    private List<Hook> before = new ArrayList<>();
    private List<Hook> after = new ArrayList<>();
    private List<Embedding> embeddings = new ArrayList<>();
    private List<String> output = new ArrayList<>();
    private List<String> rowData = new ArrayList<>();

    public Step () {

    }

    public Step(String name,
                String keyword,
                Result result,
                List<Hook> before,
                List<Hook> after,
                List<Embedding> embeddings,
                List<String> output,
                List<String> rowData) {
        this.name = name;
        this.keyword = keyword;
        this.result = result;
        this.before = before;
        this.after = after;
        this.embeddings = embeddings;
        this.output = output;
        this.rowData = rowData;
    }

    public Step(String name,
                String keyword) {
        this.name = name;
        this.keyword = keyword;

    }

    public String getName() {
        return name;
    }

    public String getKeyword() {
        return keyword;
    }

    public Result getResult() {
        return result;
    }

    public List<Hook> getBefore() {
        return before;
    }

    public List<Hook> getAfter() {
        return after;
    }

    public List<Embedding> getEmbeddings() {
        return embeddings;
    }

    public List<String> getOutput() {
        return output;
    }

    public List<String> getRowData() {
        return rowData;
    }

    public boolean passed() {
        return result.getStatus().equalsIgnoreCase(PASSED.toString()) || result.getStatus().equalsIgnoreCase(SKIPPED.toString());
    }

//    public boolean skipped() {
//        return result.getStatus().equalsIgnoreCase(SKIPPED.toString()) || result.getStatus().equalsIgnoreCase(AMBIGUOUS.toString());
//    }

    @Override
    public String toString() {
        return "Step{" +
                "name='" + name + '\'' +
                ", keyword='" + keyword + '\'' +
                ", result=" + result +
                ", before=" + before +
                ", after=" + after +
                ", embeddings=" + embeddings +
                ", output=" + output +
                ", rowData=" + rowData +
                '}';
    }
}