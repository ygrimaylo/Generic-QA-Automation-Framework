package com.placester.services.steps;

import org.junit.runner.RunWith;
import cucumber.junit.Cucumber;

@RunWith(Cucumber.class)
@Cucumber.Options(features = {"src/main/features"}, tags="@Placester_Services_API_Billing_Coupon")
public class StepsRunnerTest {}
