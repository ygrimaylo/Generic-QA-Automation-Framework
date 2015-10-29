package com.placester.web.steps;

import org.junit.runner.RunWith;
import cucumber.junit.Cucumber;

@RunWith(Cucumber.class)
@Cucumber.Options(features = {"src/main/features"}, tags="@PlacesterWeb")
public class StepsRunnerTest {}
