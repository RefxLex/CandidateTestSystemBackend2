package com.reflex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.reflex.model.Topic;
import com.reflex.repository.TopicRepository;
import com.reflex.repository.UserRepository;

@SpringBootTest
class CandidateTestSystemBackendApplicationTests {
	


	@Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }

}
