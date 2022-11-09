package com.automation.abi.bees.entity.Monitor;

import lombok.Data;

@Data
public class DailyReport {
    
    private String date;
    private String wholeSaler;
    private int creationAccount;
    private int orderCountPlaced;
    private int orderCountCompleted;
    private int orderCountDelivered;
    private int orderCountCancelled;
    private int numberOfnonABI;
    private int numberOfABI;
    private int revenue;
}
