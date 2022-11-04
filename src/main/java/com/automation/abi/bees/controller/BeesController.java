package com.automation.abi.bees.controller;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.automation.abi.bees.conf.ConfigUtil;
import com.automation.abi.bees.service.BeesService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("ob")
public class BeesController {

    @Autowired
    private BeesService beesService;

    private final ConfigUtil configUtil;


    @GetMapping("/invoice")
    public ResponseEntity<?> getInvoice(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate) throws ParseException{
        configUtil.getProperty("UAT.KCON.URL");
        return ResponseEntity.ok().body(beesService.invoiceService(startDate, endDate));
    }

    @GetMapping("/itemDiff")
    public ResponseEntity<?> getItemDiff(@RequestParam(value = "startDate") String date, @RequestParam(value = "endDate") String endDate, @RequestParam(value = "wsId") String wsId) throws ParseException{
        return ResponseEntity.ok().body(beesService.itemDiff(date, wsId));
    }

    @GetMapping("/getOrder")
    public ResponseEntity<?> getItemDiff(@RequestParam(value = "updateSince") String date, HttpServletRequest request) throws ParseException {
        return ResponseEntity.ok().body(beesService.getOrderInfoArrangement(date));
    }
}