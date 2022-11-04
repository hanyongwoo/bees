package com.automation.abi.bees.runner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class AccountAuto {
    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
        //System.out.println("테스트");

		String test = "2022-11-03T05:30:40+00:00";
		final Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX",Locale.KOREA).parse(test);
		//Date date = format.parse(test);

		SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd");
		String parse = newDtFormat.format(date);
		
		System.out.println(parse);

		// File reader = new File("C:/Users/16904/Documents/BEES/bees/src/main/resources/static/화창 Account.txt");
		
		// BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(reader), "utf-8"));

		// JSONParser parser = new JSONParser();
		// System.out.println(parser);
		// Object object = parser.parse(br);
		
		// JSONObject jsonObject = (JSONObject)object;
		
		// JSONArray jsonArray = (JSONArray)jsonObject.get("payload");
		// //JSONObject ojbj = (JSONObject)jsonObject.get("payload");
		// if(jsonArray.size() > 0) {
		// 	for(int i=0; i<jsonArray.size(); i++){
		// 		JSONObject jObject = (JSONObject)jsonArray.get(i);
		// 		JSONObject owner = (JSONObject) jObject.get("owner");
		// 		String str = jObject.getOrDefault("accountId","") + 
        //         " | " + jObject.getOrDefault("customerAccountId","") + 
        //         " | " + jObject.getOrDefault("name","") + " | ";
        //         // " | " + owner.get("firstName") +
        //         // " | " + owner.get("phone");
		// 		//System.out.println(jObject.get("accountId") + " | " + jObject.get("customerAccountId") );

		// 		JSONArray reArr = (JSONArray)jObject.get("representatives");
		// 		if(reArr != null) {
        //             for(int j=0; j<reArr.size(); j++){
        //                 JSONObject reO = (JSONObject)reArr.get(j);
        //                 str += reO.getOrDefault("name","") + " | " + reO.getOrDefault("phone","");
        //             }
        //         }
        //         System.out.println(str);
		// 	}
		// }
    }
}
