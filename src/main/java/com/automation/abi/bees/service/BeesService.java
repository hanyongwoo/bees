package com.automation.abi.bees.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.automation.abi.bees.conf.ConfigUtil;
import com.automation.abi.bees.entity.Monitor.Monitor;
import com.automation.abi.bees.entity.Monitor.MonitorRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@RequiredArgsConstructor
@Service
public class BeesService {

	@Autowired
	private MonitorRepository monitorRepository;

	private final ConfigUtil configUtil;

	/**
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ParseException
	 * @description Invoice 데이터 추출
	 */
	public JSONArray invoiceService(String startDate, String endDate) throws ParseException {
		String ws_id = configUtil.getProperty("UAT_WS_ID");
		// String result = "";
		JSONArray resultArray = new JSONArray();

		List<Monitor> a = invoiceExtracted(startDate, endDate, ws_id);

		JSONParser parser = new JSONParser();

		for (int i = 0; i < a.size(); i++) {
			Object object = parser.parse(a.get(i).getDebugData());

			JSONObject jsonObject = (JSONObject) object;

			String jsonArrayString = jsonObject.get("payload").toString();
			JSONArray payload = (JSONArray) parser.parse(jsonArrayString);

			for (int j = 0; j < payload.size(); j++) {
				JSONObject obj = (JSONObject) payload.get(j);
				JSONObject resultobj = new JSONObject();
				// Invoice 번호 | Account ID | total
				JSONObject vendor = (JSONObject) obj.get("vendor");

				System.out.println(vendor.get("invoiceId") + " | " + vendor.get("accountId") + " | " + obj.get("total"));
				resultobj.put("invoiceId", vendor.get("invoiceId"));
				resultobj.put("accountId", vendor.get("accountId"));
				resultobj.put("total", obj.get("total"));

				resultArray.add(resultobj);
			}
		}
		return resultArray;
	}

	/**
	 * invoice 데이터 조회
	 * 
	 * @param startDate
	 * @param endDate
	 * @param if_id
	 * @param ws_id
	 * @return
	 */
	private List<Monitor> invoiceExtracted(String startDate, String endDate, String ws_id) {

		String if_id = InterfaceId.INVOICE_POST.getValue();

		int sy = Integer.parseInt(startDate.substring(0, 4));
		int sm = Integer.parseInt(startDate.substring(4, 6));
		int sd = Integer.parseInt(startDate.substring(6, 8));

		int ey = Integer.parseInt(endDate.substring(0, 4));
		int em = Integer.parseInt(endDate.substring(4, 6));
		int ed = Integer.parseInt(endDate.substring(6, 8));

		LocalDateTime startDatetime = LocalDateTime.of(LocalDate.of(sy, sm, sd), LocalTime.of(0, 0, 0));
		LocalDateTime endDatetime = LocalDateTime.of(LocalDate.of(ey, em, ed), LocalTime.of(23, 59, 59));

		List<Monitor> a = monitorRepository.findByProcDateBetweenAndIfIdAndWsId(startDatetime, endDatetime, if_id,ws_id);

		return a;
	};

