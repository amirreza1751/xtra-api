package com.xtra.api;

import com.xtra.api.controller.LineController;
import com.xtra.api.model.Line;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests {

	@Autowired
	private LineController lineController;
	@Test
	void contextLoads() {
	}

}
