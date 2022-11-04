package com.automation.abi.bees.runner;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

import com.automation.abi.bees.entity.Monitor.Monitor;
import com.automation.abi.bees.entity.Monitor.MonitorRepository;

public class InvoiceAuto {

	@Autowired
	private static MonitorRepository monitorRepository;

    public static void main(String[] args) throws ParseException, org.json.simple.parser.ParseException {
		
    }

	@Bean
	public CommandLineRunner demo(MonitorRepository monitorRepository) {
		return (args)-> {

			//Monitor m = monitorRepository.findByTxId("202210241511282KBONdelvywin_kbon_0001.004");

			String if_id = "INVOICE_KBON_0001";
			String ws_id = "2148665741";

			LocalDateTime startDatetime = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.of(0,0,0));
 			LocalDateTime endDatetime = LocalDateTime.of(LocalDate.now(), LocalTime.of(23,59,59));

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
					// Invoice 번호 | Account ID | total
					JSONObject vendor = (JSONObject)obj.get("vendor");
					System.out.println(vendor.get("invoiceId") + " | " + vendor.get("accountId") + " | " + obj.get("total"));
				}
			}
			
		};

	}
}
