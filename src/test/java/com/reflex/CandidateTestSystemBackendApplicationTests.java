package com.reflex;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.reflex.repository.UserRepository;

@SpringBootTest
class CandidateTestSystemBackendApplicationTests {
	
	@Autowired
	UserRepository userRepository;

	@Test
	void contextLoads() {
	}
	
	/*
	@Test
	@Sql({"/data.sql"})
	public void testLoadDataForTestCase() {
	    assertTrue(userRepository.existsById((long)1));
	} */

}
