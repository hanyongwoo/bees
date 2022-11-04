package com.automation.abi.bees.runner;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class postgreSQLRunner implements ApplicationRunner{

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO Auto-generated method stub
        try (Connection connection = dataSource.getConnection()){
            System.out.println("dataSource1 : " + dataSource.getClass());
            System.out.println("dataSource1 : " +connection.getMetaData().getURL());
            System.out.println("dataSource1 : " +connection.getMetaData().getUserName());
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}