package com.enocta.hooks;

import com.enocta.driver.DriverManager;
import com.enocta.utils.Pause;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;

public class Hooks {

    private final DriverManager driverManager;

    public Hooks(DriverManager driverManager) {
        this.driverManager = driverManager;
    }

    @AfterStep("@ui")
    public void pauseBetweenSteps() {
        Pause.configuredDuration();
    }

    @After("@ui")
    public void closeBrowser() {
        driverManager.quitDriver();
    }
}
