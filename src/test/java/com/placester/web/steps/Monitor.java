package com.placester.web.steps;
//This separate thread monitors if the browser process runs for too long, which is indication of stuck webdriver problem,
//and then execute at run time bash script, which kills unix/mac process
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.annotation.Resource;

public class Monitor implements Runnable{
	
	private static final String TASKLIST = "tasklist"; 
	@Resource
    private PlacesterSupport scenericSupport;
    	
	public void run() {
		String os = "", browser = "";
		long totalTime = 0, startTime = 0, endTime = 0;
		int how_many_processes = 0;
		Process pr1 = null;
		Process pr = null;
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
    	else if(os.equalsIgnoreCase("windows") && System.getProperty("webdriver.driver").trim().equalsIgnoreCase("chrome"))
    		browser = "chromedriver.exe";
    	else if(os.equalsIgnoreCase("windows") && System.getProperty("webdriver.driver").trim().equalsIgnoreCase("firefox"))
    		browser = "firefox.exe";
		while(true) {
			startTime = System.currentTimeMillis();
			how_many_processes = checkHowManyProcessInstancesRunning(browser);
			if(how_many_processes != 0) {
				endTime = System.currentTimeMillis(); 
				//System.out.print("\nDetected that process: " + browser + " is running\n");
				totalTime = totalTime + (endTime - startTime);
			}
			else {
				//System.out.print("\nDetected that process: " + browser + " is not running\n");
				totalTime = 0;
				startTime = 0;
				endTime = 0;
			}
			//System.out.print("Total_time: " + totalTime + "\n");
    	    if(totalTime > 1800*1000) {
    	    	if(os.equalsIgnoreCase("linux")) {
    	    		// kill process on linux/mac
    	    		System.out.print("\nSetting up permissions for KillProcess.sh\n");
    	    		try {
    	    			pr1 = Runtime.getRuntime().exec("chmod u+x " + currentDir + "/KillProcess.sh");
    	    		} catch (IOException e) {
    	    			// TODO Auto-generated catch block
    	    			e.printStackTrace();
    	    		}
    	    	}
				BufferedReader reader = new BufferedReader(new InputStreamReader(pr1.getInputStream())); 
    			String line; 
    			try {
    				while ((line = reader.readLine()) != null) { 
    					System.out.print("line: " + line + "\n");
    				}
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    			if(os.equalsIgnoreCase("linux")) {
    				System.out.print("\nKilling process java by executing KillProcess.sh bash script with process_name as command line argument\n");
    				try {
    					pr = Runtime.getRuntime().exec(currentDir + "/KillProcess.sh java");
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    			}
    	    	BufferedReader reader1 = new BufferedReader(new InputStreamReader(pr.getInputStream())); 
    			String line1; 
    			try {
    				while ((line1 = reader1.readLine()) != null) { 
    					System.out.print("line1: " + line1 + "\n");
    				}
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    	    	startTime = 0;
    	    	endTime = 0;
    	    	totalTime = 0;
    	    }
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
