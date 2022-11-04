package com.automation.abi.bees;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.automation.abi.bees.entity.Monitor.MonitorRepository;

@SpringBootTest
class BeesApplicationTests {

	@Autowired
	private MonitorRepository monitorRepository;

	@Test
	void contextLoads() {
		System.out.println(1);
		assertEquals("1", monitorRepository.findByTxId("202210241511282KBONdelvywin_kbon_0001.004"));
	}

}
