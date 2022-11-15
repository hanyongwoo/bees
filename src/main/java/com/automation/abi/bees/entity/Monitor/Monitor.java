package com.automation.abi.bees.entity.Monitor;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;

@Entity(name = "eai_trans_log")
@Data
public class Monitor {
    
    @Id
    private String txId;
    private String txUrl;
    private String txDatetime;
    private String ifId;
    private String wsId;
    private LocalDateTime procDate;
    private String dataCnt;
    private String debugData;
    private String step;
    
}
