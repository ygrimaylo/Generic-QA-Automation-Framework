package com.placester.services.steps;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import com.placester.web.steps.PlacesterSupport;

	public class JUnitParallelServices {
		String env = "", ver = "", command = "", currentDir = "", tag = "", brs = "", cmd = "", url = "", browser_device_version = "", os = "", os_version = "", screen_resolution = "", device = "";
		int y = 0, flag = 0, number_of_records_in_config_file = 0;
		
		@Before
		public void setUp() throws Exception { 
			//do nothing 
		}  

		@Test 
		public void executeCucumberTestsParallel() throws Exception { 
			String ConfigArray[] = null, filename = "", filename1 = "", ConfigArrayCheck[] = null;
			currentDir = System.getProperty("user.dir");
			PlacesterSupport obj = new PlacesterSupport();
			os = obj.getOS();
			final String environment_global = System.getProperty("execution_environment");
			System.out.print("Environment is " + environment_global + "\n");
			//Get number of records in config file
			int rows = obj.getBrowserStackConfigRows("BrowserStackConfigParallelRun.csv");
			number_of_records_in_config_file = rows;
			System.out.print("Number of records in BrowserStackConfigParallelRun.csv is " + rows + "\n");
			for(int x = 1; x <= rows; x++) {
				if(os.equalsIgnoreCase("windows")) {
		    		filename = currentDir + "\\src\\test\\resources\\BrowserStackConfig_" + x + ".csv";
		    		filename1 = "BrowserStackConfig_" + x + ".csv";
				}
		    	else if(os.equalsIgnoreCase("linux")) {
		    		filename = currentDir + "/src/test/resources/BrowserStackConfig_" + x + ".csv";
		    		filename1 = "BrowserStackConfig_" + x + ".csv";
		    	}
				//System.out.print("filename: " + filename +"\n");
				y = x;
				//Get records from BrowserStackConfigParallelRun.csv and write them into BrowserStackConfig.csv
				ConfigArray = obj.getBrowserStackConfigForRow("BrowserStackConfigParallelRun.csv", x);
				for(int y1 = 0; y1 < ConfigArray.length; y1++)
					System.out.print("ConfigArray[" + y1 + "]: " + ConfigArray[y1] + "\n");
				brs = ConfigArray[0].trim();
				browser_device_version = ConfigArray[1].trim();
				if(browser_device_version.indexOf(".") != -1)
					ver = browser_device_version.substring(0, browser_device_version.indexOf("."));
				else
					ver = browser_device_version;
				os = ConfigArray[2].trim();
				os_version = ConfigArray[3].trim();
				screen_resolution = ConfigArray[4].trim();
				device = ConfigArray[5].trim();
				tag = ConfigArray[6].trim();
				url = ConfigArray[7].trim();
				System.out.print("Execution tags: " + tag + "\n");
				for(int z = 1; z <= 10; z++) {
					System.out.print("Trying to create configuration file: " + filename + " with attempt number " + z + "\n");
					//Write header
					PrintWriter writer = new PrintWriter(filename, "UTF-8");
					writer.println("browser_device,browser_device_version,os,os_version,screen_resolution,device");
					writer.close();
					try
					{
						FileWriter fw = new FileWriter(filename,true); //the true will append the new data
						//appends the string to the file
						fw.write(brs + "," + browser_device_version + "," + os + "," + os_version + "," + screen_resolution + ",Samsung Galaxy S5");
						fw.close();
					}
					catch(IOException ioe)
					{
						System.out.println("IOException: " + ioe.getMessage());
					}
					ConfigArrayCheck = obj.getBrowserStackConfig(filename1);
					if(ConfigArrayCheck != null) {
						System.out.print("Configuration file: " + filename + " was created successfully\n");
						break;
					}
				}
				os = obj.getOS();
				Thread t = new Thread(new Runnable() {
					public void run(){
						Process pr = null;
						Process pr1 = null;
						if(os.equalsIgnoreCase("linux")) {
							//Writing command line execution for maven into CommandLine.sh for each browserstack thread
							PrintWriter writer = null;
							try {
								command = currentDir + "/CommandLine.sh";
								writer = new PrintWriter(command, "UTF-8");
							} catch (FileNotFoundException e1) {
								e1.printStackTrace();
							} catch (UnsupportedEncodingException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							writer.println("#!/bin/sh");
							writer.println("/usr/bin/apache-maven-3.2.5/bin/mvn verify -Dwebdriver.base.url=" + url + " -Dunlock.setting.value=" + y + " -Dcucumber.options=\"--glue classpath:com/placester/services/steps src/main/features --tags " + tag + " --format json-pretty:target/cucumber-report-myReport.json --format html:target/cucumber-html-report-myReport\" >TestResults/TestReport_thread" + y);
							writer.close();
						}
						if(os.equalsIgnoreCase("windows") && environment_global.contains("browserstack"))
							cmd = "cmd /c start mvn verify test -Dwebdriver.base.url=" + url + " -Dunlock.setting.value=" + y + " -Dcucumber.options=\"--glue classpath:com/placester/services/steps src/main/features --tags " + tag + " --format json-pretty:target/cucumber-report-myReport.json --format html:target/cucumber-html-report-myReport\"";
						else if(os.equalsIgnoreCase("windows") && environment_global.contains("local"))
							cmd = "cmd /c start mvn verify test -Dwebdriver.base.url=" + url + " -Dunlock.setting.value=" + y + " -Dcucumber.options=\"--glue classpath:com/placester/services/steps src/main/features --tags " + tag + " --format json-pretty:target/cucumber-report-myReport.json --format html:target/cucumber-html-report-myReport\"";
						else {
							cmd = "chmod u+x " + currentDir + "/CommandLine.sh";
						}
						System.out.print("CMD: " + cmd + "\n");
						try {
							pr = Runtime.getRuntime().exec(cmd);
						} catch (IOException e) {
							e.printStackTrace();
						}
						BufferedReader reader = new BufferedReader(new InputStreamReader(pr.getInputStream())); 
						String line; 
						try {
							while ((line = reader.readLine()) != null) { 
								System.out.print("line: " + line + "\n");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						int exitVal = -1;
						try {
							exitVal = pr.waitFor();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("Exited with error code "+exitVal+"\n");
						if(exitVal != 0)
							System.out.print("Failed to execute command: " + command + "\n");
						if(os.equalsIgnoreCase("linux")) {
							try {
								cmd = "./CommandLine.sh";
								System.out.print("Executing command: " + cmd +"\n");
								pr1 = Runtime.getRuntime().exec(cmd);
							} catch (IOException e) {
								e.printStackTrace();
							}
							BufferedReader reader1 = new BufferedReader(new InputStreamReader(pr1.getInputStream())); 
							String line1; 
							try {
								while ((line1 = reader1.readLine()) != null) { 
									System.out.print("line: " + line1 + "\n");
								}
							} catch (IOException e) {
							e.printStackTrace();
							}
							int exitVal1 = -1;
							try {
								exitVal1 = pr1.waitFor();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							System.out.println("Exited with error code "+exitVal1+"\n");
							if(exitVal1 != 0) {
								System.out.print("Failed to execute command: " + command + " for thread: " + y + "\n");
								flag = 1;
							}
						}
					}
				});
				t.start();
				Thread.sleep(5000);
				if(x == rows)
					t.join();
				if(flag == 1)
					Assert.fail("There are failures in tests!");
			}
		}
		@After
		public void tearDown() throws Exception {  
			//do nothing
		}
	}


