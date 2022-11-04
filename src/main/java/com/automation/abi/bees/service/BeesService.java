package com.automation.abi.bees.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.automation.abi.bees.entity.Monitor.Monitor;
import com.automation.abi.bees.entity.Monitor.MonitorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class BeesService {

    @Autowired
    private MonitorRepository monitorRepository;
    
	/**
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ParseException
	 * @description Invoice 데이터 추출
	 */
    public JSONArray invoiceService(String startDate, String endDate) throws ParseException {
		String if_id = InterfaceId.INVOICE_POST.getValue();
		String ws_id = "2148665741";
		//String result = "";
		JSONArray resultArray = new JSONArray();

		int sy = Integer.parseInt(startDate.substring(0, 4));
		int sm = Integer.parseInt(startDate.substring(4, 6));
		int sd = Integer.parseInt(startDate.substring(6	,8));

		int ey = Integer.parseInt(endDate.substring(0, 4));
		int em = Integer.parseInt(endDate.substring(4, 6));
		int ed = Integer.parseInt(endDate.substring(6	,8));
			
		LocalDateTime startDatetime = LocalDateTime.of(LocalDate.of(sy, sm, sd), LocalTime.of(0,0,0));
		LocalDateTime endDatetime = LocalDateTime.of(LocalDate.of(ey, em, ed), LocalTime.of(23,59,59));

		System.out.println(startDatetime + " , " + endDatetime);
		List<Monitor> a = monitorRepository.findByProcDateBetweenAndIfIdAndWsId(startDatetime, endDatetime, if_id, ws_id);

		JSONParser parser = new JSONParser();

		for(int i = 0; i < a.size() ; i++) {
			Object object = parser.parse(a.get(i).getDebugData());
		
			JSONObject jsonObject = (JSONObject)object;
			
			String jsonArrayString = jsonObject.get("payload").toString();
			JSONArray payload = (JSONArray)parser.parse(jsonArrayString);
			
			for(int j = 0; j < payload.size() ; j++) {
				JSONObject obj = (JSONObject)payload.get(j);
				JSONObject resultobj = new JSONObject();
				// Invoice 번호 | Account ID | total
				JSONObject vendor = (JSONObject)obj.get("vendor");
				
				System.out.println(vendor.get("invoiceId") + " | " + vendor.get("accountId") + " | " + obj.get("total"));
				resultobj.put("invoiceId", vendor.get("invoiceId"));
				resultobj.put("accountId", vendor.get("accountId"));
				resultobj.put("total", obj.get("total"));

				resultArray.add(resultobj);
			}
		}
		//System.out.println(result);
		return resultArray;
	};

	/**
	 * 
	 * @param date
	 * @param wsId
	 * @return
	 * @throws ParseException
	 * @description Item SKU 코드 전날 비교 추출
	 */
    public JSONArray itemDiff(String date, String wsId) throws ParseException {
		String if_id = InterfaceId.ITEM_POST.getValue();
		String ws_id = wsId;
		//String result = "";
		JSONArray resultArray = new JSONArray();
		
		
		int sy = Integer.parseInt(date.substring(0, 4));
		int sm = Integer.parseInt(date.substring(4, 6));
		int sd = Integer.parseInt(date.substring(6	,8));

		LocalDateTime startDatetime = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(0,0,0));
		LocalDateTime endDatetime = LocalDateTime.of(LocalDate.of(sy, sm, sd), LocalTime.of(23,59,59));

		System.out.println(startDatetime + " , " + endDatetime);
		List<Monitor> a = monitorRepository.findByProcDateBetweenAndIfIdAndWsId(startDatetime, endDatetime, if_id, ws_id);

		JSONParser parser = new JSONParser();

		for(int i = 0; i < a.size() ; i++) {
			Object object = parser.parse(a.get(i).getDebugData());
		
			JSONObject jsonObject = (JSONObject)object;

			String jsonArrayString = jsonObject.get("payload").toString();
			JSONArray payload = (JSONArray)parser.parse(jsonArrayString);
			
			for(int j = 0; j < payload.size() ; j++) {
				JSONObject obj = (JSONObject)payload.get(j);
				JSONObject resultobj = new JSONObject();
				// Invoice 번호 | Account ID | total
				JSONObject vendor = (JSONObject)obj.get("vendor");
				
				System.out.println(vendor.get("invoiceId") + " | " + vendor.get("accountId") + " | " + obj.get("total"));
				resultobj.put("invoiceId", vendor.get("invoiceId"));
				resultobj.put("accountId", vendor.get("accountId"));
				resultobj.put("total", obj.get("total"));

				resultArray.add(resultobj);
				
			}
		}
		return resultArray;
	};

	/**
	 * 
	 * @param updateSince
	 * @return
	 * @throws ParseException
	 * @description : 주문정보 조회
	 */
	public JSONArray getOrderInfoArrangement(String updateSince) throws ParseException {
		JSONArray resultArray = new JSONArray();
		
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

				JSONObject resultobj = new JSONObject();
				resultobj.put("date", parsingDate);
				resultobj.put("orderNumber", tempObject.get("orderNumber"));
				resultobj.put("accountId", vendor.get("accountId") );

				resultArray.add(resultobj);
            }
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

		return resultArray;
	}

	/**
	 * UTC 시간 변환
	 * @param pDate
	 * @return
	 */
	public String parsingDate(String pDate) {
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

