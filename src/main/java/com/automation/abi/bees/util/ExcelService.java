package com.automation.abi.bees.util;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.automation.abi.bees.service.BeesService;

@Service
public class ExcelService {

    @Autowired
    private BeesService beesService;

    public Map<String, Object> excelDownload(HttpServletRequest request, String startDate, String endDate, String wsId) {

        try {
            List<OrderInvoice> list = beesService.getOrderInfoArrangement(startDate,endDate,wsId);
            return ExcelWriter.createExcelData(list, OrderInvoice.class, wsId);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
