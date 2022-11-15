package com.automation.abi.bees.service;

public enum InterfaceId {

    INVOICE_POST("INVOICE_KBON_0001"),
    ITEM_POST("ITEM_KBON_0001"),
    PRICE_POST("PRICE_KBON_0001"),
    ACCOUNT_POST("ACCOUNT_KBON_0001");

    private final String value;

    InterfaceId(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}