package com.optiflow.platform.searchbooking.acceptance;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME,
    value = "com.optiflow.platform.searchbooking.acceptance")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
    value = "pretty,html:target/cucumber/cucumber.html,json:target/cucumber/cucumber.json")
public class SearchBookingAcceptanceTest {
}
