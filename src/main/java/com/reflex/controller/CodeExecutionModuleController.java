package com.reflex.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reflex.model.TaskTestInput;
import com.reflex.model.UserTask;
import com.reflex.model.UserTaskResult;
import com.reflex.repository.UserTaskRepository;
import com.reflex.repository.UserTaskResultRepository;
import com.reflex.request.SubmissionRequest;
import com.reflex.request.UserTaskRequest;
import com.reflex.response.LanguageResponse;
import com.reflex.response.SubmissionResultResponse;
import com.reflex.response.SubmissionTokenResponse;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/exec-module")
public class CodeExecutionModuleController {
	
	@Value("${execModuleUrl}")
	private String baseUrl;
	
	private static final int timeout=5000;
	
	@Autowired
	UserTaskRepository userTaskRepository;
	
	@Autowired
	UserTaskResultRepository userTaskResultRepository;
	
	@GetMapping("/languages")
	public ResponseEntity<List<LanguageResponse>> getSupportedLanguages(){
			
		RequestConfig requestConfig = RequestConfig.custom().
			    setConnectionRequestTimeout(timeout).setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig);
		
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<LanguageResponse> filteredList = new ArrayList<>();
        try {
            HttpGet request = new HttpGet(baseUrl + "/languages/all");
            CloseableHttpResponse response = httpClient.execute(request);
	            try {    
	                HttpEntity entity = response.getEntity();
	                if (entity != null) {
	                    String result = EntityUtils.toString(entity);
	                    ObjectMapper mapper = new ObjectMapper();
	                    List<LanguageResponse> languageList = Arrays.asList(mapper.readValue(result, LanguageResponse[].class));
	                    for(int i=0; i<languageList.size(); i++) {                    	
	                    	if(languageList.get(i).isIs_archived()==false) {		
	                    		filteredList.add(languageList.get(i));
	                    	}
	                    }
	                }
	                response.close();
	            }catch(IOException e) {
	            	throw new ResponseStatusException(HttpStatusCode.valueOf(response.getStatusLine().getStatusCode()),"Internal API error");
	            }
	        httpClient.close();
        	}catch(IOException e) {
        		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Error on httpClient close");
        	}
        return new ResponseEntity<>(filteredList, HttpStatus.OK);
	}
	@PutMapping("/submission/{id}")
	public ResponseEntity<?> createSubmission(@PathVariable ("id") Long userTaskId,
			@RequestBody UserTaskRequest userTaskRequest) throws IOException{
		Optional<UserTask> oldUserTask = userTaskRepository.findById(userTaskId);
		if(oldUserTask.isPresent()==false) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user_task found with id=" + userTaskId);
		}
		UserTask newUserTask = oldUserTask.get();
		newUserTask.setCode(userTaskRequest.getCode());
		newUserTask.setSubmitDate(Instant.now());

		// get input values list for exec module as array
		Set<TaskTestInput> taskTestInputSet = new HashSet<>();
		taskTestInputSet = oldUserTask.get().getTask().getTaskTestInput();
		List<TaskTestInput> convertedInputList = new ArrayList<>();
		for(TaskTestInput iterator: taskTestInputSet) {
			convertedInputList.add(iterator);
		}
		
		RequestConfig requestConfig = RequestConfig.custom().
			    setConnectionRequestTimeout(timeout).setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig);
		
		String result="";
		ObjectMapper mapper = new ObjectMapper();
		SubmissionRequest request = new SubmissionRequest(userTaskRequest.getCode(), oldUserTask.get().getTaskCodeLanguageId());
		for(int i=0; i<convertedInputList.size(); i++) {
			
			// set input base64
			request.setStdin(Base64.getEncoder().encodeToString((convertedInputList.get(i).getInput()).getBytes()));
			String requestJSON = mapper.writeValueAsString(request);
	        HttpPost post = new HttpPost(baseUrl + "/submissions/?base64_encoded=true&wait=false");
	        post.addHeader("content-type", "application/json");
	        post.setEntity(new StringEntity(requestJSON));

	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
	             CloseableHttpResponse response = httpClient.execute(post)) {
	            result = EntityUtils.toString(response.getEntity());
	            SubmissionTokenResponse tokenResponse = mapper.readValue(result, SubmissionTokenResponse.class);
	            UserTaskResult userTaskResult = new UserTaskResult(convertedInputList.get(i),tokenResponse.getToken());
	            newUserTask.getUserTaskResult().add(userTaskResult);
	            
	            // TODO: add retry? or one-time submit?
	            
	        }
		}
		// save user code and token
		userTaskRepository.save(newUserTask);
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	
	@PutMapping("/submission/result/{id}")
	@Transactional
	public ResponseEntity<UserTask> getSubmissionResult(@PathVariable("id") Long userTaskId) throws IOException{
		Optional<UserTask> oldUserTask = userTaskRepository.findById(userTaskId);
		if(oldUserTask.isPresent()==false) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user_task found with id=" + userTaskId);
		}
		
		List<UserTaskResult> userTaskResultList = userTaskResultRepository.selectByUserTaskId(userTaskId);
		
		RequestConfig requestConfig = RequestConfig.custom().
			    setConnectionRequestTimeout(timeout).setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig);
		ObjectMapper mapper = new ObjectMapper();
		
		for(int i=0; i<userTaskResultList.size(); i++) {
			
			HttpGet request = new HttpGet(baseUrl + "/submissions/" + userTaskResultList.get(i).getSubmissionToken());
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
	             CloseableHttpResponse response = httpClient.execute(request)) {

	            HttpEntity entity = response.getEntity();
	            if (entity != null) {
	            	String result = EntityUtils.toString(entity);
	            	SubmissionResultResponse resultResponse = mapper.readValue(result, SubmissionResultResponse.class);
	            	userTaskResultList.get(i).setStdout(resultResponse.getStdout());
	            	userTaskResultList.get(i).setStderr(resultResponse.getStderr());
	            	userTaskResultList.get(i).setCompile_output(resultResponse.getCompile_output());
	            	userTaskResultList.get(i).setMessage(resultResponse.getMessage());
	            	userTaskResultList.get(i).setExit_code(resultResponse.getExit_code());
	            	userTaskResultList.get(i).setExit_signal(resultResponse.getExit_signal());
	            	userTaskResultList.get(i).setStatus(resultResponse.getStatus().getDescription());
	            	userTaskResultList.get(i).setCreated_at(resultResponse.getCreated_at());
	            	userTaskResultList.get(i).setFinished_at(resultResponse.getFinished_at());
	            	userTaskResultList.get(i).setTime(resultResponse.getTime());
	            	userTaskResultList.get(i).setWall_time(resultResponse.getWall_time());
	            	userTaskResultList.get(i).setMemory(resultResponse.getMemory());
	            }    
	        }	
	    }
		userTaskResultRepository.saveAll(userTaskResultList);
		Optional<UserTask> newUserTask = userTaskRepository.findById(userTaskId);
		return new ResponseEntity<>(newUserTask.get(), HttpStatus.OK);		
	}
	
		
}
