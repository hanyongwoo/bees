package com.automation.abi.bees.runner;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderAuto {

    
    public static void main(String[] args) throws ParseException {


        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{\"WS_ID\":\"2148665741\",\"POC_ID\":\"2148665741\"}");
        
        Request request = new Request.Builder().url("https://dev.bees-kconnect.com/eai/extras/common_keai_0001")
                            .addHeader("Authorization", "Basic ZWFpX2tib246ZWFpX2tib25Ab2IuY28ua3I=")
                            .addHeader("SourceSystem", "KEAI")
                            .addHeader("Content-Type", "application/json")
                            .addHeader("timezone", "Asia/Seoul")
                            .addHeader("country", "KR")
                            .method("POST",body)
                            .build();

        try {

            Response response = okHttpClient.newCall(request).execute();
            Map<String, Object> responseMap = new ObjectMapper().readValue(response.body().byteStream(), HashMap.class);
            String accessToken = (String) responseMap.get("token");
    
            okHttpClient = new OkHttpClient().newBuilder().build();

            //System.out.println(accessToken);
            
            request = new Request.Builder().url("https://dev.bees-kconnect.com/eai/orders/order_kbon_0003")
            .addHeader("SourceSystem", "KEAI")
            .addHeader("Content-Type", "application/json")
            .addHeader("country", "KR")
            .addHeader("Authorization", "Bearer " + accessToken)
            .addHeader("payload-param", "?country=KR&sort=DESC")
            .addHeader("timezone", "Asia/Seoul").build();
        
            response = okHttpClient.newCall(request).execute();


            responseMap = new ObjectMapper().readValue(response.body().byteStream(), HashMap.class);

            JSONParser parser = new JSONParser();
            JSONArray payload = (JSONArray)parser.parse(responseMap.get("Message").toString());

            System.out.println(payload.size());
            for(int i = 0 ; i < payload.size() ; i++) {
                JSONObject tempObject = (JSONObject)payload.get(i);
                String parsingDate = parsingDate((String)tempObject.get("placementDate"));

                JSONObject vendor = (JSONObject)tempObject.get("vendor");
                System.out.println(parsingDate + " | " + tempObject.get("orderNumber") + " | " + vendor.get("accountId") );
            }
            

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // OkHttpClient client = new OkHttpClient().newBuilder()
		// .build();
		// MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
		// RequestBody body = RequestBody.create(mediaType, "client_id=a48baf13-ea1a-4399-a8bd-0e76ebc84c21&client_secret=13c3f791-0bc5-4523-9341-8668a7669f6b&vendor_id=3ccf952a-d597-4ebf-994c-d9d985ce072b");
		// Request request = new Request.Builder()
		// .url("https://services.bees-platform.com/api/auth/token")
		// .method("POST", body)
		// .addHeader("requestTraceId", "getTokenfromhanseong")
		// .addHeader("Content-Type", "application/x-www-form-urlencoded")
		// .build();

		// try {
		// 	Response response = client.newCall(request).execute();
        //     Map<String, Object> responseMap = new ObjectMapper().readValue(response.body().byteStream(), HashMap.class);
			
        //     String accessToken = (String) responseMap.get("access_token");

        //     request = new Request.Builder()
        //     .url("https://services.bees-platform.com/v1/order-service/v2?country=KR&sort=DESC&orderBy=placementDate&updatedSince=")
        //     .method("GET", null)
        //     .addHeader("Content-Type", "application/json")
        //     .addHeader("country", "KR")
        //     .addHeader("requestTraceId", "getOrderforOB")
        //     .addHeader("Authorization", "Bearer " + accessToken)
        //     .build();

        //     response = client.newCall(request).execute();
        //     System.out.println("Response" + response.body().string());  

        
        //     //JSONObject jos = new JSONObject(response.body().string());
        
		// } catch (IOException e) {
		// 	// TODO Auto-generated catch block
		// 	e.printStackTrace();
		// }
    }

    public static String parsingDate(String pDate) {
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX",Locale.KOREA).parse(pDate);
            SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd");
            String parse = newDtFormat.format(date);
        

            return parse;
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }
}
