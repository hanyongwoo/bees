package com.automation.abi.bees.util;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWriter {
    
    private final Workbook workbook;
    private final Map<String, Object> data;
    private final HttpServletResponse response;

    // 생성자
    public ExcelWriter(Workbook workbook, Map<String, Object> data, HttpServletResponse response) {
        this.workbook = workbook;
        this.data = data;
        this.response = response;
    }
    
    // 엑셀 파일 생성
    public void create() {
        setFileName(response, mapToFileName());
        
        Sheet sheet = workbook.createSheet();

        createHead(sheet, mapToHeadList());

        createBody(sheet, mapToBodyList());
    }

    // 모델 객체에서 파일 이름 꺼내기
    private String mapToFileName() {
        return (String) data.get("filename");
    }

    // 모델 객체에서 헤더 이름 리스트 꺼내기
    @SuppressWarnings("unchecked")
    private List<String> mapToHeadList() {
        return (List<String>) data.get("head");
    }

    // 모델 객체에서 바디 데이터 리스트 꺼내기
    @SuppressWarnings("unchecked")
    private List<List<String>> mapToBodyList() {
        return (List<List<String>>) data.get("body");
    }

    // 파일 이름 지정
    private void setFileName(HttpServletResponse response, String fileName) {
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + getFileExtension(fileName) + "\"");
    }
    
    // 넘어온 뷰에 따라서 확장자 결정
    private String getFileExtension(String fileName) {
        if (workbook instanceof XSSFWorkbook) {
            fileName += ".xlsx";
        }
        if (workbook instanceof SXSSFWorkbook) {
            fileName += ".xlsx";
        }
        if (workbook instanceof HSSFWorkbook) {
            fileName += ".xls";
        }

        return fileName;
    }

    // 엑셀 헤더 생성
    private void createHead(Sheet sheet, List<String> headList) {
        createRow(sheet, headList, 0);
    }

    // 엑셀 바디 생성
    private void createBody(Sheet sheet, List<List<String>> bodyList) {
        int rowSize = bodyList.size();
        for (int i = 0; i < rowSize; i++) {
            createRow(sheet, bodyList.get(i), i + 1);
        }
    }

    // 행 생성
    private void createRow(Sheet sheet, List<String> cellList, int rowNum) {
        int size = cellList.size();
        Row row = sheet.createRow(rowNum);

        for (int i = 0; i < size; i++) {
            row.createCell(i).setCellValue(cellList.get(i));
        }
    }

    // 모델 객체에 담을 형태로 엑셀 데이터 생성
    public static Map<String, Object> createExcelData(List<? extends ExcelDto> data, Class<?> target, String ws_id) {
        Map<String, Object> excelData = new HashMap<>();
        excelData.put("filename", createFileName(ws_id));
        excelData.put("head", createHeaderName(target));
        excelData.put("body", createBodyData(data));
        return excelData;
    }

    // @ExcelColumnName에서 헤더 이름 리스트 생성
    private static List<String> createHeaderName(Class<?> header) {
        List<String> headData = new ArrayList<>();
        for (Field field : header.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(ExcelColumnName.class)) {
                String headerName = field.getAnnotation(ExcelColumnName.class).headerName();
                if (headerName.equals("")) {
                    headData.add(field.getName());
                } else {
                    headData.add(headerName);
                }
            }
        }
        return headData;
    }

    // @ExcelFileName 에서 엑셀 파일 이름 생성
    private static String createFileName(String wsId) {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        return "orderinfo_" + wsId + "_" + now.format(dtf);
        //resultArray.add(resultobj);
    }

    // 데이터 리스트 형태로 가공
    private static List<List<String>> createBodyData(List<? extends ExcelDto> dataList) {
        List<List<String>> bodyData = new ArrayList<>();
        dataList.forEach(v -> bodyData.add(v.mapToList()));
        return bodyData;
    }
}
