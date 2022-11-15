package com.automation.abi.bees.util;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractXlsView;

public abstract class AbstractXlsxView extends AbstractXlsView {
    
    public AbstractXlsxView(){
        setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    @Override
    protected Workbook createWorkbook(Map<String, Object> model, HttpServletRequest request) {
        // TODO Auto-generated method stub
        return new XSSFWorkbook();
    }
}
