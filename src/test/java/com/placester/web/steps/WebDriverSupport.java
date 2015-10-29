package com.placester.web.steps;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import com.placester.web.steps.FirefoxDriverSupport;
import com.placester.web.steps.PlacesterSupport;
import com.gargoylesoftware.htmlunit.BrowserVersion;

/**
 * @author Yevgeniy Grimaylo
 */
@Service(value = "webdriverSupport")
public class WebDriverSupport {

	@Resource
    private PlacesterSupport scenericSupport;
	@Resource
    private String environment;
	@Resource
    String unlock;
    protected final Log log = LogFactory.getLog(getClass());
    private static volatile WebDriver webDriver;
    private static long lastGetDriverRequestTime = 0;
    private static String clearCookiesURL;
    public static StringBuffer USERNAME = new StringBuffer("yevgeniygrimaylo1");
    public static StringBuffer AUTOMATE_KEY = new StringBuffer("w8Hj46whcYZ3qUojsNvC");
    public static StringBuffer URL = new StringBuffer("http://" + USERNAME + ":" + AUTOMATE_KEY + "@hub.browserstack.com/wd/hub");

    @Resource
    private String baseUrl;

    public int getFindSystemWait() {
        String timeoutSetting = System.getProperty("webdriver.timeout", "10");
        return Integer.parseInt(timeoutSetting);
    }
    
    public void setFindSystemWait(int systemWait) {
        System.setProperty("webdriver.timeout", Integer.toString(systemWait));
    }

    public String getHiddenElementText(WebElement element) {

        JavascriptExecutor jsExecutor = (JavascriptExecutor) getWebDriver();

        String text = (String) jsExecutor.executeScript(
                "return arguments[0].innerText || arguments[0].textContent",
                element);

        return text;
    }

    public void setHiddenElementText(WebElement element, String text) {

        JavascriptExecutor jsExecutor = (JavascriptExecutor) getWebDriver();

        String userAgent = (String) jsExecutor
                .executeScript("return navigator.userAgent");

        String method = null;

        if (StringUtils.containsIgnoreCase(userAgent, "MSIE")) {

            method = "innerText";

        } else {

            method = "textContent";
        }

        String s = String.format("arguments[0].%s = '%s'", method, text)
                .toString();

        jsExecutor.executeScript(s, element);
    }

    public boolean isElementVisibleViaJavaScript(WebElement element) {

        JavascriptExecutor jsExecutor = (JavascriptExecutor) getWebDriver();

        String display = (String) jsExecutor.executeScript(
                "return arguments[0].style.display", element);

        boolean isVisible = true;

        if ("none".equals(display)) {
            isVisible = false;
        }

        return isVisible;
    }

    public WebElement getElementWithWait(final By locator) {
        return getElementWithWait(locator, 0);
    }

    public WebElement getElementWithWait(final By locator,
            final int timeoutExtension) {

        int timeout = getFindSystemWait() + timeoutExtension;

        WebDriverWait wait = new WebDriverWait(getWebDriver(), timeout);

        WebElement dynamicElement = wait
                .until(visibilityOfElementLocated(locator));

        return dynamicElement;
    }

