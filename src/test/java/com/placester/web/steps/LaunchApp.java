package com.placester.web.steps;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import javax.annotation.Resource;
import javax.swing.JOptionPane;
import org.openqa.selenium.By;
import junit.framework.Assert;
import cucumber.annotation.After;
import cucumber.annotation.Before;
import cucumber.annotation.en.Given;
import com.placester.web.steps.PlacesterSupport;

public class LaunchApp {
	@Resource
    private WebDriverSupport webDriverSupport;
    @Resource
    private PlacesterSupport placesterSupport;
    @Resource
    String environment;
    @Resource
    private String baseUrl;
    @Resource
    String unlock;
    public static StringBuffer unlockFlag = new StringBuffer("");
    public static StringBuffer displayPopupDialogFlag = new StringBuffer("");
    public static StringBuffer skipTestCase = new StringBuffer("");
    public static StringBuffer GetBrowserStackResultsURL= new StringBuffer("");
    private String issues = "";
    public File f = null;
        
    @Given("^I launch Placester$")
    public void launchPlacester() {
        int timeout = 30, counter = 0, flag = 0;
        String brs = System.getProperty("webdriver.driver");
        String os = placesterSupport.getOS();
        String ConfigArray[] = null;
        String env = placesterSupport.getEnvironment();
        String currentDir = System.getProperty("user.dir");
        String environment_global = environment;
        String device_model = "", exception_msg = "", error_msg = "";
        String os_version = "", filename = "", brs_version = "", title = "", expected = "Certificate Error: Navigation Blocked";
        int status = 0;
        System.out.println("Current directory is " + currentDir + "\n");
        if(environment_global.equalsIgnoreCase("browserstack"))
            filename = "BrowserStackConfig.csv";
        else if(environment_global.equalsIgnoreCase("browserstack_parallel") && !unlock.trim().equalsIgnoreCase("yes") && !unlock.trim().equalsIgnoreCase("no"))
            filename = "BrowserStackConfig_" + unlock.trim() + ".csv";
        if(environment_global.contains("browserstack")) {
            //Get config values for browser stack
            try {
                ConfigArray = placesterSupport.getBrowserStackConfig(filename);
            }
            catch(Exception e) {
                System.out.print("Exception caught: " + e.getMessage() + "; filename: " + filename + "; Length of ConfigArray is " + ConfigArray.length + "\n");
                ConfigArray = placesterSupport.getBrowserStackConfig(filename);
            }
            System.out.print("filename: " + filename + "\n");
            if(ConfigArray == null) {
                //System.out.print("filename: " + filename + "; ConfigArray is null!\n");
                ConfigArray = placesterSupport.getBrowserStackConfig(filename);
            }
            for(int x = 0; x < ConfigArray.length; x++) {
                if(x == 0)
                    brs = ConfigArray[x].trim();
                else if(x == 1) {
                    brs_version = ConfigArray[x].trim();
                }
                else if(x == 2) {
                    os = ConfigArray[x].trim();
                    if(os.contains("OS X"))
                        os = "linux";
                }
                else if(x == 3) {
                    os_version = ConfigArray[x].trim();
                }
                else if(x == 5) {
                    device_model = ConfigArray[x].trim();
                }
            }
        }
        if(!brs.equalsIgnoreCase("iphone") && !brs.equalsIgnoreCase("ipad") && !brs.equalsIgnoreCase("android"))
            System.out.println("\nStarting test on browser: " + brs + " " + brs_version + ", operating system: " + os + ", and environment: " + environment_global + " and local environment: " + env);
        else if(brs.equalsIgnoreCase("iphone") || brs.equalsIgnoreCase("ipad") || brs.equalsIgnoreCase("android"))
            System.out.println("\nStarting test on mobile browser: " + brs + ", and device: " + device_model + " on browserstack local");
        if("".equals(displayPopupDialogFlag.toString()) && environment_global.contains("browserstack")) {
            status = displayInformationPopupDialog(brs, brs_version, os_version, env);
            displayPopupDialogFlag.replace(0, displayPopupDialogFlag.length(), "0");
        }
        if(brs.equalsIgnoreCase("chrome") && os.equalsIgnoreCase("linux"))
            System.setProperty("webdriver.chrome.driver", currentDir + "/src/test/resources/chromedriver");
        else if (brs.equalsIgnoreCase("chrome") && os.equalsIgnoreCase("windows"))
            System.setProperty("webdriver.chrome.driver", currentDir + "\\src\\test\\resources\\chromedriver.exe");
        else if (brs.equalsIgnoreCase("ie") && os.equalsIgnoreCase("windows"))
            System.setProperty("webdriver.ie.driver", currentDir + "\\src\\test\\resources\\IEDriverServer.exe");
        if((brs.trim().equalsIgnoreCase("ie") && environment_global.contains("browserstack")) && (env.equalsIgnoreCase("test") || env.contains("dev") || env.equalsIgnoreCase("test1"))) {
            try {
                if(this.baseUrl.contains("https"))
                    this.baseUrl = this.baseUrl.substring(0, 4) + "://" + this.baseUrl.substring(8, this.baseUrl.length());
            }
            catch(Exception e) {}
        }
        else if((brs.trim().equalsIgnoreCase("ie") && environment_global.equalsIgnoreCase("local")) && (env.equalsIgnoreCase("test") || env.contains("dev") || env.equalsIgnoreCase("test1"))) {
            try {
                //Build URL for IE
                String[] url_parts1 = this.baseUrl.trim().split("/");
                String[] url_parts2 = this.baseUrl.trim().split("@");
                this.baseUrl = url_parts1[0] + "//" + url_parts2[1];
            }
            catch(Exception e) {}
        }
        if((environment_global.contains("browserstack")) && (brs.equalsIgnoreCase("safari") || brs.equalsIgnoreCase("ipad") || brs.equalsIgnoreCase("android") || brs.equalsIgnoreCase("ie") || brs.equalsIgnoreCase("firefox") || brs.equalsIgnoreCase("chrome"))) {
            while(counter <= 30000) {
                try {
                    counter++;
                    System.out.print("Trying to launch WebDriver for browser/device: " + brs + " on attempt " + counter + "\n");
                    webDriverSupport.getWebDriver().get(this.baseUrl.trim());
                    System.out.println("No Exception occured, while trying to launch web driver");
                    //((JavascriptExecutor) webDriverSupport.getWebDriver()).executeScript("PhishingAlertController.ignoreWarningSelected();");
                    webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector("#email"), timeout);
                    break;
                }
                catch(Exception e1) {
                    if(brs.equalsIgnoreCase("ie")) {
                        title = webDriverSupport.getWebDriver().getTitle();
                        if(!title.equalsIgnoreCase("Service Temporarily Unavailable") && !title.equalsIgnoreCase("502 Proxy Error"))
                            System.out.print("IE " + brs_version + " crash detected, the page title is " + title + "\n");
                    }
                    exception_msg = "";
                    exception_msg = exception_msg + e1.toString() + "\n" + e1.getStackTrace().toString();
                    System.out.print("Exception message captured, while trying to launch " + brs + " webdriver:" + exception_msg + "\n");
                    System.out.println("Printing URL variable after after catching exception with webdriver:  " + WebDriverSupport.URL.toString() + "\n");
                    if((exception_msg.contains("10 parallel sessions are currently being used") || exception_msg.contains("Automate daily limit reached for your plan") || exception_msg.contains("web view not found") || exception_msg.contains("no such window") || exception_msg.contains("Unable to communicate to node") || exception_msg.contains("Session not started or terminated") || exception_msg.contains("Session ID is null") || exception_msg.contains("Could not start a new session") || exception_msg.contains("Error communicating with the remote browser")) && (brs.equalsIgnoreCase("firefox") || brs.equalsIgnoreCase("chrome") || brs.equalsIgnoreCase("safari") || brs.equalsIgnoreCase("ipad") || brs.equalsIgnoreCase("android") || brs.equalsIgnoreCase("ie"))) {
                        System.out.print("Skipping the rest of the test cases for browser: " + brs + " webdriver, because browserstack system level exception below detected:\n" + exception_msg);
                        skipTestCase.replace(0, skipTestCase.length(), "0");
                        return;
                    }
                    else if(exception_msg.contains("Your Automate plan has expired"))
                        Assert.fail("Your Automate plan has expired!\n");
                    else if(exception_msg.contains("OS/Browser combination invalid"))
                        Assert.fail("OS and OS Version not supported!\n");
                    else if(exception_msg.contains("OS and OS Version not supported"))
                        Assert.fail("OS and OS Version not supported!\n");
                    else {
                        try {
                            webDriverSupport.cleanFullWhenDone();
                            WebDriverSupport.cleanFull();
                        }
                        catch(Exception e) {}
                    }
                }
            }
        }
        else {
            try {
                System.out.print("The test is running on base url: " + this.baseUrl.trim() + "\n");
                webDriverSupport.getWebDriver().get(this.baseUrl.trim());
                System.out.println("No Exception occured, while trying to launch web driver");
            }
            catch(Exception e) {
                try {
                    if(brs.equalsIgnoreCase("ie"))
                        System.out.print("IE " + brs_version + " crashed either locally or via browserstack\n");
                    System.out.println("Exception occured, cleaning full, please see exception message below\n" + e.toString() + "\n" + e.getStackTrace().toString());
                    webDriverSupport.cleanFullWhenDone();
                    WebDriverSupport.cleanFull();
                }
                catch(Exception e1) {
                    System.out.println(e1.toString() + "\n" + e1.getStackTrace().toString());
                }
                if((brs.equalsIgnoreCase("safari")) || (brs.equalsIgnoreCase("ie") && brs_version.equalsIgnoreCase("8")) || (brs.equalsIgnoreCase("ipad"))) {
                    try {
                        webDriverSupport.getWebDriver().get(this.baseUrl.trim());
                        System.out.println("No Exception occured, while trying to launch web driver\n");
                    }
                    catch(Exception e2) {   
                        if(brs.equalsIgnoreCase("ie"))
                            System.out.print("IE " + brs_version + " crashed either locally or via browserstack\n");
                        //System.out.print("Setting up ignore errors flag for " + brs + "\n");
                        try {
                            String parentWindowHandler = webDriverSupport.getWebDriver().getWindowHandle(); //Store parent window
                            webDriverSupport.getWebDriver().switchTo().window(parentWindowHandler);
                            webDriverSupport.getWebDriver().switchTo().activeElement();
                            f = placesterSupport.captureScreenshot();
                            flag = 1;
                        } catch (HeadlessException e3) {
                            e3.printStackTrace();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        } catch (AWTException e3) {
                            e3.printStackTrace();
                        } catch (Exception e3) {
                            System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
                            System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
                        }
                        if(flag == 0) {
                            try {
                                f = placesterSupport.captureScreenshot_selenium();
                            } catch (HeadlessException e3) {
                                e3.printStackTrace();
                            } catch (IOException e3) {
                                e3.printStackTrace();
                            } catch (AWTException e3) {
                                e3.printStackTrace();
                            } catch (Exception e3) {
                                System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
                                System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
                            }
                        }
                        System.out.print("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\n");
                        return;
                    }
                }
                else {
                    webDriverSupport.getWebDriver().get(this.baseUrl.trim());
                    System.out.println("No Exception occured, while trying to launch web driver");
                }
            }
        }
        if(environment_global.trim().equalsIgnoreCase("local"))
            System.out.println("Starting test on browser: " + brs);
        else if(environment_global.trim().contains("browserstack"))
            System.out.println("Starting browser stack test on browser/device: " + brs);
        if(brs.trim().equalsIgnoreCase("ie") && environment_global.trim().equalsIgnoreCase("local")) 
            placesterSupport.accessContinueToThisWebSiteLink(timeout);
        if(brs.equalsIgnoreCase("ie") && environment_global.trim().equalsIgnoreCase("local"))
            placesterSupport.accessContinueToThisWebSiteLink(timeout);
        else if(brs.equalsIgnoreCase("ie") && environment_global.trim().contains("browserstack")) {
            try {
                title = webDriverSupport.getWebDriver().getTitle();
            }
            catch(Exception e) {};
            if(title.equalsIgnoreCase(expected)) {
                webDriverSupport.getElementWithoutSystemTimeout(By.linkText("Continue to this website (not recommended)."), timeout).click();
                placesterSupport.pauseForATime(60000);
            }
        }
        counter = 0;
        while(counter < 10 && !brs.equalsIgnoreCase("android") && !brs.equalsIgnoreCase("iphone")) {
            if(brs.equalsIgnoreCase("ie") && environment_global.equalsIgnoreCase("local"))
                break;
            counter++;
            if(counter > 1) {
                System.out.print("Trying to relaunch browser: " + brs + " for attempt " + (counter - 1) + "\n");
                try {
                    webDriverSupport.getWebDriver().get(this.baseUrl.trim());
                }
                catch(Exception e) {
                    error_msg = "";
                    error_msg = e.toString() + "\n" + e.getStackTrace().toString();
                    try {
                        String parentWindowHandler = webDriverSupport.getWebDriver().getWindowHandle(); //Store parent window
                        webDriverSupport.getWebDriver().switchTo().window(parentWindowHandler);
                        webDriverSupport.getWebDriver().switchTo().activeElement();
                        f = placesterSupport.captureScreenshot();
                        flag = 1;
                    } catch (HeadlessException e3) {
                        e3.printStackTrace();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    } catch (AWTException e3) {
                        e3.printStackTrace();
                    }
                    catch (Exception e3) {
                        System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
                        System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
                    }
                    if(flag == 0) {
                        try {
                            f = placesterSupport.captureScreenshot_selenium();
                        } catch (HeadlessException e3) {
                            e3.printStackTrace();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        } catch (AWTException e3) {
                            e3.printStackTrace();
                        } catch (Exception e3) {
                            System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
                            System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
                        }
                    }
                    Assert.fail("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\nPlease see error below\n" + error_msg + "\n");
                }
            }
            try {
                webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector("#email"), timeout);
                break;
            }
            catch(Exception e) {
                try {
                    System.out.print("Error launching browser: " + brs + " was detected (Placester Login page was not loaded), cleaning before attempting to relaunch the browser\n");
                    title = webDriverSupport.getWebDriver().getTitle();
                    String currentURL = webDriverSupport.getWebDriver().getCurrentUrl();
                    System.out.print("Current URL is: " + currentURL + "\n");
                    System.out.print("Page title is: " + title + "\n");
                    webDriverSupport.cleanFullWhenDone();
                    WebDriverSupport.cleanFull();
                }
                catch(Exception e1) {}
            }
        }
        //Maximize the browser
        try {
            webDriverSupport.getWebDriver().manage().window().maximize();
        } 
        catch(Exception e) {}
        if(environment_global.trim().contains("browserstack") && !brs.equalsIgnoreCase("android") && !brs.equalsIgnoreCase("ipad"))
            System.out.print("Started single browserstack session for browser: " + brs + " " + brs_version + ", operating system: " + os + " " + os_version + " at " + placesterSupport.StringDateToDate(placesterSupport.GetUTCdatetimeAsString()) + "\n");
        else if(environment_global.trim().equalsIgnoreCase("browserstack_parallel") && !unlock.equalsIgnoreCase("yes") && !unlock.equalsIgnoreCase("no") && !brs.equalsIgnoreCase("android") && !brs.equalsIgnoreCase("ipad"))
            System.out.print("Started parallel browserstack session " + unlock + " for browser: " + brs + " " + brs_version + ", operating system: " + os + " " + os_version + " at " + placesterSupport.StringDateToDate(placesterSupport.GetUTCdatetimeAsString()) + "\n");
        else if((environment_global.trim().equalsIgnoreCase("browserstack_parallel")) && (!unlock.equalsIgnoreCase("yes") && !unlock.equalsIgnoreCase("no")) && (brs.equalsIgnoreCase("android") || brs.equalsIgnoreCase("ipad")))
            System.out.print("Started parallel browserstack session " + unlock + " for browser: " + brs + " and device model: " + device_model + " at " + placesterSupport.StringDateToDate(placesterSupport.GetUTCdatetimeAsString()) + "\n");
        else if((environment_global.trim().contains("browserstack")) && (brs.equalsIgnoreCase("android") || brs.equalsIgnoreCase("ipad")))
            System.out.print("Started single browserstack session for browser: " + brs + " and device model: " + device_model + " at " + placesterSupport.StringDateToDate(placesterSupport.GetUTCdatetimeAsString()) + "\n");
        if(status == 1) {
            System.out.print("Waiting for 30 seconds for browser stack interactive session\n");
            placesterSupport.pauseForATime(30000);
        }
    }
    @Given("^I get browserstack results url$")
    public void iGetBrowserStackResultsURL() {
    	int timeout = 10;
    	String results_url = "", filename = "";
    	String ConfigArray[] = null;;
    	String environment_global = environment;
    	String brs = System.getProperty("webdriver.driver");
    	if(environment_global.equalsIgnoreCase("browserstack"))
        	filename = "BrowserStackConfig.csv";
        else if(environment_global.equalsIgnoreCase("browserstack_parallel") && !unlock.trim().equalsIgnoreCase("yes") && !unlock.trim().equalsIgnoreCase("no"))
        	filename = "BrowserStackConfig_" + unlock.trim() + ".csv";
        if(environment_global.contains("browserstack")) {
    		//Get config values for browser stack
          	ConfigArray = placesterSupport.getBrowserStackConfig(filename);
          	for(int x = 0; x < ConfigArray.length; x++) {
          		if(x == 0) {
          			brs = ConfigArray[x].trim();
          			break;
          		}
          	}
    	}
    	if(!environment_global.equalsIgnoreCase("browserstack") || brs.equalsIgnoreCase("android"))
    		return;
    	placesterSupport.getURL("https://www.browserstack.com/Automate", timeout);
    	try {
			if(webDriverSupport.getElementWithoutSystemTimeout(By.linkText("Sign in"), 5).isDisplayed()) {
				System.out.print("Logging to browserstack\n");
				placesterSupport.clickElement("Sign in", "by linkText", timeout);
				//Specify userid/pwd and clock Login
				placesterSupport.clickElement("user_email_login", "by id", timeout);
				placesterSupport.sendKeysElement("user_email_login", "yevgeniy.grimaylo@sceneric.com", "by id", timeout);
				placesterSupport.clickElement("user_password", "by id", timeout);
				placesterSupport.sendKeysElement("user_password", "Password_1", "by id", timeout);
				placesterSupport.clickElement("user_submit", "by id", timeout);
				//Select the latest session and capture url
				placesterSupport.clickElement("//li[1]/a/div[2]/span[3]", "by xpath", timeout);
				placesterSupport.clickElement("//tr[2]/td/a/span", "by xpath", timeout);
				placesterSupport.pauseForATime(5000);
				results_url = webDriverSupport.getWebDriver().getCurrentUrl();
				System.out.print("The results url for this test case is: " + results_url + " (login to browserstack.com/Automate as yevgeniy.grimaylo@sceneric.com/Password_1)\n");
				GetBrowserStackResultsURL.replace(0, GetBrowserStackResultsURL.length(), "0");
			}
		}
		catch(Exception e) {};
    }
    @Given("^I capture screenshot for page: (.*) after launching url: (.*)$")
    public void iCaptureScreenshotForPageAfterLaunchingURL(String page, String url) {
    	String brs = System.getProperty("webdriver.driver");
    	String os = placesterSupport.getOS();
    	String ConfigArray[] = null;
    	String currentDir = System.getProperty("user.dir");
    	String environment_global = environment;
    	String device_model = "";
    	String brs_version = "", exception_msg = "", filename = "";
    	int counter = 0;
        System.out.println("Current directory is " + currentDir + "\n");
        if(environment_global.equalsIgnoreCase("browserstack"))
        	filename = "BrowserStackConfig.csv";
        else if(environment_global.equalsIgnoreCase("browserstack_parallel") && !unlock.trim().equalsIgnoreCase("yes") && !unlock.trim().equalsIgnoreCase("no"))
        	filename = "BrowserStackConfig_" + unlock.trim() + ".csv";
        if(environment_global.contains("browserstack")) {
        	//Get config values for browser stack
        	ConfigArray = placesterSupport.getBrowserStackConfig(filename);
        	for(int x = 0; x < ConfigArray.length; x++) {
        		if(x == 0)
        			brs = ConfigArray[x].trim();
        		else if(x == 1) {
        			brs_version = ConfigArray[x].trim();
        		}
        		else if(x == 2) {
        			os = ConfigArray[x].trim();
        			if(os.contains("OS X"))
        				os = "linux";
        		}
        		else if(x == 5) {
        			device_model = ConfigArray[x].trim();
        		}
        	}
        }
        if(!brs.equalsIgnoreCase("iphone") && !brs.equalsIgnoreCase("ipad") && !brs.equalsIgnoreCase("android"))
        	System.out.println("\nStarting test on browser: " + brs + " " + brs_version + ", operating system: " + os + "\n");
        else if(brs.equalsIgnoreCase("iphone") || brs.equalsIgnoreCase("ipad") || brs.equalsIgnoreCase("android"))
        	System.out.println("\nStarting test on mobile browser: " + brs + ", and device: " + device_model + " on browserstack\n");
    	if(brs.equalsIgnoreCase("chrome") && os.equalsIgnoreCase("linux"))
    		System.setProperty("webdriver.chrome.driver", currentDir + "/src/test/resources/chromedriver");
    	else if (brs.equalsIgnoreCase("chrome") && os.equalsIgnoreCase("windows"))
			System.setProperty("webdriver.chrome.driver", currentDir + "\\src\\test\\resources\\chromedriver.exe");
    	if((environment_global.contains("browserstack")) && (brs.equalsIgnoreCase("safari") || brs.equalsIgnoreCase("ipad") || brs.equalsIgnoreCase("android") || brs.equalsIgnoreCase("iphone"))) {
    		while(counter <= 100) {
				try {
					counter++;
					System.out.print("Trying to launch WebDriver for browser/device: " + brs + " on attempt " + counter + "\n");
					webDriverSupport.getWebDriver().get(url.trim());
					break;
				}
				catch(Exception e1) {
					exception_msg = "";
					exception_msg = exception_msg + e1.toString() + "\n" + e1.getStackTrace().toString();
					System.out.print("Exception message captured, while trying to launch " + brs + " webdriver:" + exception_msg + "\n");
					try {
						webDriverSupport.cleanFullWhenDone();
						WebDriverSupport.cleanFull();
					}
					catch(Exception e) {}
					if((exception_msg.contains("Session not started or terminated") || exception_msg.contains("Session ID is null")) && (brs.equalsIgnoreCase("ie") || brs.equalsIgnoreCase("ipad"))) {
						System.out.print("Trying to restart " + brs + " webdriver\n");
						webDriverSupport.getWebDriver();
						webDriverSupport.getWebDriver().get(url.trim());
					}
				}
    		}
    	}
    	if(environment_global.trim().equalsIgnoreCase("local")) {
    		placesterSupport.pauseForATime(3000);
    		try {
    			String parentWindowHandler = webDriverSupport.getWebDriver().getWindowHandle(); //Store parent window
    			webDriverSupport.getWebDriver().switchTo().window(parentWindowHandler);
    			webDriverSupport.getWebDriver().switchTo().activeElement();
    			f = placesterSupport.captureScreenshot();
    		} catch (HeadlessException e3) {
    			e3.printStackTrace();
    		} catch (IOException e3) {
    			e3.printStackTrace();
    		} catch (AWTException e3) {
    			e3.printStackTrace();
    		} catch (Exception e3) {
    			System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
    			System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
    		}
    		System.out.print("\nPlease see screenshot  for page: " + page + " and url: " + url + " in " + f.getAbsolutePath() + "\n");
    	}
    				
    }
    @Given("^I maximize the browser$")
    public void iMaximizeTheBrowser() {
    	//Maximize the browser
    	try {
    		webDriverSupport.getWebDriver().manage().window().maximize();
    	} 
    	catch(Exception e) {}
    }
    @Given("^I end the test case for browser stack interactive session$")
    public void iEndTheTestCaseForBrowserStackInteractiveSession() {
    	
    	if (!"".equals(PlacesterSupport.verificationErrors.toString())) {
    		issues = PlacesterSupport.verificationErrors.toString();
    		PlacesterSupport.verificationErrors.replace(0, PlacesterSupport.verificationErrors.length(), "");
    	    Assert.fail(issues);
    	}
    }
    public int displayInformationPopupDialog(String brs, String brs_version, String os_version, String env) {	
		int return_status = 0;
		if((brs.equalsIgnoreCase("safari")) && (os_version.equalsIgnoreCase("mountain lion") || os_version.equalsIgnoreCase("lion") || os_version.equalsIgnoreCase("yosemite")) && (!env.equalsIgnoreCase("test"))) {
    		System.out.print("Displaying information popup dialog for browserstack browser: " + brs + ", os_version: " + os_version + " and environment: " + env + "\n");
    		Thread t = new Thread(new Runnable(){
    			public void run(){
    				JOptionPane.showMessageDialog(null,
    						"You may need to perform manual steps below:\n1. Access browserstack by launching https://browserstack/Automate\n2. Specify your browserstack credentials (ask Christian Hurley)\n3. Click Interactive Session link\n4. Click Continue button of Safari browser\n5. Enter username/password for basic http authentication, and then click OK button\n6. You have 60 seconds to perform above manual steps.",
    						"Interactive browser stack information for Safari",
    						JOptionPane.INFORMATION_MESSAGE);
    			}
    		});
    		t.start();
    		return_status = 1;
    	}
    	else if(brs.equalsIgnoreCase("safari") && os_version.equalsIgnoreCase("snow leopard") && env.equalsIgnoreCase("dev")) {
    		System.out.print("Displaying information popup dialog for browserstack browser: " + brs + ", os_version: " + os_version + " and environment: " + env + "\n");
    		Thread t = new Thread(new Runnable(){
    			public void run(){
    				JOptionPane.showMessageDialog(null,
    						"BroswerStack execution for dev environment on OS X Snow Leopard is not supported!",
    						"Interactive browser stack information for Safari",
    						JOptionPane.INFORMATION_MESSAGE);
    			}
    		});
    		t.start();
    	}
    	//else if(brs.equalsIgnoreCase("ie")) {
    		//System.out.print("Displaying information popup dialog for browserstack browser: " + brs + ", os_version: " + os_version + " and environment: " + env + "\n");
    		//Thread t = new Thread(new Runnable(){
    			//public void run(){
    				//JOptionPane.showMessageDialog(null,
    						//"You may need to perform manual steps below:\n1. Access browserstack by launching https://browserstack/Automate\n2. Specify your browserstack credentials (ask Christian Hurley)\n3. For IE 8, you may need to click Continue to this website (not recommended)., refresh the page, and then enter username/password for basic http authentication, and then click OK button\n6. For IE 9,10 and 11, enter username/password for basic http authentication, and then click OK button\n7. You have 60 seconds to perform above manual steps.",
    						//"Interactive browser stack information for IE",
    						//JOptionPane.INFORMATION_MESSAGE);
    			//}
    		//});
    		//t.start();
    	//}
    	//else if(brs.equalsIgnoreCase("iphone") || brs.equalsIgnoreCase("android")) {
    		//System.out.print("Displaying information popup dialog for browserstack browser: " + brs + ", os_version: " + os_version + " and environment: " + env + "\n");
    		//Thread t = new Thread(new Runnable(){
    			//public void run(){
    				//JOptionPane.showMessageDialog(null,
    						//"You may need to perform manual steps below:\n1. Access browserstack by launching https://browserstack/Automate\n2. Specify your browserstack credentials (ask Christian Hurley)\n3. For iPhone, you may need to click Ignore this Warning button, and then enter username/password for basic http authentication, and then click OK button\n6. You have 60 seconds to perform above manual steps.",
    						//"Interactive browser stack information for mobile devices",
    						//JOptionPane.INFORMATION_MESSAGE);
    			//}
    		//});
    		//t.start();
    		//return_status = 1;
    	//}
    	return return_status;
    }
    @After
    public void cleanup() {
    	String brs = System.getProperty("webdriver.driver"), filename = "";
    	String os = placesterSupport.getOS();
    	String ConfigArray[] = null;
    	String environment_global = environment;
    	if(environment_global.equalsIgnoreCase("browserstack"))
        	filename = "BrowserStackConfig.csv";
        else if(environment_global.equalsIgnoreCase("browserstack_parallel") && !unlock.trim().equalsIgnoreCase("yes") && !unlock.trim().equalsIgnoreCase("no"))
        	filename = "BrowserStackConfig_" + unlock.trim() + ".csv";
        if(environment_global.contains("browserstack")) {
        	//Get config values for browser stack
        	try {
        		ConfigArray = placesterSupport.getBrowserStackConfig(filename);
        	}
        	catch(Exception e) {
        		System.out.print("Exception caught: " + e.getMessage() + "; filename: " + filename + "; Length of ConfigArray is " + ConfigArray.length + "\n");
        		ConfigArray = placesterSupport.getBrowserStackConfig(filename);
        	}
        	//System.out.print("filename: " + filename + "\n");
        	if(ConfigArray == null) {
        		//System.out.print("filename: " + filename + "; ConfigArray is null!\n");
        		ConfigArray = placesterSupport.getBrowserStackConfig(filename);
        	}
        	for(int x = 0; x < ConfigArray.length; x++) {
        		if(x == 0)
        			brs = ConfigArray[x].trim();
        		else if(x == 2) {
        			os = ConfigArray[x].trim();
        			if(os.contains("OS X"))
        				os = "linux";
        			break;
        		}
        	}
        }
    	System.out.println("\nEnding test on browser: " + brs + ", operating system: " + os + ", and environment: " + environment);
    	if(!brs.equalsIgnoreCase("ipad") && ("".equals(skipTestCase.toString()))) {
    		try {
    			webDriverSupport.cleanCookies();
    			placesterSupport.clearCookies();
    			webDriverSupport.cleanFullWhenDone();
    			WebDriverSupport.cleanFull();
    		}
    		catch(Exception e) {}
    	}
    	else if(brs.equalsIgnoreCase("ipad") && ("".equals(skipTestCase.toString()))) {
    		try {
    			webDriverSupport.cleanFullWhenDone();
    		}
    		catch(Exception e) {}
    	}
    	skipTestCase.replace(0, skipTestCase.length(), "");
    	if ("".equals(GetBrowserStackResultsURL.toString()))
    		iGetBrowserStackResultsURL();
    	if (!"".equals(PlacesterSupport.verificationErrors.toString())) {
    		issues = PlacesterSupport.verificationErrors.toString();
    		PlacesterSupport.verificationErrors.replace(0, PlacesterSupport.verificationErrors.length(), "");
    		System.out.print(issues);
    	    Assert.fail(issues);
    	}
    }
    @Before("@unlock")
    public void UnLock() throws IOException {
    	String os = placesterSupport.getOS();
    	String environment_global = environment;
    	placesterSupport.pauseForATime(2000);
    	if(os.equalsIgnoreCase("linux"))
    		System.out.print("Number of java processes running: " + placesterSupport.checkHowManyProcessInstancesRunning("java") + "\n");
    	else
    		System.out.print("Number of java.exe processes running: " + placesterSupport.checkHowManyProcessInstancesRunning("java.exe") + "\n");
    	if("".equals(unlockFlag.toString()) && environment_global.equalsIgnoreCase("local") && os.equalsIgnoreCase("windows") && unlock.trim().equalsIgnoreCase("yes") && placesterSupport.checkHowManyProcessInstancesRunning("java.exe") < 11) {
    		unlockFlag.replace(0, unlockFlag.length(), "0");
    		System.out.print("Starting UnLock thread\n");
    		new Thread(new UnLock()).start();
    	}
    	else if("".equals(unlockFlag.toString()) && environment_global.equalsIgnoreCase("local") && os.equalsIgnoreCase("linux") && unlock.trim().equalsIgnoreCase("yes") && placesterSupport.checkHowManyProcessInstancesRunning("java") <= 12) {
    		unlockFlag.replace(0, unlockFlag.length(), "0");
    		System.out.print("Starting UnLock thread\n");
			new Thread(new UnLock()).start();
			if(!environment_global.equalsIgnoreCase("browserstack")) {
				System.out.print("Starting Monitor thread\n");
				new Thread(new Monitor()).start();
			}
    	}
    }
}
