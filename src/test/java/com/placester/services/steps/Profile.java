package com.placester.services.steps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;

import com.placester.web.steps.PlacesterSupport;

import cucumber.annotation.en.Given;

public class Profile {
    @Resource
    private PlacesterSupport placesterSupport;
    @Resource
    String environment;
    @Resource
    private String baseUrl;
    
    @Given("^I create account profile for account_email (.*) and account_id (.*) with account address data (.*) and validate response code (.*)$")
    public void createAccountProfile(String account_email, String account_id, String account_profile_data, String exp_response_code) {
        StringBuffer response = new StringBuffer();
        String id = "", group_id = "", region = "", primary_address = "", postal_code = "", locality = "", address1 = "", address2 = "", country_code = "", phone = "";
        int responseCode = 0, counter = 0;
        if(baseUrl.contains("8082")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4) + "8081";
        }
        if(!"".equals(Account.accountID.toString())) {
            account_id = Account.accountID.toString(); 
        }
        if(!"".equals(Group.groupID.toString())) {
            group_id = Group.groupID.toString(); 
        }
        String[] profile_parts = account_profile_data.trim().split(",");
        for(int x = 0; x < profile_parts.length; x++) {
            if(x == 0) {
                region = profile_parts[x];
            }
            else if(x == 1) {
                primary_address = profile_parts[x];
            }
            else if(x == 2) {
                postal_code = profile_parts[x];
            }
            else if(x == 3) {
                locality = profile_parts[x];
            }
            else if(x == 4) {
                address1 = profile_parts[x];
            }
            else if(x == 5) {
                address2 = profile_parts[x];
            }
            else if(x == 6) {
                country_code = profile_parts[x];
            }
            else if(x == 7) {
                phone = profile_parts[x];
            }
        }
        try {
            URL url = new URL(baseUrl + "/accounts/v1.5/profile/create");
            String line = "";
            String authString = Account.username + ":" + Account.pwd;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            String create_account_profile_payload = "{\"account_id\": \"" + account_id + "\","
            + "\"group_id\": \"" + group_id + "\","
            + "\"email\": \"" + account_email + "\"," 
            + "\"primary_profile\": " + primary_address + ","
            + "\"password\": \"" + Account.accountPassword.toString() + "\","
            + "\"phone\": [{\"phone\": \"" + phone + "\",\"primary_phone\": " + primary_address + ",\"label\": \"cell\"}],"
            + "\"address\": [{\"region\": \"" + region + "\",\"primary_address\": " + primary_address + ",\"postal_code\": \"" + postal_code + "\",\"locality\": \"" + locality + "\",\"address1\": \"" + address1 + "\",\"address2\": \"" + address2 + "\",\"country_code\": \"" + country_code + "\"}],"
            + "\"media\": [{\"media_type\": \"logo\",\"media_url\": \"http:\\/\\/www.yahoo.com\"}],"
            + "\"meta\": [{\"meta_key\": \"zoom\",\"meta_value\": \"zeek\"}]}";
            System.out.print("Create Account Profile Payload: " + create_account_profile_payload +"\n");
            System.out.println("\nSending 'POST' request to URL : " + url + " with pay load: " + create_account_profile_payload + "\n");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(create_account_profile_payload);
            writer.close();
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Response Code : " + responseCode);
            System.out.println("Expected Response Code : " + exp_response_code);
            if(!exp_response_code.equalsIgnoreCase(Integer.toString(responseCode)) && responseCode != 409) {
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for create account profile test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
            response = new StringBuffer();
            while ((line = in.readLine()) != null) {
                response.append(line);
                if(line.contains(phone) && line.contains(region) && line.contains(account_id) && line.contains(primary_address) && line.contains(postal_code) && line.contains(locality) && line.contains(address1) && line.contains(address2) && line.contains(country_code)) {
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for create account profile test case. Expected that string: " + account_profile_data + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
        //Capture account profile id from the positive response
        if(responseCode == 200) {
            String[] response_parts = response.toString().trim().split(":");
            for(int x = 0; x < response_parts.length; x++) {
                if(x == 4) {
                    System.out.print("response_parts[" + x + "]=" + response_parts[x] + "\n");
                    id = response_parts[x].substring(1, 37);
                    AccountType.ID.replace(0, AccountType.ID.length(), id);
                }
            }
            System.out.print("Captured account profile id: " + AccountType.ID.toString() + "\n");
        }
    }
    @Given("^I update account profile for account_email (.*) with account_id (.*) and validate response code (.*)$")
    public void updateAccountProfile(String account_email, String account_id, String exp_response_code) {
        StringBuffer response = new StringBuffer();
        String id = "", group_id = "";
        int responseCode = 0, counter = 0;
        if(baseUrl.contains("8082")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4) + "8081";
        }
        if(!"".equals(AccountType.ID.toString())) {
            id = AccountType.ID.toString(); 
        }
        if(!"".equals(Account.accountID.toString())) {
            account_id = Account.accountID.toString(); 
        }
        if(!"".equals(Group.groupID.toString())) {
            group_id = Group.groupID.toString(); 
        }
        try {
            URL url = new URL(baseUrl + "/accounts/v1.5/address/update");
            String line = "";
            String authString = Account.username + ":" + Account.pwd;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            String update_account_profile_payload = "{\"id\": \"" + id + "\","
            + "\"account_id\": \"" + account_id + "\"," 
            + "\"group_id\": \"" + group_id + "\","
            + "\"email\": \"" + account_email + "\","
            + "\"primary_profile\" : false,"
            + "\"password\": \"" + Account.accountPassword.toString() + "\"}";
            System.out.print("Update Account Address Payload: " + update_account_profile_payload +"\n");
            System.out.println("\nSending 'POST' request to URL : " + url + " with pay load: " + update_account_profile_payload + "\n");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(update_account_profile_payload);
            writer.close();
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Response Code : " + responseCode);
            System.out.println("Expected Response Code : " + exp_response_code);
            if(!exp_response_code.equalsIgnoreCase(Integer.toString(responseCode)) && responseCode != 409) {
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for update account profile test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
            response = new StringBuffer();
            while ((line = in.readLine()) != null) {
                response.append(line);
                if(line.contains(id) || line.contains(account_id) || line.contains(account_email) || line.contains(": \"")) {
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for update account profile test case. Expected that string: " + id + " " + account_id + " " + account_email + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
    @Given("^I read account profile and validate response code (.*) and response text (.*)$")
    public void readAccountProfile(String exp_response_code, String exp_response_text) {
        URL url = null;
        int counter = 0, responseCode = 0;
        StringBuffer response = null;
        String phone = "", id = "", region = "", primary_address = "", postal_code = "", locality = "", address1 = "", address2 = "", country_code = "";
        if(baseUrl.contains("8082")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4) + "8081";
        }
        if(!"".equals(AccountType.ID.toString())) {
            id = AccountType.ID.toString(); 
        }
        String[] profile_parts = exp_response_text.trim().split(",");
        for(int x = 0; x < profile_parts.length; x++) {
            if(x == 0) {
                region = profile_parts[x];
            }
            else if(x == 1) {
                primary_address = profile_parts[x];
            }
            else if(x == 2) {
                postal_code = profile_parts[x];
            }
            else if(x == 3) {
                locality = profile_parts[x];
            }
            else if(x == 4) {
                address1 = profile_parts[x];
            }
            else if(x == 5) {
                address2 = profile_parts[x];
            }
            else if(x == 6) {
                country_code = profile_parts[x];
            }
            else if(x == 7) {
                phone = profile_parts[x];
            }
        }
        try {
           
            url = new URL(baseUrl + "/accounts/v1.5/profile/read?id[]=" + AccountType.ID);
            String authString = Account.username + ":" + Account.pwd;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            HttpURLConnection connection = null;
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestMethod("GET");
            responseCode = connection.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);
            System.out.println("Expected Response Code : " + exp_response_code);
            if(!exp_response_code.equalsIgnoreCase(Integer.toString(responseCode))) {
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for read account profile test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
                if(inputLine.contains(phone) && inputLine.contains(region) && inputLine.contains(id) && inputLine.contains(primary_address) && inputLine.contains(postal_code) && inputLine.contains(locality) && inputLine.contains(address1) && inputLine.contains(address2) && inputLine.contains(country_code)) {
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for read account profile test case. Expected that string: " + exp_response_text + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
    @Given("^I delete account_profile and validate response code (.*) and response text is blank$")
    public void deleteAccountProfile(String exp_response_code) {
        URL url = null;
        int counter = 0;
        StringBuffer response = null;
        String id = "";
        if(!"".equals(AccountType.ID.toString())) {
            id = AccountType.ID.toString(); 
        }
        if(baseUrl.contains("8082")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4) + "8081";
        }
        try {
            url = new URL(baseUrl + "/accounts/v1.5/profile/delete?id[]=" + id);
            String authString = Account.username + ":" + Account.pwd;
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
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for delete account profile with id: " + id + ". Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for delete account profile test case. Expected response: No content, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
}
