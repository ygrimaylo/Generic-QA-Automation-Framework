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

public class Csv {
    @Resource
    private PlacesterSupport placesterSupport;
    @Resource
    String environment;
    @Resource
    private String baseUrl;
    
    @Given("^I convert csv to json and validate response code (.*)$")
    public void csvToJSONConverter(String exp_response_code) {
        StringBuffer response = new StringBuffer();
        int responseCode = 0, counter = 0;
        String exp_response_text = "{\"header\":[\"a\",\"b\",\"c\"],\"rows\":[{\"b\":\"two\",\"c\":\"three\",\"a\":\"one\"},{\"b\":\"five\",\"c\":\"size\",\"a\":\"four\"}]}";
        if(baseUrl.contains("8082")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 4) + "8081";
        }
        try {
            URL url = new URL(baseUrl + "/accounts/v1.5/csv");
            String line = "";
            String authString = Account.username + ":" + Account.pwd;
            System.out.println("auth string: " + authString);
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            String authStringEnc = new String(authEncBytes);
            System.out.println("Base64 encoded auth string: " + authStringEnc);
            String convert_csv_to_json_payload = "a,b,c\none,two,three,\nfour,five,size";
            System.out.print("Convert CSV to JSON Payload: " + convert_csv_to_json_payload +"\n");
            System.out.println("\nSending 'POST' request to URL : " + url + " with pay load: " + convert_csv_to_json_payload + "\n");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(convert_csv_to_json_payload);
            writer.close();
            try {
                responseCode = connection.getResponseCode();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            System.out.println("Response Code : " + responseCode);
            System.out.println("Expected Response Code : " + exp_response_code);
            if(!exp_response_code.equalsIgnoreCase(Integer.toString(responseCode)) && responseCode != 409) {
                placesterSupport.VerifyFailNoScreenshot("Response Code validation failed for convert csv to json test case. Expected: " + exp_response_code + ", but Actual was: " + responseCode + "\n");
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
                if(line.contains(exp_response_text)) {
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
            placesterSupport.VerifyFailNoScreenshot("Response Text validation failed for convert csv to json test case. Expected that string: " + exp_response_text + " should be found within the response, but it was not found and actual response was: " + response.toString() + "\n");
        }
        //Capture account media id from the positive response
        if(responseCode == 200) {
            AccountType.ID.replace(0, AccountType.ID.length(), response.toString().trim().substring(7, 43));
            System.out.print("Captured id: " + AccountType.ID.toString() + "\n");
        }
    }
}
