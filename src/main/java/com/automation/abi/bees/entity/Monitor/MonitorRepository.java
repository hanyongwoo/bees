package com.automation.abi.bees.entity.Monitor;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MonitorRepository extends CrudRepository<Monitor, Long> {
    Monitor findByTxId(String tx_id);
    
    @Query(value = "select * from eai_trans_log where proc_date between :startDatetime and :endDatetime and if_id = :if_id and ws_id = :ws_id and step='END'", nativeQuery = true)
    List<Monitor> findListByWs(@Param("startDatetime") LocalDateTime startDatetime
                                ,@Param("endDatetime") LocalDateTime endDatetime
                                ,@Param("if_id") String if_id
                                ,@Param("ws_id") String ws_id);
                                
    // @Query("select * from eai_trans_log where proc_date between :startDatetime and :endDatetime and if_id = :if_id and ws_id != :ws_id and step='END'", nativeQuery = true)
    // List<Monitor> findByListWsIdNot(@Param("startDatetime") LocalDateTime startDatetime
    //                                 ,@Param("endDatetime") LocalDateTime endDatetime
    //                                 ,@Param("if_id") String if_id
    //                                 ,@Param("ws_id") String ws_id);
}