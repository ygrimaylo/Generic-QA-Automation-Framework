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

public class BillingAccount {
    @Resource
    private PlacesterSupport placesterSupport;
    @Resource
    String environment;
    @Resource
    private String baseUrl;
    
    @Given("^I create billing account for account_id (.*) with billing_account_data (.*) and validate response code (.*)$")
    public void createBillingAccount(String account_id, String billing_account_data, String exp_response_code) {
        StringBuffer response = new StringBuffer();
        int responseCode = 0, counter = 0;
        String id = "", billing_account_create_payload = "", username = "", email = "", first_name = "", last_name = "", company_name = "", city = "", address1 = "", address2 = "", country = "", phone = "", state = "", zip = "";
        if(!"".equals(Account.accountID.toString())) {
            account_id = Account.accountID.toString(); 
        }
        String[] billing_account_data_parts = billing_account_data.trim().split(",");
        for(int x = 0; x < billing_account_data_parts.length; x++) {
            if(x == 0) {
                username = billing_account_data_parts[x];
                email = username;
            }
            else if(x == 1) {
                first_name = billing_account_data_parts[x];
            }
            else if(x == 2) {
                last_name = billing_account_data_parts[x];
            }
            else if(x == 3) {
                company_name = billing_account_data_parts[x];
            }
            else if(x == 4) {
                state = billing_account_data_parts[x];
            }
            else if(x == 5) {
                address1 = billing_account_data_parts[x];
            }
            else if(x == 6) {
                address2 = billing_account_data_parts[x];
            }
            else if(x == 7) {
                country = billing_account_data_parts[x];
            }
            else if(x == 8) {
                phone = billing_account_data_parts[x];
            }
            else if(x == 9) {
                zip = billing_account_data_parts[x];
            }
            else if(x == 10) {
                city = billing_account_data_parts[x];
            }
        }
        try {
            URL url = new URL(baseUrl + "/billing/v1.5/billingaccount/create");
            String line = "";
            String authString = Account.username + ":" + Account.pwd;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            billing_account_create_payload = "{\"username\": \"" + username + "\","
            + "\"email\": \"" + email + "\","
            + "\"account_id\": \"" + account_id + "\"," 
            + "\"first_name\": \"" + first_name + "\","
            + "\"last_name\": \"" + last_name + "\","
            + "\"company_name\": \"" + company_name + "\","
            + "\"city\": \"" + city + "\","
            + "\"address1\": \"" + address1 + "\","
            + "\"address2\": \"" + address2 + "\","
            + "\"state\": \"" + state + "\","
            + "\"zip\": \"" + zip + "\","
            + "\"phone\": \"" + phone + "\","
            + "\"country\": \"" + country + "\"}";
            System.out.print("Create Billing Account Payload: " + billing_account_create_payload +"\n");
            System.out.println("\nSending 'POST' request to URL : " + url + " with pay load: " + billing_account_create_payload + "\n");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(billing_account_create_payload);
            writer.close();
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Response Code : " + responseCode);
            System.out.println("Expected Response Code : " + exp_response_code);
            if(!exp_response_code.equalsIgnoreCase(Integer.toString(responseCode)) && responseCode != 409) {
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for create billing account test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
                if(line.contains(account_id) && line.contains(state) && line.contains(country) && line.contains(zip) && line.contains(city) && line.contains(username) && line.contains(email) && line.contains(address1) && line.contains(address2) && line.contains(phone) && line.contains(first_name) && line.contains(last_name) && line.contains(company_name)) {
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for create billing account test case. Expected that string: " + account_id + " " + username + " " + first_name + " " + last_name + " " + company_name + " " + country + " " + zip + " " + address1 + " " + address2 + " " + phone + " " + city + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
        //Capture billing id from the positive response
        if(responseCode == 200) {
            String[] response_parts = response.toString().trim().split(":");
            for(int x = 0; x < response_parts.length; x++) {
                if(x == 13) {
                    System.out.print("response_parts[" + x + "]=" + response_parts[x] + "\n");
                    id = response_parts[x].substring(1, 37);
                    AccountType.ID.replace(0, AccountType.ID.length(), id);
                }
            }
            System.out.print("Captured id: " + AccountType.ID.toString() + "\n");
        }
    }
    @Given("^I create billing coupon with coupon data (.*) and validate response code (.*)$")
    public void createBillingCoupon(String billing_coupon_data, String exp_response_code) {
        StringBuffer response = new StringBuffer();
        int responseCode = 0, counter = 0;
        String billing_coupon_create_payload = "", coupon_name = "", coupon_description = "", discount_percent = "", code_id = "", plan_id = "";
        String[] billing_coupon_data_parts = billing_coupon_data.trim().split(",");
        for(int x = 0; x < billing_coupon_data_parts.length; x++) {
            if(x == 0) {
                coupon_name = billing_coupon_data_parts[x];
            }
            else if(x == 1) {
                coupon_description = billing_coupon_data_parts[x];
            }
            else if(x == 2) {
                discount_percent = billing_coupon_data_parts[x];
            }
            else if(x == 3) {
                code_id = billing_coupon_data_parts[x] + System.currentTimeMillis();
            }
            else if(x == 4) {
                plan_id = billing_coupon_data_parts[x];
            }
        }
        try {
            URL url = new URL(baseUrl + "/billing/v1.5/coupon/create");
            String line = "";
            String authString = Account.username + ":" + Account.pwd;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            billing_coupon_create_payload = "{\"plans\": [ \"" + plan_id + "\" ],"
            + "\"name\": \"" + coupon_name + "\","
            + "\"description\": \"" + coupon_description + "\"," 
            + "\"code\": \"" + code_id + "\","
            + "\"discount_percent\": \"" + discount_percent + "\","
            + "\"lifetime\": 1}";
            System.out.print("Create Billing Account Payload: " + billing_coupon_create_payload +"\n");
            System.out.println("\nSending 'POST' request to URL : " + url + " with pay load: " + billing_coupon_create_payload + "\n");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(billing_coupon_create_payload);
            writer.close();
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Response Code : " + responseCode);
            System.out.println("Expected Response Code : " + exp_response_code);
            if(!exp_response_code.equalsIgnoreCase(Integer.toString(responseCode)) && responseCode != 409) {
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for create billing coupon test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
                if(line.contains(coupon_name) && line.contains(coupon_description) && line.contains(code_id) && line.contains(plan_id) && line.contains(discount_percent)) {
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for create billing coupon test case. Expected that string: " + coupon_name + " " + coupon_description + " " + discount_percent + " " + plan_id + " " + code_id + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
    @Given("^I update billing account for account_id (.*) with billing_account_data (.*) and validate response code (.*)$")
    public void updateBillingAccount(String account_id, String billing_account_data, String exp_response_code) {
        StringBuffer response = new StringBuffer();
        int responseCode = 0, counter = 0;
        String id = "", billing_account_update_payload = "", username = "", email = "", first_name = "", last_name = "", company_name = "", city = "", address1 = "", address2 = "", country = "", phone = "", state = "", zip = "";
        if(!"".equals(Account.accountID.toString())) {
            account_id = Account.accountID.toString(); 
        }
        if(!"".equals(AccountType.ID.toString())) {
            id = AccountType.ID.toString(); 
        }
        String[] billing_account_data_parts = billing_account_data.trim().split(",");
        for(int x = 0; x < billing_account_data_parts.length; x++) {
            if(x == 0) {
                username = billing_account_data_parts[x];
                email = username;
            }
            else if(x == 1) {
                first_name = billing_account_data_parts[x];
            }
            else if(x == 2) {
                last_name = billing_account_data_parts[x];
            }
            else if(x == 3) {
                company_name = billing_account_data_parts[x];
            }
            else if(x == 4) {
                state = billing_account_data_parts[x];
            }
            else if(x == 5) {
                address1 = billing_account_data_parts[x];
            }
            else if(x == 6) {
                address2 = billing_account_data_parts[x];
            }
            else if(x == 7) {
                country = billing_account_data_parts[x];
            }
            else if(x == 8) {
                phone = billing_account_data_parts[x];
            }
            else if(x == 9) {
                zip = billing_account_data_parts[x];
            }
            else if(x == 10) {
                city = billing_account_data_parts[x];
            }
        }
        try {
            URL url = new URL(baseUrl + "/billing/v1.5/billingaccount/update");
            String line = "";
            String authString = Account.username + ":" + Account.pwd;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            billing_account_update_payload = "{\"username\": \"" + username + "\","
            + "\"email\": \"" + email + "\","
            + "\"account_id\": \"" + account_id + "\"," 
            + "\"id\": \"" + id + "\","
            + "\"first_name\": \"" + first_name + "\","
            + "\"last_name\": \"" + last_name + "\","
            + "\"company_name\": \"" + company_name + "\","
            + "\"city\": \"" + city + "\","
            + "\"address1\": \"" + address1 + "\","
            + "\"address2\": \"" + address2 + "\","
            + "\"state\": \"" + state + "\","
            + "\"zip\": \"" + zip + "\","
            + "\"phone\": \"" + phone + "\","
            + "\"country\": \"" + country + "\"}";
            System.out.print("Update Billing Account Payload: " + billing_account_update_payload +"\n");
            System.out.println("\nSending 'POST' request to URL : " + url + " with pay load: " + billing_account_update_payload + "\n");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(billing_account_update_payload);
            writer.close();
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Response Code : " + responseCode);
            System.out.println("Expected Response Code : " + exp_response_code);
            if(!exp_response_code.equalsIgnoreCase(Integer.toString(responseCode)) && responseCode != 409) {
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for update billing account test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
                if(line.contains(account_id) && line.contains(state) && line.contains(country) && line.contains(zip) && line.contains(city) && line.contains(username) && line.contains(email) && line.contains(address1) && line.contains(address2) && line.contains(phone) && line.contains(first_name) && line.contains(last_name) && line.contains(company_name)) {
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for update billing account test case. Expected that string: " + account_id + " " + username + " " + first_name + " " + last_name + " " + company_name + " " + country + " " + zip + " " + address1 + " " + address2 + " " + phone + " " + city + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
    @Given("^I read billing account by (.*) for account_id (.*) and validate response code (.*) and response text (.*)$")
    public void readBillingAccount(String account_billing_type, String account_id, String exp_response_code, String exp_response_text) {
        URL url = null;
        int counter = 0, responseCode = 0;
        StringBuffer response = null;
        String id = "", username = "", email = "", first_name = "", last_name = "", company_name = "", city = "", address1 = "", address2 = "", country = "", phone = "", state = "", zip = "";
        if(!"".equals(Account.accountID.toString())) {
            account_id = Account.accountID.toString(); 
        }
        if(!"".equals(AccountType.ID.toString())) {
            id = AccountType.ID.toString(); 
        }
        String[] billing_account_data_parts = exp_response_text.trim().split(",");
        for(int x = 0; x < billing_account_data_parts.length; x++) {
            if(x == 0) {
                username = billing_account_data_parts[x];
                email = username;
            }
            else if(x == 1) {
                first_name = billing_account_data_parts[x];
            }
            else if(x == 2) {
                last_name = billing_account_data_parts[x];
            }
            else if(x == 3) {
                company_name = billing_account_data_parts[x];
            }
            else if(x == 4) {
                state = billing_account_data_parts[x];
            }
            else if(x == 5) {
                address1 = billing_account_data_parts[x];
            }
            else if(x == 6) {
                address2 = billing_account_data_parts[x];
            }
            else if(x == 7) {
                country = billing_account_data_parts[x];
            }
            else if(x == 8) {
                phone = billing_account_data_parts[x];
            }
            else if(x == 9) {
                zip = billing_account_data_parts[x];
            }
            else if(x == 10) {
                city = billing_account_data_parts[x];
            }
        }
        try {
            if(account_billing_type.equalsIgnoreCase("id")) {
                url = new URL(baseUrl + "/billing/v1.5/billingaccount/read?id[]=" + id);
            }
            else if(account_billing_type.equalsIgnoreCase("account_id")) {
                url = new URL(baseUrl + "/billing/v1.5/billingaccount/read?account_id[]=" + account_id);
            }
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
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for read billing account test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
                if(line.contains(account_id) && line.contains(state) && line.contains(country) && line.contains(zip) && line.contains(city) && line.contains(username) && line.contains(email) && line.contains(address1) && line.contains(address2) && line.contains(phone) && line.contains(first_name) && line.contains(last_name) && line.contains(company_name)) {
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for read billing account test case. Expected that string: " + exp_response_text + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
    @Given("^I delete billing_account and validate response code (.*) and response text is blank$")
    public void deleteBillingAccount(String exp_response_code) {
        URL url = null;
        int counter = 0;
        StringBuffer response = null;
        String id = "";
        if(!"".equals(AccountType.ID.toString())) {
            id = AccountType.ID.toString(); 
        }
        try {
            url = new URL(baseUrl + "/billing/v1.5/billingaccount/delete?id[]=" + id);
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
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for delete billing account with id: " + id + ". Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for delete billing account test case. Expected response: No content, but it was not found and actual response was: " + response.toString() + "\n");
        }
    }
}
