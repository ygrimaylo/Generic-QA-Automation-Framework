/*
 * Copyright 2015, Yevgeniy Grimaylo
 *
 * scenericSupport.java
 */
package com.placester.web.steps;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;
import java.util.regex.Pattern;
import javax.annotation.Resource;
import javax.imageio.ImageIO;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.Select;
import org.springframework.stereotype.Service;
import com.placester.web.steps.WebDriverSupport;

@Service
public class PlacesterSupport {

	@Resource
    private WebDriverSupport webDriverSupport;
    @Resource
    String environment;
    @Resource
    private String baseUrl;
    @Resource
    String unlock;
    private static final String TASKLIST = "tasklist";
    public static StringBuffer verificationErrors = new StringBuffer("");
    public static String DATEFORMAT = "HH:mm:ss yyyy-MM-dd";
    		
    public void pauseForATime() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pauseForATime(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public File createTmpFile(String content, String fileType) throws IOException {
        return createTmpFile("webdriver", content, fileType);
    }

    public File captureScreenshot() throws IOException, HeadlessException, AWTException, Exception {
    	File f = File.createTempFile("screenshot", ".png");
    	BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
    	ImageIO.write(image, "png", new File(f.getAbsolutePath()));
        return f;
    }
    
    public File captureScreenshot_selenium() throws IOException, HeadlessException, AWTException, Exception {
    	WebDriver augmentedDriver = new Augmenter().augment(webDriverSupport.getWebDriver());
    	File f = ((TakesScreenshot)augmentedDriver).getScreenshotAs(OutputType.FILE);
        return f;
    }
    
    public File createTmpFile(String name, String content, String fileType) throws IOException {
        File f = File.createTempFile(name, fileType);

        OutputStream out = new FileOutputStream(f);
        PrintWriter pw = new PrintWriter(out);
        pw.write(content);
        pw.flush();
        pw.close();
        out.close();

        return f;
    }

    public File createTmpFile(int sizeInMB, String fileType) throws IOException {
        StringBuffer content = new StringBuffer();

        int length = content.length();
        while (length < 1024 * 1024 * sizeInMB) {
            content.append("superman ");
            length = content.length();
        }

        return createTmpFile(content.toString(), fileType);
    }

    public void switchFrame(String frame) {
        pauseForATime();
        webDriverSupport.getWebDriver().switchTo().frame(frame);
    }

    public void switchFrame(int frame) {
        pauseForATime();
        webDriverSupport.getWebDriver().switchTo().frame(frame);
    }

    public void switchToDefaultFrame() {
        webDriverSupport.getWebDriver().switchTo().defaultContent();
    }

    public String getCharactersByNumber(String statement) {
        int num = new Integer(statement.substring(13, 16).trim());
        return StringUtils.leftPad("chars" + new Random().nextInt(1000), num, "F");
    }

    public void iHoverOverElement(WebElement item) {
        Actions builder = new Actions(webDriverSupport.getWebDriver());
        //builder.clickAndHold(item);
        builder.moveToElement(item);
    }
    
    public boolean clickElement(String uiLocator, String locatorType, int timeout) {
    	String error_msg = "", filename = "";
    	File f = null;
    	int flag = 0, flag1 = 0;
        long totalTime = 0, startTime = 0, endTime = 0;
        Boolean status = false;
        WebElement element = null;
        String ConfigArray[] = null;
    	String environment_global = environment;
    	String brs = System.getProperty("webdriver.driver");
    	String env = getEnvironment();
    	if(environment_global.equalsIgnoreCase("browserstack"))
        	filename = "BrowserStackConfig.csv";
        else if(environment_global.equalsIgnoreCase("browserstack_parallel") && !unlock.trim().equalsIgnoreCase("yes") && !unlock.trim().equalsIgnoreCase("no"))
        	filename = "BrowserStackConfig_" + unlock.trim() + ".csv";
        if(environment_global.contains("browserstack")) {
    		//Get config values for browser stack
          	ConfigArray = getBrowserStackConfig(filename);
          	for(int x = 0; x < ConfigArray.length; x++) {
          		if(x == 0) {
          			brs = ConfigArray[x].trim();
          			break;
          		}
          	}
    	}
        System.out.println("Clicking on element " + locatorType + ": " + uiLocator);
    	while(totalTime <= timeout*1000) {
            startTime = System.currentTimeMillis();
            try {
            	if(locatorType.equalsIgnoreCase("by name")) {
            		element = webDriverSupport.getElementWithoutSystemTimeout(By.name(uiLocator), 0);
            		//Moving toolbar to the element for better visibility
            		if(brs.equalsIgnoreCase("chrome") || brs.equalsIgnoreCase("android") || brs.equalsIgnoreCase("ipad"))
            			((JavascriptExecutor) webDriverSupport.getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
            		element.click();
            	}
            	else if(locatorType.equalsIgnoreCase("by id")) {
            		element = webDriverSupport.getElementWithoutSystemTimeout(By.id(uiLocator), 0);
            		if(brs.equalsIgnoreCase("chrome") || brs.equalsIgnoreCase("android") || brs.equalsIgnoreCase("ipad"))
            			((JavascriptExecutor) webDriverSupport.getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
            		element.click();
            	}
            	else if(locatorType.equalsIgnoreCase("by css")) {
            		element = webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector(uiLocator), 0);
            		if(brs.equalsIgnoreCase("chrome") || brs.equalsIgnoreCase("android") || brs.equalsIgnoreCase("ipad"))
            			((JavascriptExecutor) webDriverSupport.getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
            		element.click();
            	}
            	else if(locatorType.equalsIgnoreCase("by xpath")) {
            		element = webDriverSupport.getElementWithoutSystemTimeout(By.xpath(uiLocator), 0);
            		if(brs.equalsIgnoreCase("chrome") || brs.equalsIgnoreCase("android") || brs.equalsIgnoreCase("ipad"))
            			((JavascriptExecutor) webDriverSupport.getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
            		element.click();
            	}
            	else if(locatorType.equalsIgnoreCase("by linkText")) {
            		element = webDriverSupport.getElementWithoutSystemTimeout(By.linkText(uiLocator), 0);
            		if(brs.equalsIgnoreCase("chrome") || brs.equalsIgnoreCase("android") || brs.equalsIgnoreCase("ipad"))
            			((JavascriptExecutor) webDriverSupport.getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
            		element.click();
            	}
                flag = 1;
                error_msg = "";
                status = true;
                break;
            }
            catch(Exception e) {
            	error_msg = "Element " + locatorType + ": " + uiLocator + " not found! " + e.toString() + "\n" + e.getStackTrace().toString();
            }
            catch(AssertionError e) {error_msg = e.toString() + "\n" + e.getStackTrace().toString();}
            endTime = System.currentTimeMillis();
            totalTime = totalTime + (endTime - startTime);
            //System.out.println("Total time: " + totalTime);
        }
        if(flag == 0) {
        	try {
        		String title = webDriverSupport.getWebDriver().getTitle();
        		System.out.print("Page title is: " + title + "\n");
				String currentURL = webDriverSupport.getWebDriver().getCurrentUrl();
				System.out.print("Current URL is: " + currentURL + "\n");
				//String bodyText = webDriverSupport.getElementWithoutSystemTimeout(By.tagName("BODY"), 3).getText();
				//System.out.print("Page body text is: " + bodyText + "\n");
				////String pageSource = webDriverSupport.getWebDriver().getPageSource();
				////System.out.print("Page source is: " + pageSource + "\n");
				String header = webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector("h1"), 3).getText();
				System.out.print("Page header is: " + header + "\n");
        	}
        	catch(Exception e1) {}
            try {
            	String parentWindowHandler = webDriverSupport.getWebDriver().getWindowHandle(); //Store parent window
    			webDriverSupport.getWebDriver().switchTo().window(parentWindowHandler);
    			webDriverSupport.getWebDriver().switchTo().activeElement();
				f = captureScreenshot();
				flag1 = 1;
			} catch (HeadlessException e2) {
				System.out.print("Screenshot capture failed!\n");
			} catch (IOException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			} catch (AWTException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			}
            catch (Exception e3) {
				System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
			}
            if(flag1 == 0) {
            	try {
        			f = captureScreenshot_selenium();
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
            if(locatorType.contains("linkText") && !env.equalsIgnoreCase("dev2")) {
            	if(Pattern.compile("[0-9].*").matcher(uiLocator).find() || Pattern.compile("[A-Z].*[0-9].*").matcher(uiLocator).find() || Pattern.compile("[a-z].*[0-9].*").matcher(uiLocator).find()) {
            		Assert.fail("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\nTimeout of "+ timeout + " seconds occured, please see error below\n" + error_msg + "\n");
            	}
            	else
                	verificationErrors.append("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\nTimeout of "+ timeout + " seconds occured, please see error below\n" + error_msg + "\n");
            }
            else
            	verificationErrors.append("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\nTimeout of "+ timeout + " seconds occured, please see error below\n" + error_msg + "\n");
        }
		return status;
    }
    public boolean isElementDisplayed(String uiLocator, String locatorType, int timeout) {
        String filename = "";
        long totalTime = 0, startTime = 0, endTime = 0;
        Boolean status = false;
        WebElement element = null;
        String ConfigArray[] = null;
        String environment_global = environment;
        String brs = System.getProperty("webdriver.driver");
        if(environment_global.equalsIgnoreCase("browserstack"))
            filename = "BrowserStackConfig.csv";
        else if(environment_global.equalsIgnoreCase("browserstack_parallel") && !unlock.trim().equalsIgnoreCase("yes") && !unlock.trim().equalsIgnoreCase("no"))
            filename = "BrowserStackConfig_" + unlock.trim() + ".csv";
        if(environment_global.contains("browserstack")) {
            //Get config values for browser stack
            ConfigArray = getBrowserStackConfig(filename);
            for(int x = 0; x < ConfigArray.length; x++) {
                if(x == 0) {
                    brs = ConfigArray[x].trim();
                    break;
                }
            }
        }
        System.out.println("Checking if element " + locatorType + ": " + uiLocator + " is displayed");
        while(totalTime <= timeout*1000) {
            startTime = System.currentTimeMillis();
            try {
                if(locatorType.equalsIgnoreCase("by name")) {
                    element = webDriverSupport.getElementWithoutSystemTimeout(By.name(uiLocator), 0);
                    //Moving toolbar to the element for better visibility
                    if(brs.equalsIgnoreCase("chrome") || brs.equalsIgnoreCase("android") || brs.equalsIgnoreCase("ipad"))
                        ((JavascriptExecutor) webDriverSupport.getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
                    element.isDisplayed();
                }
                else if(locatorType.equalsIgnoreCase("by id")) {
                    element = webDriverSupport.getElementWithoutSystemTimeout(By.id(uiLocator), 0);
                    if(brs.equalsIgnoreCase("chrome") || brs.equalsIgnoreCase("android") || brs.equalsIgnoreCase("ipad"))
                        ((JavascriptExecutor) webDriverSupport.getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
                    element.isDisplayed();
                }
                else if(locatorType.equalsIgnoreCase("by css")) {
                    element = webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector(uiLocator), 0);
                    if(brs.equalsIgnoreCase("chrome") || brs.equalsIgnoreCase("android") || brs.equalsIgnoreCase("ipad"))
                        ((JavascriptExecutor) webDriverSupport.getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
                    element.isDisplayed();
                }
                else if(locatorType.equalsIgnoreCase("by xpath")) {
                    element = webDriverSupport.getElementWithoutSystemTimeout(By.xpath(uiLocator), 0);
                    if(brs.equalsIgnoreCase("chrome") || brs.equalsIgnoreCase("android") || brs.equalsIgnoreCase("ipad"))
                        ((JavascriptExecutor) webDriverSupport.getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
                    element.isDisplayed();
                }
                else if(locatorType.equalsIgnoreCase("by linkText")) {
                    element = webDriverSupport.getElementWithoutSystemTimeout(By.linkText(uiLocator), 0);
                    if(brs.equalsIgnoreCase("chrome") || brs.equalsIgnoreCase("android") || brs.equalsIgnoreCase("ipad"))
                        ((JavascriptExecutor) webDriverSupport.getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
                    element.isDisplayed();
                }
                status = true;
                break;
            }
            catch(Exception e) {}
            endTime = System.currentTimeMillis();
            totalTime = totalTime + (endTime - startTime);
            //System.out.println("Total time: " + totalTime);
        }
        return status;
    }
    public void clickByWebElement(WebElement element, int timeout) {
    	String error_msg = "";
    	File f = null;
    	int flag = 0, flag1 = 0;
        long totalTime = 0, startTime = 0, endTime = 0;
        System.out.println("Clicking on element " + element.toString());
    	while(totalTime <= timeout*1000) {
            startTime = System.currentTimeMillis();
            try {
            	//Move vertical toolbar to the element
            	((JavascriptExecutor) webDriverSupport.getWebDriver()).executeScript("arguments[0].scrollIntoView(true);", element);
            	element.click();
            	flag = 1;
                error_msg = "";
                break;
            }
            catch(Exception e) {
            	error_msg = "Element " + element.toString() + " not found! " + e.toString() + "\n" + e.getStackTrace().toString();
            }
            catch(AssertionError e) {error_msg = e.toString() + "\n" + e.getStackTrace().toString();}
            endTime = System.currentTimeMillis();
            totalTime = totalTime + (endTime - startTime);
            //System.out.println("Total time: " + totalTime);
        }
        if(flag == 0) {
        	try {
        		String title = webDriverSupport.getWebDriver().getTitle();
        		System.out.print("Page title is: " + title + "\n");
				String currentURL = webDriverSupport.getWebDriver().getCurrentUrl();
				System.out.print("Current URL is: " + currentURL + "\n");
				//String bodyText = webDriverSupport.getElementWithoutSystemTimeout(By.tagName("BODY"), 3).getText();
				//System.out.print("Page body text is: " + bodyText + "\n");
				//String pageSource = webDriverSupport.getWebDriver().getPageSource();
				//System.out.print("Page source is: " + pageSource + "\n");
				String header = webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector("h1"), 3).getText();
				System.out.print("Page header is: " + header + "\n");
        	}
        	catch(Exception e1) {}
        	try {
        		String parentWindowHandler = webDriverSupport.getWebDriver().getWindowHandle(); //Store parent window
        		webDriverSupport.getWebDriver().switchTo().window(parentWindowHandler);
        		webDriverSupport.getWebDriver().switchTo().activeElement();
				f = captureScreenshot();
				flag1 = 1;
			} catch (HeadlessException e2) {
				System.out.print("Screenshot capture failed!\n");
			} catch (IOException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			} catch (AWTException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			}
            catch (Exception e3) {
				System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
			}
            if(flag1 == 0) {
            	try {
        			f = captureScreenshot_selenium();
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
            verificationErrors.append("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\nTimeout of "+ timeout + " seconds occured, please see error below\n" + error_msg + "\n");
        }
    }
    public void verifyPageBodyText(String page, String expected, int timeout) {
    	String error_msg = "", actual = "";
    	File f = null;
    	int flag = 0, flag1 = 0;
        long totalTime = 0, startTime = 0, endTime = 0;
        System.out.println("Comparing entire " + page + " page body text with expected value of: " + expected);
    	while(totalTime <= timeout*1000) {
            startTime = System.currentTimeMillis();
            try {
            	actual = webDriverSupport.getElementWithoutSystemTimeout(By.tagName("BODY"), 0).getText();
            	if(actual.contains(expected)) {
            		flag = 1;
            		error_msg = "";
            		break;
            	}
            }
            catch(Exception e) {
            	error_msg = "Error capturing entire page body text! " + e.toString() + "\n" + e.getStackTrace().toString();
            }
            endTime = System.currentTimeMillis();
            totalTime = totalTime + (endTime - startTime);
            //System.out.println("Total time: " + totalTime);
        }
    	if(flag == 0) {
    		try {
        		String title = webDriverSupport.getWebDriver().getTitle();
        		System.out.print("Page title is: " + title + "\n");
				String currentURL = webDriverSupport.getWebDriver().getCurrentUrl();
				System.out.print("Current URL is: " + currentURL + "\n");
				//String bodyText = webDriverSupport.getElementWithoutSystemTimeout(By.tagName("BODY"), 3).getText();
				//System.out.print("Page body text is: " + bodyText + "\n");
				//String pageSource = webDriverSupport.getWebDriver().getPageSource();
				//System.out.print("Page source is: " + pageSource + "\n");
				String header = webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector("h1"), 3).getText();
				System.out.print("Page header is: " + header + "\n");
        	}
        	catch(Exception e1) {}
        	try {
        		String parentWindowHandler = webDriverSupport.getWebDriver().getWindowHandle(); //Store parent window
    			webDriverSupport.getWebDriver().switchTo().window(parentWindowHandler);
    			webDriverSupport.getWebDriver().switchTo().activeElement();
				f = captureScreenshot();
				flag1 = 1;
			} catch (HeadlessException e2) {
				System.out.print("Screenshot capture failed!\n");
			} catch (IOException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			} catch (AWTException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			}
            catch (Exception e3) {
				System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
			}
            if(flag1 == 0) {
            	try {
        			f = captureScreenshot_selenium();
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
            verificationErrors.append("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\nTimeout of "+ timeout + " seconds occured, please see error below\n" + error_msg + "\n");
        }
    }
    public void getURL(String url, int timeout) {
    	String error_msg = "";
    	File f = null;
    	int flag = 0, flag1 = 0;
        long totalTime = 0, startTime = 0, endTime = 0;
        System.out.println("Navigating to URL: " + url + "\n");
    	while(totalTime <= timeout*1000) {
            startTime = System.currentTimeMillis();
            try {
            	webDriverSupport.getWebDriver().get(url);
            	flag = 1;
                error_msg = "";
                break;
            }
            catch(Exception e) {
            	error_msg = "Error getting URL: " + url + " " + e.toString() + "\n" + e.getStackTrace().toString();
            }
            endTime = System.currentTimeMillis();
            totalTime = totalTime + (endTime - startTime);
            //System.out.println("Total time: " + totalTime);
        }
    	if(flag == 0) {
    		try {
        		String title = webDriverSupport.getWebDriver().getTitle();
        		System.out.print("Page title is: " + title + "\n");
				String currentURL = webDriverSupport.getWebDriver().getCurrentUrl();
				System.out.print("Current URL is: " + currentURL + "\n");
				//String bodyText = webDriverSupport.getElementWithoutSystemTimeout(By.tagName("BODY"), 3).getText();
				//System.out.print("Page body text is: " + bodyText + "\n");
				//String pageSource = webDriverSupport.getWebDriver().getPageSource();
				//System.out.print("Page source is: " + pageSource + "\n");
				String header = webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector("h1"), 3).getText();
				System.out.print("Page header is: " + header + "\n");
        	}
        	catch(Exception e1) {}
        	try {
        		String parentWindowHandler = webDriverSupport.getWebDriver().getWindowHandle(); //Store parent window
    			webDriverSupport.getWebDriver().switchTo().window(parentWindowHandler);
    			webDriverSupport.getWebDriver().switchTo().activeElement();
				f = captureScreenshot();
				flag1 = 1;
			} catch (HeadlessException e2) {
				System.out.print("Screenshot capture failed!\n");
			} catch (IOException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			} catch (AWTException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			}
            catch (Exception e3) {
				System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
			}
            if(flag1 == 0) {
            	try {
        			f = captureScreenshot_selenium();
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
            verificationErrors.append("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\nTimeout of "+ timeout + " seconds occured, please see error below\n" + error_msg + "\n");
        }
    }
    public void sendKeysElement(String uiLocator, String text, String locatorType, int timeout) {
    	String error_msg = "";
    	File f = null;
    	int flag = 0, flag1 = 0;
        long totalTime = 0, startTime = 0, endTime = 0;
        if(!uiLocator.equalsIgnoreCase("password"))
        	System.out.println("Typing " + text + " into element " + locatorType + ": " + uiLocator);
        else if(uiLocator.equalsIgnoreCase("password"))
        	System.out.println("Typing XXXXXXXX into element: " + uiLocator);
    	while(totalTime <= timeout*1000) {
            startTime = System.currentTimeMillis();
            try {
            	if(locatorType.equalsIgnoreCase("by name"))
            		webDriverSupport.getElementWithoutSystemTimeout(By.name(uiLocator), 0).sendKeys(text);
            	else if(locatorType.equalsIgnoreCase("by id"))
            		webDriverSupport.getElementWithoutSystemTimeout(By.id(uiLocator), 0).sendKeys(text);
            	else if(locatorType.equalsIgnoreCase("by css"))
            		webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector(uiLocator), 0).sendKeys(text);
            	else if(locatorType.equalsIgnoreCase("by xpath"))
            		webDriverSupport.getElementWithoutSystemTimeout(By.xpath(uiLocator), 0).sendKeys(text);
                flag = 1;
                error_msg = "";
                break;
            }
            catch(Exception e) {
            	error_msg = "Element " + locatorType + ": " + uiLocator + " not found! " + e.toString() + "\n" + e.getStackTrace().toString();
            }
            catch(AssertionError e) {error_msg = e.toString() + "\n" + e.getStackTrace().toString();}
            endTime = System.currentTimeMillis();
            totalTime = totalTime + (endTime - startTime);
        }
    	if(flag == 0) {
    		try {
        		String title = webDriverSupport.getWebDriver().getTitle();
        		System.out.print("Page title is: " + title + "\n");
				String currentURL = webDriverSupport.getWebDriver().getCurrentUrl();
				System.out.print("Current URL is: " + currentURL + "\n");
				//String bodyText = webDriverSupport.getElementWithoutSystemTimeout(By.tagName("BODY"), 3).getText();
				//System.out.print("Page body text is: " + bodyText + "\n");
				//String pageSource = webDriverSupport.getWebDriver().getPageSource();
				//System.out.print("Page source is: " + pageSource + "\n");
				String header = webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector("h1"), 3).getText();
				System.out.print("Page header is: " + header + "\n");
        	}
        	catch(Exception e1) {}
        	try {
        		String parentWindowHandler = webDriverSupport.getWebDriver().getWindowHandle(); //Store parent window
    			webDriverSupport.getWebDriver().switchTo().window(parentWindowHandler);
    			webDriverSupport.getWebDriver().switchTo().activeElement();
				f = captureScreenshot();
				flag1 = 1;
			} catch (HeadlessException e2) {
				System.out.print("Screenshot capture failed!\n");
			} catch (IOException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			} catch (AWTException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			}
            catch (Exception e3) {
				System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
			}
            if(flag1 == 0) {
            	try {
        			f = captureScreenshot_selenium();
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
            verificationErrors.append("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\nTimeout of "+ timeout + " seconds occured, please see error below\n" + error_msg + "\n");
        }
    }
    public void clearElement(String uiLocator, String locatorType, int timeout) {
    	String error_msg = "";
    	File f = null;
    	int flag = 0, flag1 = 0;
        long totalTime = 0, startTime = 0, endTime = 0;
        System.out.println("Clearing element " + locatorType + ": " + uiLocator);
    	while(totalTime <= timeout*1000) {
            startTime = System.currentTimeMillis();
            try {
            	if(locatorType.equalsIgnoreCase("by name")) 
            		webDriverSupport.getElementWithoutSystemTimeout(By.name(uiLocator), 0).clear();
            	else if(locatorType.equalsIgnoreCase("by id"))
            		webDriverSupport.getElementWithoutSystemTimeout(By.id(uiLocator), 0).clear();
            	else if(locatorType.equalsIgnoreCase("by css"))
            		webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector(uiLocator), 0).clear();
            	else if(locatorType.equalsIgnoreCase("by xpath"))
            		webDriverSupport.getElementWithoutSystemTimeout(By.xpath(uiLocator), 0).clear();
                flag = 1;
                error_msg = "";
                break;
            }
            catch(Exception e) {
            	error_msg = "Element " + locatorType + ": " + uiLocator + " not found! " + e.toString() + "\n" + e.getStackTrace().toString();
            }
            catch(AssertionError e) {error_msg = e.toString() + "\n" + e.getStackTrace().toString();}
            endTime = System.currentTimeMillis();
            totalTime = totalTime + (endTime - startTime);
        }
    	if(flag == 0) {
    		try {
        		String title = webDriverSupport.getWebDriver().getTitle();
        		System.out.print("Page title is: " + title + "\n");
				String currentURL = webDriverSupport.getWebDriver().getCurrentUrl();
				System.out.print("Current URL is: " + currentURL + "\n");
				//String bodyText = webDriverSupport.getElementWithoutSystemTimeout(By.tagName("BODY"), 3).getText();
				//System.out.print("Page body text is: " + bodyText + "\n");
				//String pageSource = webDriverSupport.getWebDriver().getPageSource();
				//System.out.print("Page source is: " + pageSource + "\n");
				String header = webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector("h1"), 3).getText();
				System.out.print("Page header is: " + header + "\n");
        	}
        	catch(Exception e1) {}
        	try {
        		String parentWindowHandler = webDriverSupport.getWebDriver().getWindowHandle(); //Store parent window
    			webDriverSupport.getWebDriver().switchTo().window(parentWindowHandler);
    			webDriverSupport.getWebDriver().switchTo().activeElement();
				f = captureScreenshot();
				flag1 = 1;
			} catch (HeadlessException e2) {
				System.out.print("Screenshot capture failed!\n");
			} catch (IOException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			} catch (AWTException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			}
            catch (Exception e3) {
				System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
			}
            if(flag1 == 0) {
            	try {
        			f = captureScreenshot_selenium();
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
            verificationErrors.append("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\nTimeout of "+ timeout + " seconds occured, please see error below\n" + error_msg + "\n");
        }
    }
    
    public void selectByVisibleText(String uiLocator, String locatorType, String value, int timeout) {
    	String error_msg = "";
    	File f = null;
    	int flag = 0, flag1 = 0;
        long totalTime = 0, startTime = 0, endTime = 0;
        System.out.println("Selecting value of: " + value + " for element " + locatorType + ": " + uiLocator);
    	while(totalTime <= timeout*1000) {
            startTime = System.currentTimeMillis();
            try {
            	if(locatorType.equalsIgnoreCase("by name")) 
            		new Select(webDriverSupport.getElementWithoutSystemTimeout(By.name(uiLocator), timeout)).selectByVisibleText(value);
            	else if(locatorType.equalsIgnoreCase("by id"))
            		new Select(webDriverSupport.getElementWithoutSystemTimeout(By.id(uiLocator), timeout)).selectByVisibleText(value);
            	else if(locatorType.equalsIgnoreCase("by css"))
            		new Select(webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector(uiLocator), timeout)).selectByVisibleText(value);
            	else if(locatorType.equalsIgnoreCase("by xpath"))
            		new Select(webDriverSupport.getElementWithoutSystemTimeout(By.xpath(uiLocator), timeout)).selectByVisibleText(value);
                flag = 1;
                error_msg = "";
                break;
            }
            catch(Exception e) {
            	error_msg = "Element " + locatorType + ": " + uiLocator + " not found! " + e.toString() + "\n" + e.getStackTrace().toString();
            }
            catch(AssertionError e) {error_msg = e.toString() + "\n" + e.getStackTrace().toString();}
            endTime = System.currentTimeMillis();
            totalTime = totalTime + (endTime - startTime);
        }
    	if(flag == 0) {
    		try {
        		String title = webDriverSupport.getWebDriver().getTitle();
        		System.out.print("Page title is: " + title + "\n");
				String currentURL = webDriverSupport.getWebDriver().getCurrentUrl();
				System.out.print("Current URL is: " + currentURL + "\n");
				//String bodyText = webDriverSupport.getElementWithoutSystemTimeout(By.tagName("BODY"), 3).getText();
				//System.out.print("Page body text is: " + bodyText + "\n");
				//String pageSource = webDriverSupport.getWebDriver().getPageSource();
				//System.out.print("Page source is: " + pageSource + "\n");
				String header = webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector("h1"), 3).getText();
				System.out.print("Page header is: " + header + "\n");
        	}
        	catch(Exception e1) {}
        	try {
        		String parentWindowHandler = webDriverSupport.getWebDriver().getWindowHandle(); //Store parent window
    			webDriverSupport.getWebDriver().switchTo().window(parentWindowHandler);
    			webDriverSupport.getWebDriver().switchTo().activeElement();
				f = captureScreenshot();
				flag1 = 1;
			} catch (HeadlessException e2) {
				System.out.print("Screenshot capture failed!\n");
			} catch (IOException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			} catch (AWTException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			}
            catch (Exception e3) {
				System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
			}
            if(flag1 == 0) {
            	try {
        			f = captureScreenshot_selenium();
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
            verificationErrors.append("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\nTimeout of "+ timeout + " seconds occured, please see error below\n" + error_msg + "\n");
        }
    }
    public void VerifyEquals(String uiLocator, String locatorType, String msg, String exp) {
    	int timeout = 20;
    	String error_msg = "";
    	File f = null;
    	int flag = 0, flag1 = 0;
        long totalTime = 0, startTime = 0, endTime = 0;
    	System.out.println("Comparing text from element " + locatorType + ": " + uiLocator + " with expected value: " + exp);
    	while(totalTime <= timeout*1000) {
            startTime = System.currentTimeMillis();
            try {
            	if(locatorType.equalsIgnoreCase("by name")) {
            		webDriverSupport.getElementWithoutSystemTimeout(By.name(uiLocator), 0);
            		Assert.assertEquals(msg, exp, webDriverSupport.getElementWithWait(By.name(uiLocator), timeout).getText());
            	}
            	else if(locatorType.equalsIgnoreCase("by id")) {
            		webDriverSupport.getElementWithoutSystemTimeout(By.id(uiLocator), 0);
            		Assert.assertEquals(msg, exp, webDriverSupport.getElementWithWait(By.id(uiLocator), timeout).getText());
            	}
            	else if(locatorType.equalsIgnoreCase("by css")) {
            		webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector(uiLocator), 0);
            		Assert.assertEquals(msg, exp, webDriverSupport.getElementWithWait(By.cssSelector(uiLocator), timeout).getText());
            	}
            	else if(locatorType.equalsIgnoreCase("by xpath")) {
            		webDriverSupport.getElementWithoutSystemTimeout(By.xpath(uiLocator), 0);
            		Assert.assertEquals(msg, exp, webDriverSupport.getElementWithWait(By.xpath(uiLocator), timeout).getText());
            	}
            	else if(locatorType.equalsIgnoreCase("by linkText")) {
            		webDriverSupport.getElementWithoutSystemTimeout(By.linkText(uiLocator), 0);
            		Assert.assertEquals(msg, exp, webDriverSupport.getElementWithWait(By.linkText(uiLocator), timeout).getText());
            	}
                flag = 1;
                error_msg = "";
                break;
            }
            catch(Exception e) {
            	error_msg = "Element " + locatorType + ": " + uiLocator + " not found! " + e.toString() + "\n" + e.getStackTrace().toString();
            }
            catch(AssertionError e) {error_msg = e.toString() + "\n" + e.getStackTrace().toString();}
            endTime = System.currentTimeMillis();
            totalTime = totalTime + (endTime - startTime);
            //System.out.println("Total time: " + totalTime);
        }
    	if(flag == 0) {
    		try {
        		String title = webDriverSupport.getWebDriver().getTitle();
        		System.out.print("Page title is: " + title + "\n");
				String currentURL = webDriverSupport.getWebDriver().getCurrentUrl();
				System.out.print("Current URL is: " + currentURL + "\n");
				//String bodyText = webDriverSupport.getElementWithoutSystemTimeout(By.tagName("BODY"), 3).getText();
				//System.out.print("Page body text is: " + bodyText + "\n");
				//String pageSource = webDriverSupport.getWebDriver().getPageSource();
				//System.out.print("Page source is: " + pageSource + "\n");
				String header = webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector("h1"), 3).getText();
				System.out.print("Page header is: " + header + "\n");
        	}
        	catch(Exception e1) {}
        	try {
        		String parentWindowHandler = webDriverSupport.getWebDriver().getWindowHandle(); //Store parent window
    			webDriverSupport.getWebDriver().switchTo().window(parentWindowHandler);
    			webDriverSupport.getWebDriver().switchTo().activeElement();
				f = captureScreenshot();
				flag1 = 1;
			} catch (HeadlessException e2) {
				System.out.print("Screenshot capture failed!\n");
			} catch (IOException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			} catch (AWTException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			}
            catch (Exception e3) {
				System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
			}
            if(flag1 == 0) {
            	try {
        			f = captureScreenshot_selenium();
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
            verificationErrors.append("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\nTimeout of "+ timeout + " seconds occured, please see error below\n" + error_msg + "\n");
        }
	}
    public void VerifySelected(String uiLocator, String locatorType, String msg) {
    	int timeout = 10;
    	String error_msg = "";
    	File f = null;
    	int flag = 0, flag1 = 0;
        long totalTime = 0, startTime = 0, endTime = 0;
    	System.out.println("Verifying radio button from element " + locatorType + ": " + uiLocator + " is selected");
    	while(totalTime <= timeout*1000) {
            startTime = System.currentTimeMillis();
            try {
            	if(locatorType.equalsIgnoreCase("by name")) {
            		webDriverSupport.getElementWithoutSystemTimeout(By.name(uiLocator), 0);
            		Assert.assertTrue(msg, webDriverSupport.getElementWithWait(By.name(uiLocator), timeout).isSelected());
            	}
            	else if(locatorType.equalsIgnoreCase("by id")) {
            		webDriverSupport.getElementWithoutSystemTimeout(By.id(uiLocator), 0);
            		Assert.assertTrue(msg, webDriverSupport.getElementWithWait(By.id(uiLocator), timeout).isSelected());
            	}
            	else if(locatorType.equalsIgnoreCase("by css")) {
            		webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector(uiLocator), 0);
            		Assert.assertTrue(msg, webDriverSupport.getElementWithWait(By.cssSelector(uiLocator), timeout).isSelected());
            	}
            	else if(locatorType.equalsIgnoreCase("by xpath")) {
            		webDriverSupport.getElementWithoutSystemTimeout(By.xpath(uiLocator), 0);
            		Assert.assertTrue(msg, webDriverSupport.getElementWithWait(By.xpath(uiLocator), timeout).isSelected());
            	}
            	else if(locatorType.equalsIgnoreCase("by linkText")) {
            		webDriverSupport.getElementWithoutSystemTimeout(By.linkText(uiLocator), 0);
            		Assert.assertTrue(msg, webDriverSupport.getElementWithWait(By.linkText(uiLocator), timeout).isSelected());
            	}
                flag = 1;
                error_msg = "";
                break;
            }
            catch(Exception e) {
            	error_msg = "Element " + locatorType + ": " + uiLocator + " not found! " + e.toString() + "\n" + e.getStackTrace().toString();
            }
            catch(AssertionError e) {error_msg = e.toString() + "\n" + e.getStackTrace().toString();}
            endTime = System.currentTimeMillis();
            totalTime = totalTime + (endTime - startTime);
            //System.out.println("Total time: " + totalTime);
        }
    	if(flag == 0) {
    		try {
        		String title = webDriverSupport.getWebDriver().getTitle();
        		System.out.print("Page title is: " + title + "\n");
				String currentURL = webDriverSupport.getWebDriver().getCurrentUrl();
				System.out.print("Current URL is: " + currentURL + "\n");
				//String bodyText = webDriverSupport.getElementWithoutSystemTimeout(By.tagName("BODY"), 3).getText();
				//System.out.print("Page body text is: " + bodyText + "\n");
				//String pageSource = webDriverSupport.getWebDriver().getPageSource();
				//System.out.print("Page source is: " + pageSource + "\n");
				String header = webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector("h1"), 3).getText();
				System.out.print("Page header is: " + header + "\n");
        	}
        	catch(Exception e1) {}
        	try {
        		String parentWindowHandler = webDriverSupport.getWebDriver().getWindowHandle(); //Store parent window
    			webDriverSupport.getWebDriver().switchTo().window(parentWindowHandler);
    			webDriverSupport.getWebDriver().switchTo().activeElement();
				f = captureScreenshot();
				flag1 = 1;
			} catch (HeadlessException e2) {
				System.out.print("Screenshot capture failed!\n");
			} catch (IOException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			} catch (AWTException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			}
            catch (Exception e3) {
				System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
			}
            if(flag1 == 0) {
            	try {
        			f = captureScreenshot_selenium();
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
            verificationErrors.append("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\nTimeout of "+ timeout + " seconds occured, please see error below\n" + error_msg + "\n");
        }
	}
    public void VerifyEqualsByAttributeValue(String uiLocator, String locatorType, String msg, String exp, String reg_expr) {
    	int timeout = 10;
    	String error_msg = "";
    	File f = null;
    	int flag = 0, flag1 = 0;
        long totalTime = 0, startTime = 0, endTime = 0;
    	System.out.println("Comparing attribute value from element " + locatorType + ": " + uiLocator + " with expected value: " + exp);
    	while(totalTime <= timeout*1000) {
            startTime = System.currentTimeMillis();
            if(reg_expr.equalsIgnoreCase("no")) {
            	try {
            		if(locatorType.equalsIgnoreCase("by name")) {
            			webDriverSupport.getElementWithoutSystemTimeout(By.name(uiLocator), 0);
            			Assert.assertEquals(msg, exp, webDriverSupport.getElementWithWait(By.name(uiLocator), timeout).getAttribute("value"));
            		}
            		else if(locatorType.equalsIgnoreCase("by id")) {
            			webDriverSupport.getElementWithoutSystemTimeout(By.id(uiLocator), 0);
            			Assert.assertEquals(msg, exp, webDriverSupport.getElementWithWait(By.id(uiLocator), timeout).getAttribute("value"));
            		}
            		else if(locatorType.equalsIgnoreCase("by css")) {
            			webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector(uiLocator), 0);
            			Assert.assertEquals(msg, exp, webDriverSupport.getElementWithWait(By.cssSelector(uiLocator), timeout).getAttribute("value"));
            		}
            		else if(locatorType.equalsIgnoreCase("by xpath")) {
            			webDriverSupport.getElementWithoutSystemTimeout(By.xpath(uiLocator), 0);
            			Assert.assertEquals(msg, exp, webDriverSupport.getElementWithWait(By.xpath(uiLocator), timeout).getAttribute("value"));
            		}
            		else if(locatorType.equalsIgnoreCase("by linkText")) {
            			webDriverSupport.getElementWithoutSystemTimeout(By.linkText(uiLocator), 0);
            			Assert.assertEquals(msg, exp, webDriverSupport.getElementWithWait(By.linkText(uiLocator), timeout).getAttribute("value"));
            		}
            		flag = 1;
            		error_msg = "";
            		break;
            	}
            	catch(Exception e) {
            		error_msg = "Element " + locatorType + ": " + uiLocator + " not found! " + e.toString() + "\n" + e.getStackTrace().toString();
            	}
            	catch(AssertionError e) {error_msg = e.toString() + "\n" + e.getStackTrace().toString();}
            	endTime = System.currentTimeMillis();
            	totalTime = totalTime + (endTime - startTime);
            	//System.out.println("Total time: " + totalTime);
            }
            else if(reg_expr.equalsIgnoreCase("yes")) {
            	try {
            		if(locatorType.equalsIgnoreCase("by name")) {
            			webDriverSupport.getElementWithoutSystemTimeout(By.name(uiLocator), 0);
            			Assert.assertTrue(msg, Pattern.compile(exp).matcher(webDriverSupport.getElementWithWait(By.name(uiLocator), timeout).getAttribute("value")).find());
            		}
            		else if(locatorType.equalsIgnoreCase("by id")) {
            			webDriverSupport.getElementWithoutSystemTimeout(By.id(uiLocator), 0);
            			Assert.assertTrue(msg, Pattern.compile(exp).matcher(webDriverSupport.getElementWithWait(By.id(uiLocator), timeout).getAttribute("value")).find());
            		}
            		else if(locatorType.equalsIgnoreCase("by css")) {
            			webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector(uiLocator), 0);
            			Assert.assertTrue(msg, Pattern.compile(exp).matcher(webDriverSupport.getElementWithWait(By.cssSelector(uiLocator), timeout).getAttribute("value")).find());
            		}
            		else if(locatorType.equalsIgnoreCase("by xpath")) {
            			webDriverSupport.getElementWithoutSystemTimeout(By.xpath(uiLocator), 0);
            			Assert.assertTrue(msg, Pattern.compile(exp).matcher(webDriverSupport.getElementWithWait(By.xpath(uiLocator), timeout).getAttribute("value")).find());
            		}
            		else if(locatorType.equalsIgnoreCase("by linkText")) {
            			webDriverSupport.getElementWithoutSystemTimeout(By.linkText(uiLocator), 0);
            			Assert.assertTrue(msg, Pattern.compile(exp).matcher(webDriverSupport.getElementWithWait(By.linkText(uiLocator), timeout).getAttribute("value")).find());
            		}
            		flag = 1;
            		error_msg = "";
            		break;
            	}
            	catch(Exception e) {
            		error_msg = "Element " + locatorType + ": " + uiLocator + " not found! " + e.toString() + "\n" + e.getStackTrace().toString();
            	}
            	catch(AssertionError e) {error_msg = e.toString() + "\n" + e.getStackTrace().toString();}
            	endTime = System.currentTimeMillis();
            	totalTime = totalTime + (endTime - startTime);
            	//System.out.println("Total time: " + totalTime);
            }
        }
    	if(flag == 0) {
    		try {
        		String title = webDriverSupport.getWebDriver().getTitle();
        		System.out.print("Page title is: " + title + "\n");
				String currentURL = webDriverSupport.getWebDriver().getCurrentUrl();
				System.out.print("Current URL is: " + currentURL + "\n");
				//String bodyText = webDriverSupport.getElementWithoutSystemTimeout(By.tagName("BODY"), 3).getText();
				//System.out.print("Page body text is: " + bodyText + "\n");
				//String pageSource = webDriverSupport.getWebDriver().getPageSource();
				//System.out.print("Page source is: " + pageSource + "\n");
				String header = webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector("h1"), 3).getText();
				System.out.print("Page header is: " + header + "\n");
        	}
        	catch(Exception e1) {}
        	try {
        		String parentWindowHandler = webDriverSupport.getWebDriver().getWindowHandle(); //Store parent window
    			webDriverSupport.getWebDriver().switchTo().window(parentWindowHandler);
    			JavascriptExecutor jse = (JavascriptExecutor) webDriverSupport.getWebDriver();
    			jse.executeScript("document.getElementById('elementid').focus();");
				f = captureScreenshot();
				flag1 = 1;
			} catch (HeadlessException e2) {
				System.out.print("Screenshot capture failed!\n");
			} catch (IOException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			} catch (AWTException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			}
            catch (Exception e3) {
				System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
			}
            if(flag1 == 0) {
            	try {
        			f = captureScreenshot_selenium();
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
            verificationErrors.append("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\nTimeout of "+ timeout + " seconds occured, please see error below\n" + error_msg + "\n");
        }
	}
    public void VerifyBackgroundColor(WebElement element, String msg, String exp_color) {
    	int timeout = 10;
    	String error_msg = "";
    	File f = null;
    	int flag = 0, flag1 = 0;
        long totalTime = 0, startTime = 0, endTime = 0;
    	System.out.println("Comparing background color from element " + element.toString() + " with expected color value: " + exp_color);
    	while(totalTime <= timeout*1000) {
            startTime = System.currentTimeMillis();
            try {
            	element.isDisplayed();
            	Assert.assertEquals(msg, exp_color, element.getCssValue("background-color"));
                flag = 1;
                error_msg = "";
                break;
            }
            catch(Exception e) {
            	error_msg = "Element " + element.toString() + " not found! " + e.toString() + "\n" + e.getStackTrace().toString();
            }
            catch(AssertionError e) {error_msg = e.toString() + "\n" + e.getStackTrace().toString();}
            endTime = System.currentTimeMillis();
            totalTime = totalTime + (endTime - startTime);
            //System.out.println("Total time: " + totalTime);
        }
    	if(flag == 0) {
    		try {
        		String title = webDriverSupport.getWebDriver().getTitle();
        		System.out.print("Page title is: " + title + "\n");
				String currentURL = webDriverSupport.getWebDriver().getCurrentUrl();
				System.out.print("Current URL is: " + currentURL + "\n");
				//String bodyText = webDriverSupport.getElementWithoutSystemTimeout(By.tagName("BODY"), 3).getText();
				//System.out.print("Page body text is: " + bodyText + "\n");
				//String pageSource = webDriverSupport.getWebDriver().getPageSource();
				//System.out.print("Page source is: " + pageSource + "\n");
				String header = webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector("h1"), 3).getText();
				System.out.print("Page header is: " + header + "\n");
        	}
        	catch(Exception e1) {}
        	try {
        		String parentWindowHandler = webDriverSupport.getWebDriver().getWindowHandle(); //Store parent window
        		webDriverSupport.getWebDriver().switchTo().window(parentWindowHandler);
        		webDriverSupport.getWebDriver().switchTo().activeElement();
				f = captureScreenshot();
				flag1 = 1;
			} catch (HeadlessException e2) {
				System.out.print("Screenshot capture failed!\n");
			} catch (IOException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			} catch (AWTException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			}
            catch (Exception e3) {
				System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
			}
            if(flag1 == 0) {
            	try {
        			f = captureScreenshot_selenium();
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
            verificationErrors.append("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\nTimeout of "+ timeout + " seconds occured, please see error below\n" + error_msg + "\n");
        }
	}
    public void VerifyBorderColor(WebElement element, String msg, String exp_color) {
    	int timeout = 10;
    	String error_msg = "";
    	File f = null;
    	int flag = 0, flag1 = 0;
        long totalTime = 0, startTime = 0, endTime = 0;
    	System.out.println("Comparing background color from element " + element.toString() + " with expected color value: " + exp_color);
    	while(totalTime <= timeout*1000) {
            startTime = System.currentTimeMillis();
            try {
            	element.isDisplayed();
            	Assert.assertEquals(msg, exp_color, element.getCssValue("border-color"));
                flag = 1;
                error_msg = "";
                break;
            }
            catch(Exception e) {
            	error_msg = "Element " + element.toString() + " not found! " + e.toString() + "\n" + e.getStackTrace().toString();
            }
            catch(AssertionError e) {error_msg = e.toString() + "\n" + e.getStackTrace().toString();}
            endTime = System.currentTimeMillis();
            totalTime = totalTime + (endTime - startTime);
            //System.out.println("Total time: " + totalTime);
        }
    	if(flag == 0) {
    		try {
        		String title = webDriverSupport.getWebDriver().getTitle();
        		System.out.print("Page title is: " + title + "\n");
				String currentURL = webDriverSupport.getWebDriver().getCurrentUrl();
				System.out.print("Current URL is: " + currentURL + "\n");
				//String bodyText = webDriverSupport.getElementWithoutSystemTimeout(By.tagName("BODY"), 3).getText();
				//System.out.print("Page body text is: " + bodyText + "\n");
				//String pageSource = webDriverSupport.getWebDriver().getPageSource();
				//System.out.print("Page source is: " + pageSource + "\n");
				String header = webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector("h1"), 3).getText();
				System.out.print("Page header is: " + header + "\n");
        	}
        	catch(Exception e1) {}
        	try {
        		String parentWindowHandler = webDriverSupport.getWebDriver().getWindowHandle(); //Store parent window
        		webDriverSupport.getWebDriver().switchTo().window(parentWindowHandler);
        		webDriverSupport.getWebDriver().switchTo().activeElement();
				f = captureScreenshot();
				flag1 = 1;
			} catch (HeadlessException e2) {
				System.out.print("Screenshot capture failed!\n");
			} catch (IOException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			} catch (AWTException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			}
            catch (Exception e3) {
				System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
			}
            if(flag1 == 0) {
            	try {
        			f = captureScreenshot_selenium();
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
            verificationErrors.append("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\nTimeout of "+ timeout + " seconds occured, please see error below\n" + error_msg + "\n");
        }
	}
    public void VerifyFail(String msg) {
    	File f = null;
    	int flag = 0;
    	try {
    		Assert.assertEquals(1, 2);
    	}
    	catch(AssertionError e)
    	{
    		try {
    			String parentWindowHandler = webDriverSupport.getWebDriver().getWindowHandle(); //Store parent window
    			webDriverSupport.getWebDriver().switchTo().window(parentWindowHandler);
    			webDriverSupport.getWebDriver().switchTo().activeElement();
			f = captureScreenshot();
			flag = 1;
		} catch (HeadlessException e2) {
			e2.printStackTrace();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (AWTException e2) {
			e2.printStackTrace();
		}
    	catch (Exception e3) {
			System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
			System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
		}
    		if(flag == 0) {
    			try {
            		String title = webDriverSupport.getWebDriver().getTitle();
            		System.out.print("Page title is: " + title + "\n");
    				String currentURL = webDriverSupport.getWebDriver().getCurrentUrl();
    				System.out.print("Current URL is: " + currentURL + "\n");
    				//String bodyText = webDriverSupport.getElementWithoutSystemTimeout(By.tagName("BODY"), 3).getText();
    				//System.out.print("Page body text is: " + bodyText + "\n");
    				//String pageSource = webDriverSupport.getWebDriver().getPageSource();
    				//System.out.print("Page source is: " + pageSource + "\n");
    				String header = webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector("h1"), 3).getText();
    				System.out.print("Page header is: " + header + "\n");
            	}
            	catch(Exception e1) {}
    			try {
    				f = captureScreenshot_selenium();
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
    		verificationErrors.append("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\nPlease see error below\n" + msg + "\n");
    	}
    }
    public void VerifyFailNoScreenshot(String msg) {
    	try {
    		Assert.assertEquals(1, 2);
    	}
    	catch(AssertionError e)
    	{
    		verificationErrors.append("\nPlease see error below\n" + msg + "\n");
    	}
    }
    public String GetUTCdatetimeAsString()
    {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }
    public Date StringDateToDate(String StrDate)
    {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
        try
        {
            dateToReturn = (Date)dateFormat.parse(StrDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return dateToReturn;
    }
    public void VerifyTrue(String uiLocator, String locatorType, String msg, String exp, String reg_expr) {
    	int timeout = 10;
    	String error_msg = "", text = "";
    	File f = null;
    	int flag = 0, flag1 = 0;
        long totalTime = 0, startTime = 0, endTime = 0;
        if(reg_expr.equalsIgnoreCase("yes"))
        	System.out.println("Comparing that text from element " + locatorType + ": " + uiLocator + " matches regular expression: " + exp);
        else
        	System.out.println("Comparing text from element " + locatorType + ": " + uiLocator + " with expected value: " + exp);
    	while(totalTime <= timeout*1000) {
            startTime = System.currentTimeMillis();
            if(reg_expr.equalsIgnoreCase("yes"))
        	{
        		try {
        			if(locatorType.equalsIgnoreCase("by name")) {
        				webDriverSupport.getElementWithoutSystemTimeout(By.name(uiLocator), 0);
        				text = webDriverSupport.getElementWithWait(By.name(uiLocator), timeout).getText();
        				Assert.assertTrue(msg, Pattern.compile(exp).matcher(webDriverSupport.getElementWithWait(By.name(uiLocator), timeout).getText()).find());
        			}
        			else if(locatorType.equalsIgnoreCase("by id")) {
        				webDriverSupport.getElementWithoutSystemTimeout(By.id(uiLocator), 0);
        				text = webDriverSupport.getElementWithWait(By.id(uiLocator), timeout).getText();
        				Assert.assertTrue(msg, Pattern.compile(exp).matcher(webDriverSupport.getElementWithWait(By.id(uiLocator), timeout).getText()).find());
        			}
        			else if(locatorType.equalsIgnoreCase("by css")) {
        				webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector(uiLocator), 0);
        				text = webDriverSupport.getElementWithWait(By.cssSelector(uiLocator), timeout).getText();
        				Assert.assertTrue(msg, Pattern.compile(exp).matcher(webDriverSupport.getElementWithWait(By.cssSelector(uiLocator), timeout).getText()).find());
        			}
        			else if(locatorType.equalsIgnoreCase("by xpath")) {
        				webDriverSupport.getElementWithoutSystemTimeout(By.xpath(uiLocator), 0);
        				text = webDriverSupport.getElementWithWait(By.xpath(uiLocator), timeout).getText();
        				Assert.assertTrue(msg, Pattern.compile(exp).matcher(webDriverSupport.getElementWithWait(By.xpath(uiLocator), timeout).getText()).find());
        			}
        			else if(locatorType.equalsIgnoreCase("by linkText")) {
        				webDriverSupport.getElementWithoutSystemTimeout(By.linkText(uiLocator), 0);
        				text = webDriverSupport.getElementWithWait(By.linkText(uiLocator), timeout).getText();
        				Assert.assertTrue(msg, Pattern.compile(exp).matcher(webDriverSupport.getElementWithWait(By.linkText(uiLocator), timeout).getText()).find());
        			}
        			flag = 1;
        			error_msg = "";
        			break;
        		}
        		catch(Exception e) {
        			error_msg = "Element " + locatorType + ": " + uiLocator + " not found! " + e.toString() + "\n" + e.getStackTrace().toString();
        		}
        		catch(AssertionError e) {error_msg = msg + " did not match regular expression pattern of: " + exp + ", Actual value was: " + text + " " + e.toString() + "\n" + e.getStackTrace().toString();}
        	    endTime = System.currentTimeMillis();
        	    totalTime = totalTime + (endTime - startTime);
        	    //System.out.println("Total time: " + totalTime);
        	}
            else if(reg_expr.equalsIgnoreCase("no"))
        	{
            	try {
        			if(locatorType.equalsIgnoreCase("by name")) {
        				webDriverSupport.getElementWithoutSystemTimeout(By.name(uiLocator), 0);
        				Assert.assertTrue(msg, webDriverSupport.getElementWithWait(By.name(uiLocator), timeout).isDisplayed());
        			}
        			else if(locatorType.equalsIgnoreCase("by id")) {
        				webDriverSupport.getElementWithoutSystemTimeout(By.id(uiLocator), 0);
        				Assert.assertTrue(msg, webDriverSupport.getElementWithWait(By.id(uiLocator), timeout).isDisplayed());
        			}
        			else if(locatorType.equalsIgnoreCase("by css")) {
        				webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector(uiLocator), 0);
        				Assert.assertTrue(msg, webDriverSupport.getElementWithWait(By.cssSelector(uiLocator), timeout).isDisplayed());
        			}
        			else if(locatorType.equalsIgnoreCase("by xpath")) {
        				webDriverSupport.getElementWithoutSystemTimeout(By.xpath(uiLocator), 0);
        				Assert.assertTrue(msg, webDriverSupport.getElementWithWait(By.xpath(uiLocator), timeout).isDisplayed());
        			}
        			else if(locatorType.equalsIgnoreCase("by linkText")) {
        				webDriverSupport.getElementWithoutSystemTimeout(By.linkText(uiLocator), 0);
        				Assert.assertTrue(msg, webDriverSupport.getElementWithWait(By.linkText(uiLocator), timeout).isDisplayed());
        			}
        			flag = 1;
        			error_msg = "";
        			break;
        		}
            	catch(Exception e) {
            		error_msg = "Element " + locatorType + ": " + uiLocator + " not found! " + e.toString() + "\n" + e.getStackTrace().toString();
            	}
        		catch(AssertionError e) {error_msg = e.toString() + "\n" + e.getStackTrace().toString();}
        	    endTime = System.currentTimeMillis();
        	    totalTime = totalTime + (endTime - startTime);
        	    //System.out.println("Total time: " + totalTime);
        	}
        }
    	if(flag == 0) {
    		try {
        		String title = webDriverSupport.getWebDriver().getTitle();
        		System.out.print("Page title is: " + title + "\n");
				String currentURL = webDriverSupport.getWebDriver().getCurrentUrl();
				System.out.print("Current URL is: " + currentURL + "\n");
				//String bodyText = webDriverSupport.getElementWithoutSystemTimeout(By.tagName("BODY"), 3).getText();
				//System.out.print("Page body text is: " + bodyText + "\n");
				//String pageSource = webDriverSupport.getWebDriver().getPageSource();
				//System.out.print("Page source is: " + pageSource + "\n");
				String header = webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector("h1"), 3).getText();
				System.out.print("Page header is: " + header + "\n");
        	}
        	catch(Exception e1) {}
        	try {
        		String parentWindowHandler = webDriverSupport.getWebDriver().getWindowHandle(); //Store parent window
    			webDriverSupport.getWebDriver().switchTo().window(parentWindowHandler);
    			webDriverSupport.getWebDriver().switchTo().activeElement();
				f = captureScreenshot();
				flag1 = 1;
			} catch (HeadlessException e2) {
				System.out.print("Screenshot capture failed!\n");
			} catch (IOException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			} catch (AWTException e2) {
				System.out.print("Screenshot capture failed!" + e2.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e2.getMessage() + "\n");
			}
            catch (Exception e3) {
				System.out.print("Screenshot capture failed!" + e3.getStackTrace().toString() + "\n");
				System.out.print("Screenshot capture failed!" + e3.getMessage() + "\n");
			}
            if(flag1 == 0) {
            	try {
        			f = captureScreenshot_selenium();
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
            verificationErrors.append("\nPlease see and then remove screenshot in " + f.getAbsolutePath() + "\nTimeout of "+ timeout + " seconds occured, please see error below\n" + error_msg + "\n");
        }
	}
    public void syncIELogin(int timeout) {
    	long totalTime = 0, startTime = 0, endTime = 0;
    	while(totalTime <= timeout*1000) {
    		startTime = System.currentTimeMillis();
    		try {
    			if(webDriverSupport.getElementWithoutSystemTimeout(By.cssSelector("h3"), 0).getText().equalsIgnoreCase("Sign in"))
    				break;
    		}
    		catch(Exception e) {};
    		endTime = System.currentTimeMillis();
            totalTime = totalTime + (endTime - startTime);
    	}
    }
    public void accessContinueToThisWebSiteLink(int timeout) {
    	String title = "", expected = "Certificate Error: Navigation Blocked";
    	Robot robot = null;
    	long totalTime = 0, startTime = 0, endTime = 0, flag = 0;
    	try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	while(totalTime <= timeout*1000) {
    		startTime = System.currentTimeMillis();
    		try {
    			title = webDriverSupport.getWebDriver().getTitle();
    		}
    		catch(Exception e) {};
    		//System.out.print("title: " + title + "\n");
    		try { 
    			//Click Continue to this website (not recommended). link
    			if(title.equalsIgnoreCase(expected)) 
    				webDriverSupport.getElementWithoutSystemTimeout(By.linkText("Continue to this website (not recommended)."), timeout).click();
    			webDriverSupport.getWebDriver().switchTo().alert().getText();
    			if(flag == 0) {
    				//Enter password
    				System.out.print("Executing keyboard input for IE\n");
    				pauseForATime(5000);
    				robot.keyPress(KeyEvent.VK_R);
    				robot.keyRelease(KeyEvent.VK_R);
    				robot.keyPress(KeyEvent.VK_3);
    				robot.keyRelease(KeyEvent.VK_3);
    				robot.keyPress(KeyEvent.VK_V);
    				robot.keyRelease(KeyEvent.VK_V);
    				robot.keyPress(KeyEvent.VK_1);
    				robot.keyRelease(KeyEvent.VK_1);
    				robot.keyPress(KeyEvent.VK_E);
    				robot.keyRelease(KeyEvent.VK_E);
    				robot.keyPress(KeyEvent.VK_W);
    				robot.keyRelease(KeyEvent.VK_W);
    				robot.keyPress(KeyEvent.VK_TAB);
    				robot.keyRelease(KeyEvent.VK_TAB);
    				robot.keyPress(KeyEvent.VK_TAB);
    				robot.keyRelease(KeyEvent.VK_TAB);
    				robot.keyPress(KeyEvent.VK_ENTER);
    				robot.keyRelease(KeyEvent.VK_ENTER);
    				flag = 1;
    			}
    		}
    		catch(Exception e) {};
    		endTime = System.currentTimeMillis();
            totalTime = totalTime + (endTime - startTime);
    	}
    }
    public String getOS() {
    	String os = "";
    	String currentDir = System.getProperty("user.dir");
    	if(currentDir.contains("/"))
    		os = "linux";
    	else
    		os = "windows";
    	return os;
    }
    public void allowCurrentLocation() {
    	Robot robot = null;
    	String brs = System.getProperty("webdriver.driver");
    	String os = getOS();
    	try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	if(brs.equalsIgnoreCase("firefox") && os.equalsIgnoreCase("windows")) {
    			try {
    				pauseForATime(2000);
    				robot.keyPress(KeyEvent.VK_TAB);
    				robot.keyRelease(KeyEvent.VK_TAB);
    				pauseForATime(300);
    				robot.keyPress(KeyEvent.VK_TAB);
    				robot.keyRelease(KeyEvent.VK_TAB);
    				pauseForATime(300);
    				robot.keyPress(KeyEvent.VK_ENTER);
    				robot.keyRelease(KeyEvent.VK_ENTER);
    				pauseForATime(300);
    				robot.keyPress(KeyEvent.VK_DOWN);
    				robot.keyRelease(KeyEvent.VK_DOWN);
    				pauseForATime(300);
    				robot.keyPress(KeyEvent.VK_ENTER);
    				robot.keyRelease(KeyEvent.VK_ENTER);
    			}
    			catch(Exception e) {};
    	}
    	else if(brs.equalsIgnoreCase("firefox") && os.equalsIgnoreCase("linux")) {
			try {
				pauseForATime(2000);
				robot.keyPress(KeyEvent.VK_TAB);
				robot.keyRelease(KeyEvent.VK_TAB);
				pauseForATime(300);
				robot.keyPress(KeyEvent.VK_TAB);
				robot.keyRelease(KeyEvent.VK_TAB);
				pauseForATime(300);
				robot.keyPress(KeyEvent.VK_TAB);
				robot.keyRelease(KeyEvent.VK_TAB);
				pauseForATime(300);
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
				pauseForATime(300);
				robot.keyPress(KeyEvent.VK_DOWN);
				robot.keyRelease(KeyEvent.VK_DOWN);
				pauseForATime(300);
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
			}
			catch(Exception e) {};
    	}
    }
    public void DismissAllAlerts() {
    	while(true) {
    		try {
    			pauseForATime(3000);
    			String alertText = webDriverSupport.getWebDriver().switchTo().alert().getText();
    			System.out.println("Warning! Getting alert: " + alertText);
    			webDriverSupport.getWebDriver().switchTo().alert().dismiss();
    		} catch(Exception e) {System.out.println("No Alerts was detected");break;}
    	}
    }
    public String executeGetCountQuery(String env, String query_stmt) throws ClassNotFoundException, SQLException {
    	String pwd = "", username = "<your_mysql_database_username>", getCount = "";
    	String db_conn_str = "";
    	if(env.equalsIgnoreCase("visn21")){
    		db_conn_str = "jdbc:mysql://localhost:3313/" + env;
    		pwd = "<your_mysql_database_password>";
    	}
    	else if(env.equalsIgnoreCase("visn20")){
    		db_conn_str = "jdbc:mysql://localhost:3312/" + env;
    		pwd = "<your_mysql_database_password>";
    		username="grimayloy";
    		pwd="Yev9379#";
    	}
    	else if(env.equalsIgnoreCase("visn15") || env.equalsIgnoreCase("VISNQA33")){
    		db_conn_str = "jdbc:mysql://localhost:3314/" + env;
    		pwd = "<your_mysql_database_password>";
    		username="grimayloy";
    		pwd="Yev9379#";
    	}
    	System.out.print("\nExecuting Query Statement: " + query_stmt + "\n");
    	Class.forName("com.mysql.jdbc.Driver");
    	java.sql.Connection conn = DriverManager.getConnection(db_conn_str, username, pwd);
    	try {
    	     java.sql.Statement stmt = conn.createStatement();
    	try {
    	    java.sql.ResultSet rs = stmt.executeQuery(query_stmt);
    	    try {
    	        while ( rs.next() ) {
    	            int numColumns = rs.getMetaData().getColumnCount();
    	            for ( int i = 1 ; i <= numColumns ; i++ ) {
    	               // Column numbers start at 1.
    	               // Also there are many methods on the result set to return
    	               //  the column as a particular type. Refer to the Sun documentation
    	               //  for the list of valid conversions.
    	               //System.out.println( "COLUMN " + i + " = " + rs.getObject(i) );
    	               getCount = rs.getObject(i).toString();
    	            }
    	        }
    	    } finally {
    	        try { rs.close(); } catch (Throwable ignore) { /* Propagate the original exception
    	instead of this one that you may want just logged */ }
    	    }
    	} finally {
    	    try { stmt.close(); } catch (Throwable ignore) { /* Propagate the original exception
    	instead of this one that you may want just logged */ }
    	}
    	} finally {
    	    //It's important to close the connection when you are done with it
    	    try { conn.close(); } catch (Throwable ignore) { /* Propagate the original exception
    	instead of this one that you may want just logged */ }
    	}
		return getCount;
    }
    public String[] executeGetArrayQuery(String env, String query_stmt) throws ClassNotFoundException, SQLException {
    	String pwd = "", username = "<your_mysql_database_username";
		String[] getArray;
    	String db_conn_str = "";
    	int counter = 0;
    	if(env.equalsIgnoreCase("visn21")){
    		db_conn_str = "jdbc:mysql://localhost:3313/" + env;
    		pwd = "<your_mysql_database_password>";
    	}
    	else if(env.equalsIgnoreCase("visn20")){
    		db_conn_str = "jdbc:mysql://localhost:3312/" + env;
    		pwd = "<your_mysql_database_password>";
    		username="grimayloy";
    		pwd="Yev9379#";
    	}
    	else if(env.equalsIgnoreCase("visn15") || env.equalsIgnoreCase("VISNQA33")){
    		db_conn_str = "jdbc:mysql://localhost:3314/" + env;
    		pwd = "<your_mysql_database_password>";
    		username="grimayloy";
    		pwd="Yev9379#";
    	}
    	System.out.print("\nExecuting Query Statement: " + query_stmt + "\n");
    	Class.forName("com.mysql.jdbc.Driver");
    	getArray = new String[1000000];
    	java.sql.Connection conn = DriverManager.getConnection(db_conn_str, username, pwd);
    	try {
    	     java.sql.Statement stmt = conn.createStatement();
    	try {
    	    java.sql.ResultSet rs = stmt.executeQuery(query_stmt);
    	    try {
    	        while ( rs.next() ) {
    	            int numColumns = rs.getMetaData().getColumnCount();
    	            for ( int i = 1 ; i <= numColumns ; i++ ) {
    	               // Column numbers start at 1.
    	               // Also there are many methods on the result set to return
    	               //  the column as a particular type. Refer to the Sun documentation
    	               //  for the list of valid conversions.
    	               //System.out.println( "COLUMN " + i + " = " + rs.getObject(i) );
    	               try {
    	            	   getArray[counter] = rs.getObject(i).toString();
    	            	   counter = counter + 1;
    	            	}
    	               catch(Exception e) {
    	            	   getArray[counter] = "NULL";
    	            	   counter = counter + 1;
    	               }
    	            }
    	        }
    	    } finally {
    	        try { rs.close(); } catch (Throwable ignore) { /* Propagate the original exception
    	instead of this one that you may want just logged */ }
    	    }
    	} finally {
    	    try { stmt.close(); } catch (Throwable ignore) { /* Propagate the original exception
    	instead of this one that you may want just logged */ }
    	}
    	} finally {
    	    //It's important to close the connection when you are done with it
    	    try { conn.close(); } catch (Throwable ignore) { /* Propagate the original exception
    	instead of this one that you may want just logged */ }
    	}
		return getArray;
    }
    public void clearCookies() {
    	//Delete all cookies, if they exist
    	if(!webDriverSupport.getWebDriver().manage().getCookies().isEmpty()) {
    		System.out.println("Deleting cookies: " + webDriverSupport.getWebDriver().manage().getCookies().toString());
    		webDriverSupport.getWebDriver().manage().deleteAllCookies();
    		//Verify all cookies were deleted successfully
    		if(webDriverSupport.getWebDriver().manage().getCookies().isEmpty())
    			System.out.println("All cookies were deleted successfully");
    		else
    			System.out.println("Cookies were not deleted successfully!");
    	}
    }
    public String getEnvironment() {
    	String env = "";
    	if(baseUrl.contains("dev") && !baseUrl.contains("dev2"))
    		env = "dev";
    	else if(baseUrl.contains("test") && !baseUrl.contains("test1"))
    		env = "test";
    	else if(baseUrl.contains("local"))
    		env = "local";
    	else if(baseUrl.contains("test1"))
    		env = "test1";
    	else if(baseUrl.contains("dev2"))
    		env = "dev2";
    	else
    		env = "prod";
		return env;	
    }
    public String[] getBrowserStackConfig(String filename) {
    	String MyArray[] = null;
    	String os = getOS();
    	String currentDir = System.getProperty("user.dir");
    	String csvFile = "", line = "", cvsSplitBy = ",";
    	BufferedReader br = null;
    	int counter = 0;
    	if(os.equalsIgnoreCase("windows"))
    		csvFile = currentDir + "\\src\\test\\resources\\" + filename;
    	else if(os.equalsIgnoreCase("linux"))
    		csvFile = currentDir + "/src/test/resources/" + filename;
    	//System.out.print("csvFile from getBrowserStackConfig =" + csvFile + "\n");
    	try { 
    		br = new BufferedReader(new FileReader(csvFile));
    		while ((line = br.readLine()) != null) {
    			if("".equals(line))
    				break;
    			counter++;
    			// use comma as separator
    			if(counter == 2)
    				MyArray = line.split(cvsSplitBy);
    		}
    	} catch (FileNotFoundException e) {
    		System.out.print("File: " + csvFile + " not found\n");
    		e.printStackTrace();
    	} catch (IOException e) {
    		System.out.print("File: " + csvFile + " IO Exception\n");
    		e.printStackTrace();
    	} finally {
    		if (br != null) {
    			try {
    				br.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	return MyArray;
    }
    public int getBrowserStackConfigRows(String filename) {
    	String os = getOS();
    	String currentDir = System.getProperty("user.dir");
    	String csvFile = "", line = "";
    	BufferedReader br = null;
    	int counter = 0;
    	if(os.equalsIgnoreCase("windows"))
    		csvFile = currentDir + "\\src\\test\\resources\\" + filename;
    	else if(os.equalsIgnoreCase("linux"))
    		csvFile = currentDir + "/src/test/resources/" + filename;
    	//System.out.print("csvFile from getBrowserStackConfig rows =" + csvFile + "\n");
    	try { 
    		br = new BufferedReader(new FileReader(csvFile));
    		while ((line = br.readLine()) != null) {
    			if("".equals(line))
    				break;
    			System.out.print("Getting line: " + line +"\n");
    			counter++;
    		}
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		if (br != null) {
    			try {
    				br.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	return counter - 1;
    }
    public String[] getBrowserStackConfigForRow(String filename, int row) {
    	String MyArray[] = null;
    	String os = getOS();
    	String currentDir = System.getProperty("user.dir");
    	String csvFile = "", line = "", cvsSplitBy = "\\|";
    	BufferedReader br = null;
    	int counter = 0;
    	if(os.equalsIgnoreCase("windows"))
    		csvFile = currentDir + "\\src\\test\\resources\\" + filename;
    	else if(os.equalsIgnoreCase("linux"))
    		csvFile = currentDir + "/src/test/resources/" + filename;
    	//System.out.print("csvFile from getBrowserStackConfigForRow =" + csvFile + "\n");
    	try { 
    		br = new BufferedReader(new FileReader(csvFile));
    		while ((line = br.readLine()) != null) {
    			if("".equals(line))
    				break;
    			counter++;
    			// use pipe as separator
    			if(counter - 1 == row) {
    				MyArray = line.split(cvsSplitBy);
    				break;
    			}
    		}
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		if (br != null) {
    			try {
    				br.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	return MyArray;
    }
    public int checkHowManyProcessInstancesRunning(String processName)
	{
		int counter = 0;
		String os = getOS();
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
