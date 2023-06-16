package com.reflex;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reflex.model.Topic;
import com.reflex.controller.TopicController;
import com.reflex.repository.TopicRepository;
import com.reflex.repository.UserRepository;
import com.reflex.security.jwt.JwtUtils;

import java.util.List;
import java.util.ArrayList;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"scripts.sql"})
class CandidateTestSystemBackendApplicationTests {
	
	@MockBean
	private TopicRepository topicRepository;
	
	@MockBean 
	private JwtUtils jwtUtils;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;	

	@Test
    public void shouldgetAllTopics () throws Exception
    {
			List<Topic> topicList = new ArrayList<>();
		    mockMvc.perform(get("/api/topic/all"))
		    	.andExpect(status().isOk())
		        .andDo(print());	    
		    assertEquals(topicList.size(),2);
    }

}
