package com.placester.services.steps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.annotation.Resource;
import junit.framework.Assert;
import org.apache.commons.codec.binary.Base64;
import com.placester.web.steps.PlacesterSupport;
import cucumber.annotation.After;
import cucumber.annotation.en.Given;


public class Account {
    @Resource
    private PlacesterSupport placesterSupport;
    @Resource
    String environment;
    @Resource
    private String baseUrl;
    public static StringBuffer username = new StringBuffer("staging");
    public static StringBuffer pwd = new StringBuffer("welcome");
    public static StringBuffer accountPassword = new StringBuffer("test123");
    public static StringBuffer accountID = new StringBuffer("");
    
    @Given("^I create account (.*) and validate response code (.*) and capture account_id$")
    public void createAccount(String account_email, String exp_response_code) {
        StringBuffer response = new StringBuffer();
        int responseCode = 0, counter = 0;
        String exp_response_text = "That email already exists";
        if(baseUrl.contains("8082")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4) + "8081";
        }
        try {
            URL url = new URL(baseUrl + "/accounts/v1.5/account/create");
            String authString = username + ":" + pwd;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            String create_account_payload = "{\"crm_id\": \"123\",\"first_name\": \"TestName\",\"account_type\" : [{\"account_type\" : \"user\"},{\"account_type\" : \"agent\"}],"
            + "\"phone\": [{\"phone\": \"555-555-5555\",\"primary_phone\": true,\"label\": \"cell\"}],\"address\": [{\"region\": \"MA\",\"primary_address\": true,\"postal_code\": \"02142\",\"locality\": \"Boston\",\"address1\": \"123 street\",\"address2\": \"unit 1\",\"country_code\": \"US\"}]," 
            + "\"profile\": [{\"phone\": [{\"phone\": \"555-555-5555\",\"primary_phone\": true,\"label\": \"cell\"}],\"address\": [{\"region\": \"MA\",\"primary_address\": true,\"postal_code\": \"02142\",\"locality\": \"Boston\",\"address1\": \"1111 street\",\"address2\": \"unit 3\",\"country_code\": \"US\"}],"
            + "\"email\": \"" + account_email + "\",\"password\": \"" + accountPassword.toString() + "\",\"primary_profile\": true,\"media\": [{\"media_type\": \"logo\",\"media_url\": \"http:\\/\\/www.yahoo.com\"}],\"meta\": [{\"meta_key\": \"zoom\",\"meta_value\": \"zeek\"}]}],"
            + "\"permissions\":[{\"group_id\":\"55a2d8a8-e4c3-492e-b865-112675fd987b\",\"permission\":7}],\"last_name\": \"LastName\",\"media\": [{\"media_type\": \"logo\",\"media_url\": \"http:\\/\\/www.google.com\"}],\"meta\": [{\"meta_key\": \"moo\",\"meta_value\": \"meow\"}]}";
            System.out.print("Create Account Payload: " + create_account_payload +"\n");
            System.out.println("\nSending 'POST' request to URL : " + url + " with pay load: " + create_account_payload + "\n");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(create_account_payload);
            writer.close();
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Response Code : " + responseCode);
            System.out.println("Expected Response Code : " + exp_response_code + " or 409");
            if(!exp_response_code.equalsIgnoreCase(Integer.toString(responseCode)) && responseCode != 409) {
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for create account test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
            }
            BufferedReader in = null;
            if(responseCode == 200) {
                in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            }
            else {
                in = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                if(inputLine.contains(account_email) || inputLine.contains(exp_response_text)) {
                    counter++;
                }
            }
            in.close();
            connection.disconnect();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        System.out.print("Captured response: " + response.toString() + "\n");
        if(counter == 0) {
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for create account test case. Expected that string: " + exp_response_text + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
        //Capture account id from the positive response
        if(responseCode == 200) {
            accountID.replace(0, accountID.length(), response.toString().trim().substring(119, 155));
            System.out.print("Captured account_id: " + accountID.toString() + "\n");
        }
    }
    @Given("^I update account (.*) and validate response code (.*) and response text (.*)$")
    public void updateAccount(String account_id, String exp_response_code, String exp_response_text) {
        StringBuffer response = null;
        int responseCode = 0, counter = 0;
        if(baseUrl.contains("8082")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4) + "8081";
        }
        if(!"".equals(accountID.toString())) {
            account_id = accountID.toString(); 
        }
        try {
            URL url = new URL(baseUrl + "/accounts/v1.5/account/update");
            String authString = username + ":" + pwd;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            String update_account_payload = "{\"id\": \"" + account_id + "\","
            + "\"crm_id\": \"123\"," 
            + "\"first_name\": \"TestName_updated\","
            + "\"last_name\": \"LastName\","
            + "\"deleted\" : false}";
            System.out.print("Update Account Payload: " + update_account_payload +"\n");
            System.out.println("\nSending 'POST' request to URL : " + url + " with pay load: " + update_account_payload + "\n");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(update_account_payload);
            writer.close();
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Response Code : " + responseCode);
            System.out.println("Expected Response Code : " + exp_response_code);
            if(!exp_response_code.equalsIgnoreCase(Integer.toString(responseCode)) && responseCode != 409) {
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for update account test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
            }
            BufferedReader in = null;
            if(responseCode == 200) {
                in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            }
            else {
                in = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                if(inputLine.contains(exp_response_text) && inputLine.contains(account_id)) {
                    counter++;
                }
            }
            in.close();
            connection.disconnect();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        System.out.print("Captured response: " + response.toString() + "\n");
        if(counter == 0) {
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for update account test case. Expected that string: " + exp_response_text + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
    @Given("^I read account by (.*) with account_id (.*) and validate response code (.*) and response text (.*)$")
    public void readAccount(String read_type, String account_id, String exp_response_code, String exp_response_text) {
        URL url = null;
        int counter = 0;
        StringBuffer response = null;
        if(!"".equals(accountID.toString())) {
            account_id = accountID.toString(); 
        }
        if(baseUrl.contains("8082")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4) + "8081";
        }
        try {
            if(read_type.equalsIgnoreCase("account id")) {
                url = new URL(baseUrl + "/accounts/v1.5/account/read?id[]=" + account_id);
            }
            else if(read_type.equalsIgnoreCase("email")) {
                url = new URL(baseUrl + "/accounts/v1.5/account/read?email=" + exp_response_text + "&password=" + accountPassword.toString());
            }
            String authString = username + ":" + pwd;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            System.out.println("\nSending 'GET' request to URL : " + url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestMethod("GET");
            int responseCode = 0;
            responseCode = connection.getResponseCode();
            System.out.println("Response Code : " + responseCode);
            System.out.println("Expected Response Code : " + exp_response_code);
            if(!exp_response_code.equalsIgnoreCase(Integer.toString(responseCode))) {
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for read account by " + read_type + " test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
            }
            BufferedReader in = null;
            if(responseCode == 200) {
                in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            }
            else {
                in = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                if(inputLine.contains(exp_response_text)) {
                    counter++;
                }
            }
            in.close();
            connection.disconnect();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        System.out.println("Response is: " + response.toString());
        if(counter == 0) {
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for read account by " + read_type + " test case. Expected that string: " + exp_response_text + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
    @Given("^I delete account with account_id (.*) and validate response code (.*) and response text is blank$")
    public void deleteAccount(String account_id, String exp_response_code) {
        URL url = null;
        int counter = 0;
        StringBuffer response = null;
        if(!"".equals(accountID.toString())) {
            account_id = accountID.toString(); 
        }
        if(baseUrl.contains("8082")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4) + "8081";
        }
        try {
            url = new URL(baseUrl + "/accounts/v1.5/account/delete?id[]=" + account_id);
            String authString = username + ":" + pwd;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            HttpURLConnection connection = null;
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestMethod("GET");
            int responseCode = 0;
            responseCode = connection.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            System.out.println("Expected Response Code : " + exp_response_code);
            if(!exp_response_code.equalsIgnoreCase(Integer.toString(responseCode))) {
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for delete account with account_id: " + account_id + ". Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
            }
            BufferedReader in = null;
            in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
            String inputLine;
            response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                if(inputLine.contains("")) {
                    counter++;
                }
            }
            in.close();
            connection.disconnect();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        System.out.println("Response is: " + response.toString());
        if(counter != 0) {
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for delete account test case. Expected response: No content, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
    @After
    public void cleanup() {
        String issues = "";
        if (!"".equals(PlacesterSupport.verificationErrors.toString())) {
            issues = PlacesterSupport.verificationErrors.toString();
            PlacesterSupport.verificationErrors.replace(0, PlacesterSupport.verificationErrors.length(), "");
            Assert.fail(issues);
        }
    }
}
