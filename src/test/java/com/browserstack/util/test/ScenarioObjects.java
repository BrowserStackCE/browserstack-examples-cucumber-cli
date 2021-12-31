package com.browserstack.util.test;

import com.browserstack.webdriver.config.Platform;
import java.util.Stack;

public class ScenarioObjects {


    String scenarioId;
    Stack<Platform> platformStack;

    public ScenarioObjects(String scenarioId, Stack<Platform> platformStack) {

        this.scenarioId = scenarioId;
        this.platformStack = platformStack;
    }

    public Stack<Platform> getPlatformStack() {
        return platformStack;
    }

    public void setPlatformStack(Stack<Platform> platformStack) {
        this.platformStack = platformStack;
    }


    @Override
    public String toString() {
        return "ScenarioObjects{" +
                "scenarioId: " + scenarioId +
                ", platformStack=" + platformStack +
                '}';
    }
}
