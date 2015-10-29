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

public class Phone {
    @Resource
    private PlacesterSupport placesterSupport;
    @Resource
    String environment;
    @Resource
    private String baseUrl;
    
    @Given("^I create account phone by (.*) for account_id (.*) with account phone data (.*) and validate response code (.*)$")
    public void createAccountPhone(String phone_type, String account_id, String account_phone_data, String exp_response_code) {
        StringBuffer response = new StringBuffer();
        int responseCode = 0, counter = 0;
        String group_id = "", label = "", phone = "", primary_phone = "", create_account_phone_payload = "";
        if(!"".equals(Account.accountID.toString())) {
            account_id = Account.accountID.toString(); 
        }
        if(!"".equals(Group.groupID.toString())) {
            group_id = Group.groupID.toString(); 
        }
        String[] phone_parts = account_phone_data.trim().split(",");
        for(int x = 0; x < phone_parts.length; x++) {
            if(x == 0) {
                label = phone_parts[x];
            }
            else if(x == 1) {
                phone = phone_parts[x];
            }
            else if(x == 2) {
                primary_phone = phone_parts[x];
            }
        }
        try {
            URL url = new URL(baseUrl + "/accounts/v1.5/phone/create");
            String line = "";
            String authString = Account.username + ":" + Account.pwd;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            if(phone_type.equalsIgnoreCase("group_id")) {
                create_account_phone_payload = "{\"phone\": \"" + phone + "\","
                + "\"group_id\": \"" + group_id + "\","
                + "\"label\": \"" + label + "\","
                + "\"primary_phone\": " + primary_phone + "}";
            }
            else if(phone_type.equalsIgnoreCase("account_id")) {
                create_account_phone_payload = "{\"phone\": \"" + phone + "\","
                + "\"account_id\": \"" + account_id + "\","
                + "\"label\": \"" + label + "\","
                + "\"primary_phone\": " + primary_phone + "}";
            }
            System.out.print("Create Account Phone Payload: " + create_account_phone_payload +"\n");
            System.out.println("\nSending 'POST' request to URL : " + url + " with pay load: " + create_account_phone_payload + "\n");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(create_account_phone_payload);
            writer.close();
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Response Code : " + responseCode);
            System.out.println("Expected Response Code : " + exp_response_code);
            if(!exp_response_code.equalsIgnoreCase(Integer.toString(responseCode)) && responseCode != 409) {
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for create account phone test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
                if(phone_type.equalsIgnoreCase("group_id")) {
                    if(line.contains(group_id) && line.contains(phone) && line.contains(label) && line.contains(primary_phone)) {
                        counter++;
                    }
                }
                else if(phone_type.equalsIgnoreCase("account_id")) {
                    if(line.contains(account_id) && line.contains(phone) && line.contains(label) && line.contains(primary_phone)) {
                        counter++;
                    }
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for create account phone test case. Expected that string: " + account_id + " " + " " + group_id + " " + phone + " " + label + " " + primary_phone + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
        //Capture account phone id from the positive response
        if(responseCode == 200) {
            AccountType.ID.replace(0, AccountType.ID.length(), response.toString().trim().substring(7, 43));
            System.out.print("Captured id: " + AccountType.ID.toString() + "\n");
        }
    }
    @Given("^I update account phone for account_id (.*) with account phone data (.*) and validate response code (.*)$")
    public void updateAccountPhone(String account_id, String account_phone_data, String exp_response_code) {
        StringBuffer response = new StringBuffer();
        int responseCode = 0, counter = 0;
        String label = "", phone = "", primary_phone = "", update_account_phone_payload = "", id = "", group_id = "";
        if(!"".equals(Account.accountID.toString())) {
            account_id = Account.accountID.toString(); 
        }
        if(!"".equals(AccountType.ID.toString())) {
            id = AccountType.ID.toString(); 
        }
        if(!"".equals(Group.groupID.toString())) {
            group_id = Group.groupID.toString(); 
        }
        String[] phone_parts = account_phone_data.trim().split(",");
        for(int x = 0; x < phone_parts.length; x++) {
            if(x == 0) {
                label = phone_parts[x].trim();
            }
            else if(x == 1) {
                phone = phone_parts[x].trim();
            }
            else if(x == 2) {
                primary_phone = phone_parts[x].trim();
            }
        }
        try {
            URL url = new URL(baseUrl + "/accounts/v1.5/phone/update");
            String line = "";
            String authString = Account.username + ":" + Account.pwd;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            update_account_phone_payload = "{\"account_id\": \"" + account_id + "\","
            + "\"phone\": \"" + phone + "\","
            + "\"id\": \"" + id + "\","
            + "\"group_id\": \"" + group_id + "\","
            + "\"label\": \"" + label + "\","
            + "\"primary_phone\": " + primary_phone + "}";
            System.out.print("Update Account Phone Payload: " + update_account_phone_payload +"\n");
            System.out.println("\nSending 'POST' request to URL : " + url + " with pay load: " + update_account_phone_payload + "\n");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(update_account_phone_payload);
            writer.close();
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Response Code : " + responseCode);
            System.out.println("Expected Response Code : " + exp_response_code);
            if(!exp_response_code.equalsIgnoreCase(Integer.toString(responseCode)) && responseCode != 409) {
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for update account phone test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
                if(line.contains(account_id) && line.contains(id) && line.contains(phone) && line.contains(label) && line.contains(primary_phone)) {
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for update account phone test case. Expected that string: " + account_id + " " + " " + id + " " + phone + " " + label + " " + primary_phone + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
    @Given("^I read account phone for account_id (.*) and validate response code (.*) and response text (.*)$")
    public void readGroup(String account_id, String exp_response_code, String exp_response_text) {
        URL url = null;
        int counter = 0;
        StringBuffer response = null;
        String id = "", label = "", phone = "", primary_phone = "";
        if(!"".equals(AccountType.ID.toString())) {
            id = AccountType.ID.toString(); 
        }
        String[] phone_parts = exp_response_text.trim().split(",");
        for(int x = 0; x < phone_parts.length; x++) {
            if(x == 0) {
                label = phone_parts[x];
            }
            else if(x == 1) {
                phone = phone_parts[x];
            }
            else if(x == 2) {
                primary_phone = phone_parts[x];
            }
        }
        try {
            url = new URL(baseUrl + "/accounts/v1.5/phone/read?id[]=" + id);
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
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for read account phone test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
                if(line.contains(label) && line.contains(account_id) && line.contains(phone) && line.contains(primary_phone)) {
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for read account phone test case. Expected that string: " + exp_response_text + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
    @Given("^I delete account_phone and validate response code (.*) and response text is blank$")
    public void deleteAccountPhone(String exp_response_code) {
        URL url = null;
        int counter = 0;
        StringBuffer response = null;
        String id = "";
        if(!"".equals(AccountType.ID.toString())) {
            id = AccountType.ID.toString(); 
        }
        try {
            url = new URL(baseUrl + "/accounts/v1.5/phone/delete?id[]=" + id);
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
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for delete account phone with id: " + id + ". Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for delete account phone test case. Expected response: No content, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
}