	/**
	 * Account 데이터 조회
	 * 
	 * @param startDate
	 * @param endDate
	 * @param if_id
	 * @param ws_id
	 * @return
	 */
	private List<Monitor> accountExtracted(String ws_id) {

		String if_id = InterfaceId.ACCOUNT_POST.getValue();

		LocalDateTime startDatetime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0));
		LocalDateTime endDatetime = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59));

		List<Monitor> a = monitorRepository.findByProcDateBetweenAndIfIdAndWsId(startDatetime, endDatetime, if_id, ws_id);

		return a;
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
		
		JSONArray resultArray = new JSONArray();

		int sy = Integer.parseInt(date.substring(0, 4));
		int sm = Integer.parseInt(date.substring(4, 6));
		int sd = Integer.parseInt(date.substring(6, 8));

		LocalDateTime startDatetime = LocalDateTime.of(LocalDate.of(sy, sm, sd).minusDays(1), LocalTime.of(0, 0, 0));
		LocalDateTime endDatetime = LocalDateTime.of(LocalDate.of(sy, sm, sd), LocalTime.of(0, 0, 0));
		
		itemDiffExtracted(resultArray, startDatetime, endDatetime, wsId);
		accountDiffExtracted(resultArray, startDatetime, endDatetime, wsId);
		

		return resultArray;
	}

	// Account 전날 비교
	private void accountDiffExtracted(JSONArray resultArray, LocalDateTime startDatetime, LocalDateTime endDatetime, String wsId) throws ParseException {
		// 데이터 조회
		Map<String,List> before = new HashMap<>();
		Map<String,List> after = new HashMap<>();

		String if_id = InterfaceId.ACCOUNT_POST.getValue();

		List<Monitor> a = monitorRepository.findByProcDateBetweenAndIfIdAndWsId(startDatetime, startDatetime, if_id,wsId);
		List<Monitor> b = monitorRepository.findByProcDateBetweenAndIfIdAndWsId(endDatetime, endDatetime, if_id,wsId);

		JSONParser parser = new JSONParser();

		// 전일 데이터 조회
		for (int i = 0; i < a.size(); i++) {
			Object object = parser.parse(a.get(i).getDebugData());

			JSONObject jsonObject = (JSONObject) object;

			String jsonArrayString = jsonObject.get("payload").toString();
			JSONArray payload = (JSONArray) parser.parse(jsonArrayString);

			for (int j = 0; j < payload.size(); j++) {
				List customerList = new ArrayList<>();
				JSONObject obj = (JSONObject) payload.get(j);

				customerList.add(obj.get("customerAccountId"));
				customerList.add(obj.get("displayName"));

				before.put((String)obj.get("accountId"), customerList);
			}
		}

		// 당일 데이터 조회
		for (int i = 0; i < b.size(); i++) {
			Object object = parser.parse(b.get(i).getDebugData());

			JSONObject jsonObject = (JSONObject) object;

			String jsonArrayString = jsonObject.get("payload").toString();
			JSONArray payload = (JSONArray) parser.parse(jsonArrayString);

			for (int j = 0; j < payload.size(); j++) {
				List customerList = new ArrayList<>();
				JSONObject obj = (JSONObject) payload.get(j);
				
				customerList.add(obj.get("customerAccountId"));
				customerList.add(obj.get("displayName"));

				after.put((String)obj.get("accountId"), customerList);
			}
		}

		Set<String> aKeys = before.keySet();
		Set<String> bKeys = after.keySet();

		System.out.println(aKeys.size() + " , " + bKeys.size());

		JSONObject resultobj = new JSONObject();
		resultobj.put("beforeAccount", aKeys.size());
		resultobj.put("afterAccount", bKeys.size());

		//resultArray += "beforeAccount : " + aKeys.size() + " , afterAccount : " +  bKeys.size() + "<br/>";
		resultArray.add(resultobj);


		// 추가된 account 찾기
		for(String key : bKeys) {
			if(before.get(key) == null){
				resultobj = new JSONObject();
				resultobj.put("+account", key);
				resultobj.put("name", after.get(key));
				//resultArray += "+account : " + key + "(" + after.get(key) + ")<br/>" ;
				resultArray.add(resultobj);
			}
		}

		// 삭제된 account 찾기
		for(String key : aKeys) {
			if(after.get(key) == null){
				resultobj = new JSONObject();
				resultobj.put("-account", key);
				resultobj.put("name", before.get(key));
				//resultArray += "-sku : " + key + "(" + before.get(key) + ")<br/>" ;
				resultArray.add(resultobj);
			}
		}
	}

	// Item 전날 비교
	private void itemDiffExtracted(JSONArray resultArray, LocalDateTime startDatetime, LocalDateTime endDatetime, String wsId) throws ParseException {
		// 데이터 조회
		Map before = new HashMap<>();
		Map after = new HashMap<>();

		String if_id = InterfaceId.ITEM_POST.getValue();

		List<Monitor> a = monitorRepository.findByProcDateBetweenAndIfIdAndWsId(startDatetime, startDatetime, if_id,wsId);
		List<Monitor> b = monitorRepository.findByProcDateBetweenAndIfIdAndWsId(endDatetime, endDatetime, if_id,wsId);

		JSONParser parser = new JSONParser();
		
		for (int i = 0; i < a.size(); i++) {
			Object object = parser.parse(a.get(i).getDebugData());

			JSONObject jsonObject = (JSONObject) object;

			String jsonArrayString = jsonObject.get("payload").toString();
			JSONArray payload = (JSONArray) parser.parse(jsonArrayString);

			for (int j = 0; j < payload.size(); j++) {
				JSONObject obj = (JSONObject) payload.get(j);
				before.put(obj.get("sku"), obj.get("name"));
			}
		}

		// 당일 데이터 조회
		for (int i = 0; i < b.size(); i++) {
			Object object = parser.parse(b.get(i).getDebugData());

			JSONObject jsonObject = (JSONObject) object;

			String jsonArrayString = jsonObject.get("payload").toString();
			JSONArray payload = (JSONArray) parser.parse(jsonArrayString);

			for (int j = 0; j < payload.size(); j++) {
				JSONObject obj = (JSONObject) payload.get(j);
				after.put(obj.get("sku"), obj.get("name"));
			}
		}

		Set<String> aKeys = before.keySet();
		Set<String> bKeys = after.keySet();

		//System.out.println(aKeys.size() + " , " + bKeys.size());

		JSONObject resultobj = new JSONObject();
		
		resultobj.put("beforeSKU", aKeys.size());
		resultobj.put("afterSKU", bKeys.size());

		//resultArray += "beforeSKU : " + aKeys.size() + " , afterSKU : " +  bKeys.size() + "<br/>";

		resultArray.add(resultobj);


		// 추가된 sku 찾기
		for(String key : bKeys) {
			if(before.get(key) == null){
				resultobj = new JSONObject();
				resultobj.put("+sku", key);
				resultobj.put("name", after.get(key));
				//resultArray += "+sku : " + key + "(" + after.get(key) + ")<br/>" ;
				resultArray.add(resultobj);
				
			}
		}

		// 삭제된 sku 찾기
		for(String key : aKeys) {
			if(after.get(key) == null){
				resultobj = new JSONObject();
				resultobj.put("-sku", key);
				resultobj.put("name", before.get(key));
				//resultArray += "-sku : " + key + "(" + before.get(key) + ")<br/>" ;
				resultArray.add(resultobj);
			}
		}
	};

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 * @throws ParseException
	 * @description : 주문정보 조회
	 */
	public JSONArray getOrderInfoArrangement(String startDate, String endDate, String wsId) throws ParseException {
		JSONArray resultArray = new JSONArray();
		Map invoiceMap = new HashMap<>();
		Map accountMap = new HashMap<>();
		
		String ws_id = wsId; //configUtil.getProperty("UAT.WS_ID");

		// invoice 리스트 조회
		List<Monitor> a = invoiceExtracted(startDate, endDate, ws_id);
		JSONParser parser = new JSONParser();

		for (int i = 0; i < a.size(); i++) {
			Object object = parser.parse(a.get(i).getDebugData());

			JSONObject jsonObject = (JSONObject) object;

			String jsonArrayString = jsonObject.get("payload").toString();
			JSONArray payload = (JSONArray) parser.parse(jsonArrayString);

			for (int j = 0; j < payload.size(); j++) {
				JSONObject obj = (JSONObject) payload.get(j);
				// AccountId | customerAccountId | total
				JSONObject vendor = (JSONObject) obj.get("vendor");
				invoiceMap.put(vendor.get("invoiceId"), obj.get("total"));
			}
		}

		// Account 리스트 조회
		List<Monitor> accountExtracted = accountExtracted(ws_id);
		for (int i = 0; i < accountExtracted.size(); i++) {
			Object object = parser.parse(accountExtracted.get(i).getDebugData());

			JSONObject jsonObject = (JSONObject) object;

			String jsonArrayString = jsonObject.get("payload").toString();
			JSONArray payload = (JSONArray) parser.parse(jsonArrayString);

			for (int j = 0; j < payload.size(); j++) {
				JSONObject obj = (JSONObject) payload.get(j);
				//List<String> accountList = new ArrayList<>();
			
				// Invoice 번호 | Account ID | total
				//accountList.add((String)obj.get("customerAccountId"));
				//accountList.add((String)obj.get("displayName"));

				accountMap.put((String)obj.get("accountId"), (String)obj.get("customerAccountId") + " | " + (String)obj.get("displayName"));
			}
		}

		OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, "{\"WS_ID\":\"" + ws_id
				+ "\",\"POC_ID\":\"" + ws_id + "\"}");

		Request request = new Request.Builder()
				.url(configUtil.getProperty("UAT.KCON.URL") + "eai/extras/common_keai_0001")
				.addHeader("Authorization", "Basic " + configUtil.getProperty("UAT.KCON.BASIC"))
				.addHeader("SourceSystem", "KEAI")
				.addHeader("Content-Type", "application/json")
				.addHeader("timezone", "Asia/Seoul")
				.addHeader("country", "KR")
				.method("POST", body)
				.build();

		try {

			Response response = okHttpClient.newCall(request).execute();

			if(response.isSuccessful()) {
				Map<String, Object> responseMap = new ObjectMapper().readValue(response.body().byteStream(), HashMap.class);
				String accessToken = (String) responseMap.get("token");

				okHttpClient = new OkHttpClient().newBuilder().build();

				String updateSince = stringToUTCtime(startDate);
				System.out.println(updateSince);

				request = new Request.Builder().url(configUtil.getProperty("UAT.KCON.URL") + "eai/orders/order_kbon_0003")
						.addHeader("SourceSystem", "KEAI")
						.addHeader("Content-Type", "application/json")
						.addHeader("country", "KR")
						.addHeader("Authorization", "Bearer " + accessToken)
						.addHeader("payload-param", "?country=KR&sort=DESC&updatedSince=" + updateSince)
						.addHeader("timezone", "Asia/Seoul").build();

				response = okHttpClient.newCall(request).execute();

				responseMap = new ObjectMapper().readValue(response.body().byteStream(), HashMap.class);

				if("200".equals(responseMap.get("StatusCode"))) {
					JSONArray payload = (JSONArray) parser.parse(responseMap.get("Message").toString());

					System.out.println(payload.size());
					for (int i = 0; i < payload.size(); i++) {
						JSONObject tempObject = (JSONObject) payload.get(i);
						String parsingDate = parsingDateforhourMin((String) tempObject.get("placementDate"));

						JSONObject vendor = (JSONObject) tempObject.get("vendor");
						JSONObject delivery = (JSONObject) tempObject.get("delivery");
						System.out.println(parsingDate + " | " + tempObject.get("orderNumber") + " | " + vendor.get("accountId"));

						JSONObject resultobj = new JSONObject();
						resultobj.put("date", parsingDate);
						resultobj.put("orderNumber", tempObject.get("orderNumber"));
						resultobj.put("status", tempObject.get("status"));
						resultobj.put("accountId", vendor.get("accountId"));
						resultobj.put("deliveryDate", delivery.get("date"));
						resultobj.put("note", tempObject.get("note"));

						// Invoice 정보
						if (invoiceMap.get(tempObject.get("orderNumber")) != null) {
							resultobj.put("invoiceId", tempObject.get("orderNumber"));
							resultobj.put("total", invoiceMap.get(tempObject.get("orderNumber")));
						}

						// Account 정보
						if (accountMap.get(vendor.get("accountId")) != null) {
							resultobj.put("accountId", vendor.get("accountId"));
							resultobj.put("customer", accountMap.get(vendor.get("accountId")));
						}

						resultArray.add(resultobj);
					}

					excelDownload(resultArray, wsId);
				}
				
			}
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultArray;
	}


	/**
	 * UTC 시간 변환
	 * 
	 * @param pDate
	 * @return
	 */
	public String parsingDate(String pDate) {
		Date date;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.KOREA).parse(pDate);
			SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd");
			String parse = newDtFormat.format(date);

			return parse;
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * UTC 시간 변환 | 일시까지
	 * 
	 * @param pDate
	 * @return
	 */
	public String parsingDateforhourMin(String pDate) {
		Date date;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX", Locale.KOREA).parse(pDate);
			SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String parse = newDtFormat.format(date);

			return parse;
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 날짜 string -> UTC
	 * 
	 * @param pDate
	 * @return
	 */
	public String stringToUTCtime(String pDate) {
		Date date;
		try {
			date = new SimpleDateFormat("yyyyMMdd").parse(pDate);
			SimpleDateFormat newDtFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			String parse = newDtFormat.format(date);

			return parse;
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/*
	 * 엑셀 생성
	 */
	public void excelDownload(JSONArray dataArray, String wsId) {
		
		try (FileInputStream fis = new FileInputStream(configUtil.getProperty("EXCEL_PATH")+"template_invoice.xlsx")) {
			XSSFWorkbook workbook = new XSSFWorkbook(fis);

			int rowindex = 0;

			// 시트를 읽습니다.
			XSSFSheet sheet = workbook.getSheetAt(0);
			XSSFRow xssfRow = sheet.getRow(0); // .xlsx
			
			XSSFCell xssfCell = null;// .xlsx

			int rowIds = 1;
			// 행의 수를 체크
			//int rows = sheet.getPhysicalNumberOfRows();
			for (rowindex = 0; rowindex < dataArray.size(); rowindex++) {
				JSONObject jsonObj = (JSONObject)dataArray.get(rowindex);
				xssfRow = sheet.createRow(rowIds++);
				
				xssfCell = xssfRow.createCell(0);
				xssfCell.setCellValue((String)jsonObj.getOrDefault("date",""));

				xssfCell = xssfRow.createCell(1);
				xssfCell.setCellValue((String)jsonObj.getOrDefault("deliveryDate",""));

				xssfCell = xssfRow.createCell(2);
				xssfCell.setCellValue((String)jsonObj.getOrDefault("orderNumber",""));

				xssfCell = xssfRow.createCell(3);
				xssfCell.setCellValue((String)jsonObj.getOrDefault("accountId", "") + " | " + (String)jsonObj.getOrDefault("customer",""));
				
				xssfCell = xssfRow.createCell(4);
				xssfCell.setCellValue(Double.parseDouble(String.valueOf(jsonObj.getOrDefault("total","0"))));

				xssfCell = xssfRow.createCell(5);
				xssfCell.setCellValue((String)jsonObj.getOrDefault("status",""));

				xssfCell = xssfRow.createCell(6);
				xssfCell.setCellValue((String)jsonObj.getOrDefault("note",""));

			}

			//autoSizeColumns(sheet);
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

			File xlsFile = new File(configUtil.getProperty("EXCEL_PATH")+"주문정보_" + wsId + "_" +now.format(dtf) + ".xlsx");
			FileOutputStream fos = new FileOutputStream(xlsFile);
			workbook.write(fos);

			if (fis != null) fis.close();
			if (fos != null) fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	private void autoSizeColumns(Sheet sheet) {
		int rowCount = sheet.getPhysicalNumberOfRows();
		for (int i = 0; i < rowCount; i++) {
		   Row row = sheet.getRow(i);
		   Iterator<Cell> cellIterator = row.cellIterator();
		   while (cellIterator.hasNext()) {
			  Cell cell = cellIterator.next();
			  int columnIndex = cell.getColumnIndex();
			  sheet.autoSizeColumn(columnIndex);
		   }
		}
	 }

	/**
	 * 도매사 별 Account 리스트 조회
	 * @param wsID
	 * @return
	 */
	public String getAccountsService(String wsID) {
		
		JSONParser parser = new JSONParser();
		String resultAccounts = "";
		try {
			// Account 리스트 조회
			List<Monitor> accountExtracted = accountExtracted(wsID);
			for (int i = 0; i < accountExtracted.size(); i++) {
				Object object = parser.parse(accountExtracted.get(i).getDebugData());
				JSONObject jsonObject = (JSONObject) object;

				String jsonArrayString = jsonObject.get("payload").toString();
				JSONArray payload = (JSONArray) parser.parse(jsonArrayString);

				for (int j = 0; j < payload.size(); j++) {
					JSONObject obj = (JSONObject) payload.get(j);
					//JSONArray reArr = (JSONArray)obj.get("representatives");
					JSONObject owner = (JSONObject) obj.get("owner");

					if(owner != null) {
						// for(int k=0; k<reArr.size(); k++){
						// 	JSONObject reO = (JSONObject)reArr.get(k);
						// 	resultAccounts += (String)obj.get("accountId") + " | " 
						// 	+ (String)obj.get("customerAccountId") + " | " 
						// 	+ (String)obj.get("displayName") + " | " 
						// 	+ reO.getOrDefault("name","") + " | " 
						// 	+ reO.getOrDefault("phone","") + "<br/>";
						// }

						resultAccounts += (String)obj.get("accountId") + " | " 
							+ (String)obj.get("customerAccountId") + " | " 
							+ (String)obj.get("displayName") + " | " 
							+ owner.getOrDefault("firstName","") + " | " 
							+ owner.getOrDefault("phone","") + "<br/>";
					} else {
						resultAccounts += (String)obj.get("accountId") + " | " 
									+ (String)obj.get("customerAccountId") + " | " 
									+ (String)obj.get("displayName") + "<br/>";
					}	
				}
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultAccounts;
	}
}
