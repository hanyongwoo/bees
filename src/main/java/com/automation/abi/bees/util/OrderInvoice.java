package com.automation.abi.bees.util;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ExcelFileName
public class OrderInvoice implements ExcelDto{
    
    @ExcelColumnName
    @JsonProperty("date")
    private String orderDate;

    @ExcelColumnName
    @JsonProperty("deliveryDate")
    private String deliveryDate;

    @ExcelColumnName
    @JsonProperty("orderNumber")
    private String orderNumber;

    @ExcelColumnName
    private String accountId;

    @ExcelColumnName
    private String customer;

    @ExcelColumnName
    private String actureInvoiceTnt;

    @ExcelColumnName
    private String status;

    @ExcelColumnName
    private String note;
    

    @Override
    public List<String> mapToList() {
        // TODO Auto-generated method stub
        return Arrays.asList(orderDate, deliveryDate, orderNumber, accountId, customer, actureInvoiceTnt, status, note);
    }
    
}
