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

public class Permission {
    @Resource
    private PlacesterSupport placesterSupport;
    @Resource
    String environment;
    @Resource
    private String baseUrl;
    
    @Given("^I create account permission for account_id (.*) with permission_value (.*) and validate response code (.*)$")
    public void createAccountPermission(String account_id, String permission_value, String exp_response_code) {
        StringBuffer response = new StringBuffer();
        int responseCode = 0, counter = 0;
        String group_id = "";
        if(!"".equals(Account.accountID.toString())) {
            account_id = Account.accountID.toString(); 
        }
        if(!"".equals(Group.groupID.toString())) {
            group_id = Group.groupID.toString(); 
        }
        if(baseUrl.contains("8082")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4) + "8081";
        }
        try {
            URL url = new URL(baseUrl + "/accounts/v1.5/permission/create");
            String line = "";
            String authString = Account.username + ":" + Account.pwd;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            String create_account_permission_payload = "{\"account_id\": \"" + account_id + "\","
            + "\"group_id\": \"" + group_id + "\","
            + "\"permission\": " + permission_value + "}";
            System.out.print("Create Account Permission Payload: " + create_account_permission_payload +"\n");
            System.out.println("\nSending 'POST' request to URL : " + url + " with pay load: " + create_account_permission_payload + "\n");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(create_account_permission_payload);
            writer.close();
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Response Code : " + responseCode);
            System.out.println("Expected Response Code : " + exp_response_code);
            if(!exp_response_code.equalsIgnoreCase(Integer.toString(responseCode)) && responseCode != 409) {
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for create account permission test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
            }
            BufferedReader br;
            if(responseCode != 200) {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            else {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            while ((line = br.readLine()) != null) {
                response.append(line);
                if(line.contains(group_id) && line.contains(account_id) && line.contains(permission_value)) {
                    counter++;
                }
            }
            br.close();
            connection.disconnect();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        System.out.print("Captured response: " + response.toString() + "\n");
        if(counter == 0) {
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for create account permission test case. Expected that string: " + account_id + " " + " " + group_id + " " + permission_value + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
        //Capture account permission id from the positive response
        if(responseCode == 200) {
            AccountType.ID.replace(0, AccountType.ID.length(), response.toString().trim().substring(7, 43));
            System.out.print("Captured id: " + AccountType.ID.toString() + "\n");
        }
    }
    @Given("^I update account permission for account_id (.*) with permission_value (.*) and validate response code (.*)$")
    public void updateAccountPermission(String account_id, String permission_value, String exp_response_code) {
        StringBuffer response = new StringBuffer();
        int responseCode = 0, counter = 0;
        String group_id = "", id = "";
        if(!"".equals(Account.accountID.toString())) {
            account_id = Account.accountID.toString(); 
        }
        if(!"".equals(AccountType.ID.toString())) {
            id = AccountType.ID.toString(); 
        }
        if(!"".equals(Group.groupID.toString())) {
            group_id = Group.groupID.toString(); 
        }
        if(baseUrl.contains("8082")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4) + "8081";
        }
        try {
            URL url = new URL(baseUrl + "/accounts/v1.5/permission/update");
            String line = "";
            String authString = Account.username + ":" + Account.pwd;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            String update_account_permission_payload = "{\"account_id\": \"" + account_id + "\","
            + "\"group_id\": \"" + group_id + "\","
            + "\"id\": \"" + id + "\","
            + "\"permission\": " + permission_value + "}";
            System.out.print("Update Account Permission Payload: " + update_account_permission_payload +"\n");
            System.out.println("\nSending 'POST' request to URL : " + url + " with pay load: " + update_account_permission_payload + "\n");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(update_account_permission_payload);
            writer.close();
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Response Code : " + responseCode);
            System.out.println("Expected Response Code : " + exp_response_code);
            if(!exp_response_code.equalsIgnoreCase(Integer.toString(responseCode)) && responseCode != 409) {
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for update account permission test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
            }
            BufferedReader br;
            if(responseCode != 200) {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            else {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            while ((line = br.readLine()) != null) {
                response.append(line);
                if(line.contains(group_id) && line.contains(account_id) && line.contains(permission_value) && line.contains(id)) {
                    counter++;
                }
            }
            br.close();
            connection.disconnect();
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        System.out.print("Captured response: " + response.toString() + "\n");
        if(counter == 0) {
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for update account permission test case. Expected that string: " + account_id + " " + " " + group_id + " " + permission_value + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
    @Given("^I read account permission by (.*) for account_id (.*) and validate response code (.*) and response text (.*)$")
    public void readGroup(String read_type, String account_id, String exp_response_code, String exp_response_text) {
        URL url = null;
        int counter = 0;
        StringBuffer response = null;
        String id = "", group_id = "";
        if(!"".equals(Account.accountID.toString())) {
            account_id = Account.accountID.toString(); 
        }
        if(!"".equals(AccountType.ID.toString())) {
            id = AccountType.ID.toString(); 
        }
        if(!"".equals(Group.groupID.toString())) {
            group_id = Group.groupID.toString(); 
        }
        if(baseUrl.contains("8082")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4) + "8081";
        }
        try {
            if(read_type.equalsIgnoreCase("account_id")) {
                url = new URL(baseUrl + "/accounts/v1.5/permission/read?account_id=" + account_id);
            }
            else if(read_type.equalsIgnoreCase("id")) {
                url = new URL(baseUrl + "/accounts/v1.5/permission/read?id[]=" + id);
            }
            String authString = Account.username + ":" + Account.pwd;
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
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for read account permission by " + read_type + " test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
            String line;
            response = new StringBuffer();
            while ((line = in.readLine()) != null) {
                response.append(line);
                if(read_type.equalsIgnoreCase("id") || read_type.equalsIgnoreCase("account_id")) {
                    if(line.contains(group_id) && line.contains(account_id) && line.contains(id) && line.contains(exp_response_text)) {
                        counter++;
                    }
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for read account permission by " + read_type + " test case. Expected that string: " + exp_response_text + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
    @Given("^I delete account_permission and validate response code (.*) and response text is blank$")
    public void deleteAccountPermission(String exp_response_code) {
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
            url = new URL(baseUrl + "/accounts/v1.5/permission/delete?id[]=" + id);
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
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for delete account permission with id: " + id + ". Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for delete account permission test case. Expected response: No content, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
}
