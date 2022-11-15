package com.automation.abi.bees.controller;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.automation.abi.bees.service.BeesService;
import com.automation.abi.bees.util.ExcelService;
import com.automation.abi.bees.util.ExcelXlsxView;


@RestController
@RequestMapping("ob")
public class BeesController {

    @Autowired
    private BeesService beesService;

    @Autowired
    private ExcelService excelService;


    @GetMapping("/accounts")
    public ResponseEntity<?> getAccounts(@RequestParam(value = "wsId") String wsId) throws ParseException{
        return ResponseEntity.ok().body(beesService.getAccountsService(wsId));
    }

    @GetMapping("/invoice")
    public ResponseEntity<?> getInvoice(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) throws ParseException{
        return ResponseEntity.ok().body(beesService.invoiceService(startDate, endDate));
    }

    @GetMapping("/itemDiff")
    public ResponseEntity<?> getItemDiff(@RequestParam(value = "startDate") String date, @RequestParam(value = "wsId") String wsId) throws ParseException{
        return ResponseEntity.ok().body(beesService.itemDiff(date, wsId));
    }

    @GetMapping("/getOrder")
    public ResponseEntity<?> getItemDiff(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate, @RequestParam(value = "wsId") String wsId, HttpServletRequest request) throws ParseException {
        return ResponseEntity.ok().body(beesService.getOrderInfoArrangement(startDate, endDate, wsId));
    }

    @GetMapping(value="/file/download")
    public ModelAndView fileDownload(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate, @RequestParam(value = "wsId") String wsId, HttpServletRequest request) throws ParseException, IOException {

        Map<String, Object> excelData = excelService.excelDownload(request, startDate, endDate, wsId);
        return new ModelAndView(new ExcelXlsxView(), excelData);
    }
}