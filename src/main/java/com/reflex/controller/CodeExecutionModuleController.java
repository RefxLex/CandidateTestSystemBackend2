package com.reflex.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.GregorianCalendar;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
import com.reflex.model.User;
import com.reflex.model.UserTask;
import com.reflex.model.UserTaskResult;
import com.reflex.repository.UserRepository;
import com.reflex.repository.UserTaskRepository;
import com.reflex.repository.UserTaskResultRepository;
import com.reflex.request.SubmissionRequest;
import com.reflex.request.UserTaskRequest;
import com.reflex.response.LanguageResponse;
import com.reflex.response.SubmissionResultResponse;
import com.reflex.response.SubmissionTokenResponse;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
//@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/exec-module")
public class CodeExecutionModuleController {
	
	@Value("${execModuleUrl}")
	private String baseUrl;
	
	private static final int timeout=5000;
	
	@Autowired
	UserTaskRepository userTaskRepository;
	
	@Autowired
	UserTaskResultRepository userTaskResultRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@GetMapping("/languages")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
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
	
	@GetMapping("/submission/{id}")
	public ResponseEntity<UserTask> getSubmission(@PathVariable("id") Long userTaskId){
		Optional<UserTask> userTask = userTaskRepository.findById(userTaskId);
		if(userTask.isPresent()==false) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user_task found with id=" + userTaskId);
		}
		return new ResponseEntity<>(userTask.get(), HttpStatus.OK);
	}
	
	@PutMapping("/submission/{id}")
	@PreAuthorize("hasRole('USER')")
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
	public ResponseEntity<UserTask> updateSubmissionResult(@PathVariable("id") Long userTaskId) throws IOException{
		Optional<UserTask> oldUserTask = userTaskRepository.findById(userTaskId);
		if(oldUserTask.isPresent()==false) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user_task found with id=" + userTaskId);
		}
		UserTask newUserTask = oldUserTask.get();
		
		RequestConfig requestConfig = RequestConfig.custom().
			    setConnectionRequestTimeout(timeout).setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig);
		ObjectMapper mapper = new ObjectMapper();
		
		for(UserTaskResult iterator: newUserTask.getUserTaskResult()) {
			
			HttpGet request = new HttpGet(baseUrl + "/submissions/" + iterator.getSubmissionToken());
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
	             CloseableHttpResponse response = httpClient.execute(request)) {

	            HttpEntity entity = response.getEntity();
	            if (entity != null) {
	            	String result = EntityUtils.toString(entity);
	            	SubmissionResultResponse resultResponse = mapper.readValue(result, SubmissionResultResponse.class);
	            	iterator.setStdout(resultResponse.getStdout());
	            	iterator.setStderr(resultResponse.getStderr());
	            	iterator.setCompile_output(resultResponse.getCompile_output());
	            	iterator.setMessage(resultResponse.getMessage());
	            	iterator.setExit_code(resultResponse.getExit_code());
	            	iterator.setExit_signal(resultResponse.getExit_signal());
	            	iterator.setStatus(resultResponse.getStatus().getDescription());
	            	iterator.setCreated_at(resultResponse.getCreated_at());
	            	iterator.setFinished_at(resultResponse.getFinished_at());
	            	iterator.setTime(resultResponse.getTime());
	            	iterator.setWall_time(resultResponse.getWall_time());
	            	iterator.setMemory(resultResponse.getMemory());
	            }    
	        }	
	    }
		
		// check if tests are passed
		int testsPassed=0;
		int testsFailed=0;
		String stdout;
		String expectedOutput;
		for(UserTaskResult iterator: newUserTask.getUserTaskResult()) {
			stdout = iterator.getStdout();
			expectedOutput = iterator.getTaskTestInput().getOutput();
			if(expectedOutput.equals(stdout)) {
				testsPassed = testsPassed + 1;
			}
			else {
				testsFailed = testsFailed + 1;
			}	
		}
		newUserTask.setTestsPassed(testsPassed);
		newUserTask.setTestFailed(testsFailed);
		
	// calculate user score
		User user = newUserTask.getUser();
		Instant lastTaskAssingDate = newUserTask.getAssignDate();
		Instant nextDay = lastTaskAssingDate.plusSeconds(24*60*60);
		
		// include tests from current task first
		int allTestsSum=newUserTask.getOverallTestsCount();
		int allPassedTestsSum=testsPassed;
		
		// then check for tasks assigned in same day as current task
		List<UserTask> userTaskList = userTaskRepository.selectByLastAssingDate(lastTaskAssingDate, nextDay);
		if(userTaskList.isEmpty()==false) {
			for(UserTask iterator: userTaskList) {
				allTestsSum = allTestsSum + iterator.getOverallTestsCount();
				allPassedTestsSum = allPassedTestsSum + iterator.getTestsPassed();
			}
		}
		String userScore = ((Integer) Math.round((allPassedTestsSum / allTestsSum)*100)).toString();
		user.setLastScore(userScore + "%");
		userRepository.save(user);
		return new ResponseEntity<>(userTaskRepository.save(newUserTask), HttpStatus.OK);
	}
	
		
}
