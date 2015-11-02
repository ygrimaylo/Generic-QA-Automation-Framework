package com.placester.web.steps;

import javax.annotation.Resource;
import cucumber.annotation.en.Given;

public class PlacesterWeb {
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
    public static StringBuffer httpBasicAuthenticationUserName = new StringBuffer("staging");
    public static StringBuffer httpBasicAuthenticationPassword = new StringBuffer("welcome");
    public static StringBuffer skipStep = new StringBuffer("");
    
    @Given("^I login to placester web site by using username (.*) and password (.*)$")
    public void iLoginToPlacesterWebSite(String username, String password) {
        int timeout = 10;
        //Enter username and password
        placesterSupport.clickElement("#email", "by css", timeout);
        placesterSupport.sendKeysElement("#email", username, "by css", timeout);
        placesterSupport.clickElement("#password", "by css", timeout);
        placesterSupport.sendKeysElement("#password", password, "by css", timeout);
        //Click Login button
        placesterSupport.clickElement("//div[@id='main-content']/div/div/div/form/div/div[2]/input", "by xpath", timeout);
        //Verify Placester Site page
        placesterSupport.VerifyTrue("//div/div/div/p/a", "by xpath", "Verifying Placester Site label", ".*Site", "yes");
        placesterSupport.VerifyEquals("h1.bd-n.mb0 > span", "by css", "Verifying Sites +", "Sites +");
        placesterSupport.VerifyEquals("Placester", "by linkText", "Verifying Placester link", "Placester");
        placesterSupport.VerifyTrue("li.ar-dw", "by css", "Verifying Listings:", "Listings.*", "yes");
        placesterSupport.VerifyTrue("//div[@id='website_table']/div/table/tbody/tr/td/div/ul/li[3]", "by xpath", "Verifying Posts:", "Posts.*", "yes");
        placesterSupport.VerifyTrue("li.ar-up", "by css", "Verifying Pages:", "Pages.*", "yes");
    }
    @Given("^I click link (.*)$")
    public void iLoginToPlacesterWebSite(String link) {
        int timeout = 10;
        placesterSupport.clickElement(link.trim(), "by linkText", timeout);
    }   
    @Given("^I access my live site (.*)$")
    public void iVerifySite(String site) {
        int timeout = 10;
        String ConfigArray[] = null;
        String environment_global = environment;
        String brs = System.getProperty("webdriver.driver"), filename = "";
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
        //Get server name
        String[] url_parts = this.baseUrl.trim().split("\\.");
        String server = url_parts[1];
        placesterSupport.getURL("http://"+ httpBasicAuthenticationUserName + ":" + httpBasicAuthenticationPassword + "@site2477." + server + ".org/", timeout);
        placesterSupport.VerifyEquals(site.trim(), "by linkText", "Verifying my site", site.trim());
        //Navigate back
        if(!brs.equalsIgnoreCase("chrome") && !brs.equalsIgnoreCase("safari"))
            webDriverSupport.getWebDriver().navigate().back();
        else {
            skipStep.replace(0, skipStep.length(), "0");
        }
    }
    @Given("^I logout from Placester web site$")
    public void iLogoutFromPlacesterWebSite() {
        int timeout = 10;
        String logout_url = "";
        String ConfigArray[] = null;
        String environment_global = environment;
        String brs = System.getProperty("webdriver.driver"), filename = "";
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
        if (!"".equals(skipStep.toString())) {
            return;
        }
        //Build logout url for safari logout
        String[] url_parts = this.baseUrl.trim().split("#");
        logout_url = url_parts[0] + "#logout";
        if(brs.equalsIgnoreCase("safari"))
            placesterSupport.getURL(logout_url, timeout);
        else {
            if(placesterSupport.isElementDisplayed("span.uni-bar-name", "by css", timeout)) {
                placesterSupport.clickElement("span.uni-bar-name", "by css", timeout);
                placesterSupport.clickElement("#logout", "by css", timeout);
            }
        }
        //Verify login page
        placesterSupport.VerifyEquals("p.mb10", "by css", "Verifying Placester Login page", "Log in to your Placester account");
    }
}