    public WebElement getElementWithoutSystemTimeout(final By locator,
            final int timeout) {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), timeout);

        WebElement dynamicElement = wait
                .until(visibilityOfElementLocated(locator));

        return dynamicElement;
    }

    private ExpectedCondition<WebElement> visibilityOfElementLocated(
            final By locator) {

        return new ExpectedCondition<WebElement>() {

            public WebElement apply(WebDriver driver) {

                WebElement element = (WebElement) driver
                        .findElement(locator);

                return element.isDisplayed() ? element : null;
            }
        };
    }

    public WebElement getHiddenElementWithWait(final By locator) {
        return getHiddenElementWithWait(locator, 0);
    }

    public WebElement getHiddenElementWithWait(final By locator,
            final int timeoutExtension) {

        int timeout = getFindSystemWait() + timeoutExtension;

        WebDriverWait wait = new WebDriverWait(getWebDriver(), timeout);

        WebElement dynamicElement = wait
                .until(invisibilityOfElementLocated(locator));

        return dynamicElement;
    }

    public WebElement getHiddenElementWithoutSystemTimeout(
            final By locator, final int timeout) {
        WebDriverWait wait = new WebDriverWait(getWebDriver(), timeout);

        WebElement dynamicElement = wait
                .until(invisibilityOfElementLocated(locator));

        return dynamicElement;
    }

    private ExpectedCondition<WebElement> invisibilityOfElementLocated(
            final By locator) {

        return new ExpectedCondition<WebElement>() {

            public WebElement apply(WebDriver driver) {

                WebElement element = (WebElement) driver
                        .findElement(locator);

                return element.isDisplayed() ? null : element;
            }
        };
    }

    public String getHiddenElementAttribueValue(WebElement element,
            String attributeName) {

        JavascriptExecutor jsExecutor = (JavascriptExecutor) getWebDriver();

        return (String) jsExecutor.executeScript(String.format(
                "return arguments[0].getAttribute('%s')", attributeName),
                element);
    }

    public void setHiddenElementAttribute(WebElement element,
            String attributeName, String attributeValue) {

        JavascriptExecutor jsExecutor = (JavascriptExecutor) getWebDriver();

        jsExecutor.executeScript(String.format(
                "arguments[0].setAttribute('%s', '%s')", attributeName,
                attributeValue), element);
    }

    public String getXPathForClassAttribute(final String className) {
        return String.format(
                "contains(concat( ' ', normalize-space(@class),' '), ' %s ')",
                className);
    }

    public String getXpathForAttributeStartsWith(final String attributeName,
            final String partialAttributeValue) {
        return String.format("starts-with(@%s, '%s'", attributeName,
                partialAttributeValue);
    }

    public String getXpathForAttributeEndsWith(final String attributeName,
            final String partialAttributeValue) {
        return String.format(
                "substring(@%s, string-length(@%s) - string-length('%s')+ 1, "
                        + "string-length(@%s)) = '%s'", attributeName,
                attributeName, partialAttributeValue, attributeName,
                partialAttributeValue);
    }

    public Map<String, String> createHash(String... values) {
        return createHash(Arrays.asList(values));
    }

    public Map<String, String> createHash(List<String> values) {

        Map<String, String> hash = new HashMap<String, String>();

        for (int i = 0; i < values.size(); i += 2) {
            hash.put(values.get(i), values.get(i + 1));
        }

        return hash;
    }

    public WebDriver getWebDriver() {
 
        lastGetDriverRequestTime = System.currentTimeMillis();

        WebDriver driver = null;

        // must synchronize on class for static variable
        synchronized (WebDriverSupport.class) {
            driver = WebDriverSupport.webDriver;
            if (driver == null && environment.trim().equalsIgnoreCase("local")) {
                WebDriverSupport.webDriver = driver = createWebDriver();
            }
            else if (driver == null && environment.trim().contains("browserstack")) {
                WebDriverSupport.webDriver = driver = createWebDriverBrowserStack();
            }
        }

        return driver;
    }

    public WebDriver createWebDriverBrowserStack() {
    	String ConfigArray[] = null, filename = "";
        boolean enableNativeEvents = Boolean.getBoolean("webdriver.enable.native.events");
        String environment_global = environment;
    	//String URL = "http://" + USERNAME + ":" + AUTOMATE_KEY + "@hub.browserstack.com/wd/hub"; //browserstack
        //String URL = "http://Yevgeniy.Grimaylo%40sceneric.com:uc59e1de6f35af4d@hub.crossbrowsertesting.com:80/wd/hub"; //http://app.crossbrowsertesting.com
		WebDriver driver = null;
		DesiredCapabilities caps = new DesiredCapabilities();
		//caps.setCapability("name", "Selenium Test Example"); //http://app.crossbrowsertesting.com
	    caps.setCapability("build", "version1"); //http://app.crossbrowsertesting.com
	    //caps.setCapability("screen_resolution", "1080x1920"); //http://app.crossbrowsertesting.com
	    //caps.setCapability("record_video", "true"); //http://app.crossbrowsertesting.com
	    //caps.setCapability("record_network", "false"); //http://app.crossbrowsertesting.com
	    //caps.setCapability("record_snapshot", "true"); //http://app.crossbrowsertesting.com
	    caps.setCapability("javascriptEnabled", "true"); //http://app.crossbrowsertesting.com
		System.out.println("Using browser stack with username: " + USERNAME + " and AUTOMATE_KEY: " + AUTOMATE_KEY + " and URL: " + URL + "\n");
		//Get config values for browser stack
		if(environment_global.equalsIgnoreCase("browserstack"))
        	filename = "BrowserStackConfig.csv";
        else if(environment_global.equalsIgnoreCase("browserstack_parallel") && !unlock.trim().equalsIgnoreCase("yes") && !unlock.trim().equalsIgnoreCase("no"))
        	filename = "BrowserStackConfig_" + unlock.trim() + ".csv";
		//System.out.print("filename from webdriversupport: " + filename +"\n");
		ConfigArray = scenericSupport.getBrowserStackConfig(filename);
		for(int x = 0; x < ConfigArray.length; x++) {
			if(x == 0 && !ConfigArray[0].trim().equalsIgnoreCase("iphone") && !ConfigArray[0].trim().equalsIgnoreCase("android") && !ConfigArray[0].trim().equalsIgnoreCase("ipad")) {
				caps.setCapability("browser", ConfigArray[x].trim());
				//caps.setCapability("browser_api_name", "MblSafari8.0"); //app.crossbrowsertesting.com
				//caps.setCapability("os_api_name", "iPhone6Plus-iOS8sim"); //app.crossbrowsertesting.com
				if(ConfigArray[x].trim().equalsIgnoreCase("firefox")) {
					FirefoxProfile profile = new FirefoxProfile();
					File profileDir = profile.layoutOnDisk();
					profile.setAcceptUntrustedCertificates(true);
					try {
						clearCookiesURL = FirefoxDriverSupport.writeClearCookiesHTML(
		                  profileDir).getAbsolutePath();
					}
					catch (IOException e) {
						log.error("Unable to write to directory "
		                    + profileDir.getAbsolutePath());
						clearCookiesURL = "about:blank";
					}
					profile.setPreference("capability.principal.codebase.p0.id", "file://"
		                + clearCookiesURL);
					profile.setPreference("capability.principal.codebase.p0.granted",
		                "UniversalXPConnect");
					profile.setPreference("capability.principal.codebase.p0.subjectName",
		                "");
					profile.setPreference("capability.principal.codebase.p1.id", baseUrl);
					profile.setPreference("capability.principal.codebase.p1.granted",
		                "UniversalXPConnect");
					profile.setPreference("capability.principal.codebase.p1.subjectName",
		                "");
					profile.setPreference("signed.applets.codebase_principal_support", true);
					profile.setPreference("browser.download.dir",
		                profileDir.getAbsolutePath());
					profile.setPreference("browser.download.folderList", 2);
					profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
		                "text/plain,application/pdf,application/zip,text/csv,application/json");
					// native events may cause some problems - but allows for drag and drop and tabs, etc.
					profile.setEnableNativeEvents(enableNativeEvents);
					caps.setCapability(FirefoxDriver.PROFILE, profile);
				}
			}
			else if((x == 0) && ConfigArray[0].trim().equalsIgnoreCase("iphone")) {
				caps.setCapability("browserName", ConfigArray[x].trim());
				caps.setPlatform(Platform.MAC);
				//caps.setCapability("browserstack.local", "true");
			}
			else if((x == 0) && ConfigArray[0].trim().equalsIgnoreCase("android")) {
				caps.setCapability("browserName", ConfigArray[x].trim());
				caps.setPlatform(Platform.ANDROID);
				//caps.setCapability("browserstack.local", "true");
			}
			else if((x == 0) && ConfigArray[0].trim().equalsIgnoreCase("ipad")) {
				caps.setCapability("browserName", ConfigArray[x].trim());
				caps.setPlatform(Platform.MAC);
				//caps.setCapability("browserstack.local", "true");
			}
			else if((x == 1) && (!ConfigArray[0].trim().equalsIgnoreCase("iPhone") && !ConfigArray[0].trim().equalsIgnoreCase("android") && !ConfigArray[0].trim().equalsIgnoreCase("ipad"))) {
				caps.setCapability("browser_version", ConfigArray[x].trim());
			}
			else if((x == 2) && (!ConfigArray[0].trim().equalsIgnoreCase("iPhone") && !ConfigArray[0].trim().equalsIgnoreCase("android") && !ConfigArray[0].trim().equalsIgnoreCase("ipad")))
				caps.setCapability("os", ConfigArray[x].trim());
			else if((x == 3) && (!ConfigArray[0].trim().equalsIgnoreCase("iPhone") && !ConfigArray[0].trim().equalsIgnoreCase("android") && !ConfigArray[0].trim().equalsIgnoreCase("ipad")))
				caps.setCapability("os_version", ConfigArray[x].trim());
			else if((x == 4) && (!ConfigArray[0].trim().equalsIgnoreCase("iPhone") && !ConfigArray[0].trim().equalsIgnoreCase("android") && !ConfigArray[0].trim().equalsIgnoreCase("ipad")))
				caps.setCapability("resolution", ConfigArray[x].trim());
			else if((x == 5 && !ConfigArray[x].trim().equalsIgnoreCase("")) && (ConfigArray[0].trim().equalsIgnoreCase("iPhone") || ConfigArray[0].trim().equalsIgnoreCase("ipad") || ConfigArray[0].trim().equalsIgnoreCase("android")))
				caps.setCapability("device", ConfigArray[x].trim());
		}
		caps.setCapability("acceptSslCerts", "true");
	    caps.setCapability("browserstack.debug", "true");
		System.out.println("Browser Stack Caps = " + caps + "\n");
		System.out.println("Printing URL variable before initiating webdriver:  " + URL + "\n");
		try {
			return driver = new RemoteWebDriver(new URL(URL.toString()), caps);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return driver;
	}
    
    private WebDriver createWebDriver() {

        String driverName = System.getProperty("webdriver.driver");

        if (StringUtils.isNotBlank(driverName)) {
            driverName = StringUtils.trim(driverName);
        } else {
            throw new IllegalStateException("Driver not specified.");
        }

        WebDriver resultDriver = null;

        Driver driver = Driver.getValue(driverName);

        switch (driver) {

            case htmlunit:
                resultDriver = new HtmlUnitDriver(
                        BrowserVersion.INTERNET_EXPLORER_8);
                ((HtmlUnitDriver) resultDriver).setJavascriptEnabled(true);
                break;

            case firefox:
                resultDriver = createFirefoxDriver();
                break;

            case ie:
                resultDriver = new InternetExplorerDriver();
                break;

            case safari:
            	//resultDriver = new SafariDriver();
            	resultDriver = createSafariDriver();
                break;

            case chrome:
                resultDriver = new ChromeDriver();
                break;

        }

        return resultDriver;
    }

    private FirefoxDriver createFirefoxDriver() {

        FirefoxDriver resultDriver = null;

        String xvfbDisplay = System.getProperty("webdriver.xvfb.display");
        boolean enableNativeEvents = Boolean.getBoolean("webdriver.enable.native.events");

        FirefoxProfile profile = new FirefoxProfile();
        File profileDir = profile.layoutOnDisk();
        
        profile.setAcceptUntrustedCertificates(true);

        try {
            clearCookiesURL = FirefoxDriverSupport.writeClearCookiesHTML(
                    profileDir).getAbsolutePath();
        }
        catch (IOException e) {
            log.error("Unable to write to directory "
                    + profileDir.getAbsolutePath());
            clearCookiesURL = "about:blank";
        }

        profile.setPreference("capability.principal.codebase.p0.id", "file://"
                + clearCookiesURL);
        profile.setPreference("capability.principal.codebase.p0.granted",
                "UniversalXPConnect");
        profile.setPreference("capability.principal.codebase.p0.subjectName",
                "");

        profile.setPreference("capability.principal.codebase.p1.id", baseUrl);
        profile.setPreference("capability.principal.codebase.p1.granted",
                "UniversalXPConnect");
        profile.setPreference("capability.principal.codebase.p1.subjectName",
                "");

        profile.setPreference("signed.applets.codebase_principal_support", true);

        profile.setPreference("browser.download.dir",
                profileDir.getAbsolutePath());
        profile.setPreference("browser.download.folderList", 2);
        profile.setPreference("browser.helperApps.neverAsk.saveToDisk",
                "text/plain,application/pdf,application/zip,text/csv,application/json");
        //profile.setPreference("locationContextEnabled","true");
        // native events may cause some problems - but allows for drag and drop and tabs, etc.
        profile.setEnableNativeEvents(enableNativeEvents); 

        if (StringUtils.isNotBlank(xvfbDisplay)) {

            long t = System.currentTimeMillis();
            FirefoxBinary firefoxBinary = new FirefoxBinary();
            firefoxBinary.setEnvironmentProperty("DISPLAY", xvfbDisplay);
            resultDriver = new FirefoxDriver(firefoxBinary, profile);
            t = System.currentTimeMillis() - t;
            log.debug("Time to start FireFox: " + t + "ms");

        } else {

            resultDriver = new FirefoxDriver(profile);
        }

        return resultDriver;
    }

    private SafariDriver createSafariDriver() {

    	//String currentDir = System.getProperty("user.dir");
        SafariDriver resultDriver = null;
        DesiredCapabilities capabilities=DesiredCapabilities.safari();
        SafariOptions options = new SafariOptions();
        //options.addExtensions(new File(currentDir + "/src/test/resources/extension.safariextz"));
        //options.setSkipExtensionInstallation(true);
        options.setUseCleanSession(true);
        capabilities.setCapability("safariIgnoreFraudWarning", "true");
        capabilities.setCapability(SafariOptions.CAPABILITY, options);
        resultDriver = new SafariDriver(options);
        return resultDriver;
    }
    @PostConstruct
    public void initialize() {
        log.debug(String.format("Using base URL: %s", baseUrl));
        System.out.print(String.format("Using base URL: %s", baseUrl) + "\n");
    }

    public void cleanCookies() {

        if (WebDriverSupport.webDriver != null) {

            // deletes cookies
            if (WebDriverSupport.webDriver instanceof FirefoxDriver) {
                webDriver.get("file://" + clearCookiesURL);
            } else {
            	try {
            		webDriver.manage().deleteAllCookies();
            	}
            	catch(Exception e) {}
            }

        }

    }

    @PreDestroy
    public void cleanFullWhenDone() {
        if (WebDriverSupport.webDriver != null) {

            new Timer(true).schedule(new TimerTask() {
                private long timerStartTime = System.currentTimeMillis();
                private WebDriver myWebDriver = WebDriverSupport.webDriver;

                public void run() {
                    log.debug("lastGetDriverRequestTime="
                            + lastGetDriverRequestTime + ",timerStartTime="
                            + timerStartTime);

                    // quit browser if necessary
                    //if (lastGetDriverRequestTime <= timerStartTime
                            //&& lastGetDriverRequestTime != 0) {
                        lastGetDriverRequestTime = 0; // just in case
                        cleanFull(myWebDriver);
                        log.debug("WebDriver browser is no longer in use.  Quitting browser.");
                        //System.out.print("WebDriver browser is no longer in use.  Quitting browser.\n");
                    //}
                }
            }, 6000); // run() after 6 seconds

        }

    }

    /**
     * Closes and quits the current browser, only if it matches the current
     * browser. Does not create a new webdriver.
     */
    public static void cleanFull() {
        cleanFull(WebDriverSupport.webDriver);
    }

    /**
     * Closes and quits the browser provided, only if it matches the current
     * browser. Does not create a new webdriver.
     * 
     * @param myWebDriver The webdriver instance to clean up if it is still
     *        active.
     */
    public static void cleanFull(WebDriver myWebDriver) {
        synchronized (WebDriverSupport.class) {
            if (WebDriverSupport.webDriver != null
                    && WebDriverSupport.webDriver == myWebDriver) {
                lastGetDriverRequestTime = 0; // just in case
                System.out.print("Closing and quiting webdriver\n");
                webDriver.close();
                webDriver.quit();
                WebDriverSupport.webDriver = null;
            }
        }
    }

    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                cleanFull();
            }
        });
    }

    public enum Driver {

        htmlunit, firefox, ie, safari, chrome;

        public static Driver getValue(String name) {

            Driver result = null;

            try {

                result = Driver.valueOf(name.toLowerCase());

            }
            catch (IllegalArgumentException iae) {

                throw new IllegalArgumentException(String.format(
                        "Driver '%s' is invalid. It must be one of: %s", name,
                        Arrays.asList(values())));
            }

            return result;
        }
    }
}
