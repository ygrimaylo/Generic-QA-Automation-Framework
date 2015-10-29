package com.placester.web.steps;
//This separate thread performs 5 mouse movements every 3 minutes to prevent local machine from locking. This is important, because if local machine
//is locked, screenshot for failures are not captured properly (everything in black).
import java.awt.AWTException;
import java.awt.Robot;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;

import javax.annotation.Resource;

public class UnLock implements Runnable{
	
	private static final String TASKLIST = "tasklist"; 
	@Resource
    private PlacesterSupport scenericSupport;
    	
	public void run() {
		String os = "", browser = "";
    	String currentDir = System.getProperty("user.dir");
    	if(currentDir.contains("/"))
    		os = "linux";
    	else
    		os = "windows";
    	if(os.equalsIgnoreCase("linux") && System.getProperty("webdriver.driver").trim().equalsIgnoreCase("safari")) 
    		browser = "Safari";
    	else if(os.equalsIgnoreCase("linux") && System.getProperty("webdriver.driver").trim().equalsIgnoreCase("firefox"))
    		browser = "Firefox";
    	else if(os.equalsIgnoreCase("linux") && System.getProperty("webdriver.driver").trim().equalsIgnoreCase("chrome"))
    		browser = "\"Google Chrome\"";
    	else if(os.equalsIgnoreCase("windows") && System.getProperty("webdriver.driver").trim().equalsIgnoreCase("ie"))
    		browser = "IEDriverServer.exe";
    	else if(os.equalsIgnoreCase("windows") && System.getProperty("webdriver.driver").trim().equalsIgnoreCase("firefox"))
    		browser = "firefox.exe";
    	else if(os.equalsIgnoreCase("windows") && System.getProperty("webdriver.driver").trim().equalsIgnoreCase("chrome"))
    		browser = "chromedriver.exe";
		Robot robot = null;
		int coord_x = 1029, coord_y = 624;
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true) {
			if(os.equalsIgnoreCase("linux")) {
					System.out.println("\nThere are " + checkHowManyProcessInstancesRunning("java") + " java processes running\n");
					System.out.println("\nThere are " + checkHowManyProcessInstancesRunning(browser) + " " + browser + " processes running\n");
			}
			else if(os.equalsIgnoreCase("windows")) {
				System.out.println("\nThere are " + checkHowManyProcessInstancesRunning("java.exe") + " java.exe processes running\n");
				System.out.println("\nThere are " + checkHowManyProcessInstancesRunning(System.getProperty("webdriver.driver").trim()) + " " + System.getProperty("webdriver.driver").trim() + "  processes running\n");
			}
			java.util.Date date= new java.util.Date();
			System.out.println("Performing mouse movements to prevent local machine from locking on " + new Timestamp(date.getTime()));
			for(int x = 1; x <= 10; x++) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(x % 2 == 0)
					robot.mouseMove(coord_x + 50, coord_y + 50);
				else
					robot.mouseMove(coord_x - 50, coord_y - 50);
				//robot.mousePress(InputEvent.BUTTON1_MASK);
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//robot.mouseRelease(InputEvent.BUTTON1_MASK);
			}
			try {
				Thread.sleep(200000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				if(os.equalsIgnoreCase("windows") && checkHowManyProcessInstancesRunning(browser) == 0 && checkHowManyProcessInstancesRunning("java.exe") == 0) {
					System.out.println(browser + " is not running, exiting unlock mechanism, which means: no more mouse movements\n");
					break;
				}
				else if(os.equalsIgnoreCase("linux") && checkHowManyProcessInstancesRunning(browser) == 0 && checkHowManyProcessInstancesRunning("java") == 0) {
					System.out.println(browser + " is not running, exiting unlock mechanism, which means: no more mouse movements\n");
					break;
				}
			}
			catch(Exception e) {};
		}
    }
	public int checkHowManyProcessInstancesRunning(String processName)
	{
		int counter = 0;
		String os = "";
		String currentDir = System.getProperty("user.dir");
		if(currentDir.contains("/"))
    		os = "linux";
    	else
    		os = "windows";
		if(os.equalsIgnoreCase("windows")) {
			Process p = null;
			try {
				p = Runtime.getRuntime().exec(TASKLIST);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader( 
					p.getInputStream())); 
			String line; 
			try {
				while ((line = reader.readLine()) != null) { 
					if (line.contains(processName)) { 
						counter++;
					} 
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			Process p = null;
			try {
				p = Runtime.getRuntime().exec("ps -e");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader( 
					p.getInputStream())); 
			String line; 
			try {
				while ((line = reader.readLine()) != null) { 
					//System.out.print("line: " + line + "\n");
					if(line.contains(processName))
						counter++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}			
		return counter;
	}
}
