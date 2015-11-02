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

public class Group {
    @Resource
    private PlacesterSupport placesterSupport;
    @Resource
    String environment;
    @Resource
    private String baseUrl;
    public static StringBuffer groupID = new StringBuffer("");
    
    @Given("^I create account group for account_email (.*) with account address data (.*) and validate response code (.*)$")
    public void createAccountGroup(String account_email, String account_group_data, String exp_response_code) {
        StringBuffer response = new StringBuffer();
        String group_type = "", group_name = "", region = "", primary_address = "", postal_code = "", locality = "", address1 = "", address2 = "", country_code = "", phone = "";
        int responseCode = 0, counter = 0;
        String[] group_parts = account_group_data.trim().split(",");
        if(baseUrl.contains("8082")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4) + "8081";
        }
        for(int x = 0; x < group_parts.length; x++) {
            if(x == 0) {
                region = group_parts[x];
            }
            else if(x == 1) {
                primary_address = group_parts[x];
            }
            else if(x == 2) {
                postal_code = group_parts[x];
            }
            else if(x == 3) {
                locality = group_parts[x];
            }
            else if(x == 4) {
                address1 = group_parts[x];
            }
            else if(x == 5) {
                address2 = group_parts[x];
            }
            else if(x == 6) {
                country_code = group_parts[x];
            }
            else if(x == 7) {
                phone = group_parts[x];
            }
            else if(x == 8) {
                group_name = group_parts[x] + System.currentTimeMillis();
            }
            else if(x == 9) {
                group_type = group_parts[x];
            }
        }
        try {
            URL url = new URL(baseUrl + "/accounts/v1.5/group/create");
            String line = "";
            String authString = Account.username + ":" + Account.pwd;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            String create_account_group_payload = "{\"group_type\": \"" + group_type + "\","
            + "\"phone\": [{\"phone\": \"" + phone + "\",\"primary_phone\": " + primary_address + ",\"label\": \"office\"}],"
            + "\"address\": [{\"region\": \"" + region + "\",\"primary_address\": " + primary_address + ",\"postal_code\": \"" + postal_code + "\",\"locality\": \"" + locality + "\",\"address1\": \"" + address1 + "\",\"address2\": \"" + address2 + "\",\"country_code\": \"" + country_code + "\"}],"
            + "\"email\": [{\"email\": \"" + account_email + "\",\"primary_email\": " + primary_address + ",\"password\": \"" + Account.accountPassword.toString() + "\"}]," 
            + "\"name\": \"" + group_name + "\","
            + "\"deleted\" : false,"
            + "\"media\": [{\"media_type\": \"logo\",\"media_url\": \"http:\\/\\/www.google.com\"}],\"meta\": [{\"meta_key\": \"moo\",\"meta_value\": \"meow\"}]},";
            System.out.print("Create Account Group Payload: " + create_account_group_payload +"\n");
            System.out.println("\nSending 'POST' request to URL : " + url + " with pay load: " + create_account_group_payload + "\n");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(create_account_group_payload);
            writer.close();
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Response Code : " + responseCode);
            System.out.println("Expected Response Code : " + exp_response_code);
            if(!exp_response_code.equalsIgnoreCase(Integer.toString(responseCode)) && responseCode != 409) {
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for create account group test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
                if(line.contains(group_name) && line.contains(group_type) && line.contains(phone) && line.contains(region) && line.contains(primary_address) && line.contains(postal_code) && line.contains(locality) && line.contains(address1) && line.contains(address2) && line.contains(country_code)) {
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for create account group test case. Expected that string: " + account_group_data + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
        //Capture account group id from the positive response
        if(responseCode == 200) {
            AccountType.ID.replace(0, AccountType.ID.length(), response.toString().trim().substring(7, 43));
            System.out.print("Captured id: " + AccountType.ID.toString() + "\n");
            groupID.replace(0, groupID.length(), response.toString().trim().substring(response.toString().indexOf("group_id") + 11, response.toString().indexOf("group_id") + 47));
            System.out.print("Captured group id: " + groupID.toString() + "\n");
        }
    }
    @Given("^I read account group by (.*) for account_id (.*) with account_email (.*) and validate response code (.*) and response text (.*)$")
    public void readGroup(String read_type, String account_id, String account_email, String exp_response_code, String exp_response_text) {
        URL url = null;
        int counter = 0;
        StringBuffer response = null;
        String id = "", group_type = "", group_name = "", region = "", primary_address = "", postal_code = "", locality = "", address1 = "", address2 = "", country_code = "", phone = "";
        if(baseUrl.contains("8082")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4) + "8081";
        }
        if(!"".equals(Account.accountID.toString())) {
            account_id = Account.accountID.toString(); 
        }
        if(!"".equals(AccountType.ID.toString())) {
            id = AccountType.ID.toString(); 
        }
        String[] group_parts = exp_response_text.trim().split(",");
        for(int x = 0; x < group_parts.length; x++) {
            if(x == 0) {
                region = group_parts[x];
            }
            else if(x == 1) {
                primary_address = group_parts[x];
            }
            else if(x == 2) {
                postal_code = group_parts[x];
            }
            else if(x == 3) {
                locality = group_parts[x];
            }
            else if(x == 4) {
                address1 = group_parts[x];
            }
            else if(x == 5) {
                address2 = group_parts[x];
            }
            else if(x == 6) {
                country_code = group_parts[x];
            }
            else if(x == 7) {
                phone = group_parts[x];
            }
            else if(x == 8) {
                group_name = group_parts[x];
            }
            else if(x == 9) {
                group_type = group_parts[x];
            }
        }
        try {
            if(read_type.equalsIgnoreCase("account id")) {
                url = new URL(baseUrl + "/accounts/v1.5/group/read?account_id=" + account_id);
            }
            else if(read_type.equalsIgnoreCase("email")) {
                url = new URL(baseUrl + "/accounts/v1.5/group/read?name=" + account_email + "&password=" + Account.accountPassword.toString());
            }
            else if(read_type.equalsIgnoreCase("id")) {
                url = new URL(baseUrl + "/accounts/v1.5/group/read?id[]=" + id);
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
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for read account group by " + read_type + " test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
                if(read_type.equalsIgnoreCase("id") || read_type.equalsIgnoreCase("email")) {
                    if(line.contains(group_name) && line.contains(group_type) && line.contains(phone) && line.contains(region) && line.contains(primary_address) && line.contains(postal_code) && line.contains(locality) && line.contains(address1) || line.contains(address2) && line.contains(country_code)) {
                        counter++;
                    }
                }
                else {
                    if(line.contains("Admin")) {
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for read account group by " + read_type + " test case. Expected that string: " + exp_response_text + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
    @Given("^I update account group for group_name (.*) with group_type (.*) and validate response code (.*)$")
    public void updateAccountGroup(String group_name, String group_type, String exp_response_code) {
        StringBuffer response = new StringBuffer();
        String id = "";
        int responseCode = 0, counter = 0;
        if(baseUrl.contains("8082")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4) + "8081";
        }
        if(!"".equals(AccountType.ID.toString())) {
            id = AccountType.ID.toString(); 
        }
        try {
            URL url = new URL(baseUrl + "/accounts/v1.5/address/update");
            String line = "";
            String authString = Account.username + ":" + Account.pwd;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            String update_account_group_payload = "{\"id\": \"" + id + "\","
            + "\"name\": \"" + group_name + System.currentTimeMillis() + "\"," 
            + "\"group_type\": \"" + group_type + "\","
            + "\"primary_profile\" : false,"
            + "\"deleted\" : false}";
            System.out.print("Update Account Group Payload: " + update_account_group_payload +"\n");
            System.out.println("\nSending 'POST' request to URL : " + url + " with pay load: " + update_account_group_payload + "\n");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(update_account_group_payload);
            writer.close();
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Response Code : " + responseCode);
            System.out.println("Expected Response Code : " + exp_response_code);
            if(!exp_response_code.equalsIgnoreCase(Integer.toString(responseCode)) && responseCode != 409) {
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for update account group test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
                if(line.contains(id) || line.contains(group_name) || line.contains(group_type)) {
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for update account group test case. Expected that string: " + id + " " + group_name + " " + group_type + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
    @Given("^I delete account_group and validate response code (.*) and response text is blank$")
    public void deleteAccountGroup(String exp_response_code) {
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
            url = new URL(baseUrl + "/accounts/v1.5/group/delete?id[]=" + id);
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
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for delete account group with id: " + id + ". Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for delete account group test case. Expected response: No content, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
}
