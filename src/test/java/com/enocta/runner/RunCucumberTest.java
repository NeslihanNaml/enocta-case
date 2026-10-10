package com.enocta.runner;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Tüm feature dosyalarını Cucumber motoruyla çalıştıran JUnit 5 suite'i.
 * Glue, rapor ve paralel koşum ayarları src/test/resources/junit-platform.properties dosyasındadır.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
public class RunCucumberTest {
}
