package com.browserstack.util.report;

import com.browserstack.util.report.model.Feature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Utility {

    public static List<Feature> getFeatureList(File dir) throws IOException {

        FilenameFilter jsonFileFilter = (file, name) -> name.toLowerCase().endsWith(".json");

        String[] filesList = dir.list(jsonFileFilter);
        List<Feature> listFeatures = new ArrayList<>();
        assert filesList != null;
        for (String fileName : filesList) {
            String jsonString = FileUtils.readFileToString(new File(dir.getAbsolutePath() + "/" + fileName), StandardCharsets.UTF_8.toString());
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            listFeatures.add(objectMapper.readValue(jsonString, new TypeReference<List<Feature>>() {
            }).get(0));
        }

        return listFeatures;

    }


}



