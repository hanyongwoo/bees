package com.automation.abi.bees.entity.Monitor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface MonitorRepository extends CrudRepository<Monitor, Long> {
    Monitor findByTxId(String tx_id);
    
    List<Monitor> findByProcDateBetweenAndIfIdAndWsId(LocalDateTime startDatetime, LocalDateTime endDatetime, String if_id, String ws_id);
    List<Monitor> findByProcDateBetweenAndIfIdAndWsIdNot(LocalDateTime startDatetime, LocalDateTime endDatetime, String if_id, String ws_id);
}