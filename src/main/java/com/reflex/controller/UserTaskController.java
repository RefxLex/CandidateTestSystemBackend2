package com.reflex.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import java.util.concurrent.CompletableFuture;
import java.util.Map;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reflex.model.ExecModulePort;
import com.reflex.model.Task;
import com.reflex.model.TaskUnitTest;
import com.reflex.model.User;
import com.reflex.model.UserTask;
import com.reflex.model.enums.UserStatus;
import com.reflex.repository.ExecModulePortRepository;
import com.reflex.repository.TaskRepository;
import com.reflex.repository.UserRepository;
import com.reflex.repository.UserTaskRepository;
import com.reflex.request.CreateUserTaskRequest;
import com.reflex.request.SolutionRequest;
import com.reflex.request.TestLaunchUserTaskRequest;
import com.reflex.request.UnitTestRequest;
import com.reflex.request.UpdateCommentRequest;
import com.reflex.response.UnitTestResultResponse;
import com.reflex.request.CompleteUserTaskRequest;
import com.reflex.model.UserTaskSolution;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;

@CrossOrigin
@RestController
@RequestMapping("/api/user-task")
public class UserTaskController {
	
	@Autowired
	UserTaskRepository userTaskRepository;
	
	@Autowired
	TaskRepository taskRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ExecModulePortRepository execModulePortRepository;
	
	@Value("${execModuleUrl}")
	private String execModuleUrl;
	
	@Value("${execModulePortPoolStartIndex}")
	private String execModulePortPoolStartIndex;
	
	private static final int timeout=15000;
	
	@GetMapping("/{id}")
	public ResponseEntity<UserTask> getUserTask(@PathVariable ("id") Long userTaskId){
		Optional <UserTask> userTask = userTaskRepository.findById(userTaskId);
		if(userTask.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user task found with id=" + userTaskId);
		}
		return new ResponseEntity<>(userTask.get(), HttpStatus.OK);
	}
	
	@GetMapping("/find/{userId}")
	public ResponseEntity<List<UserTask>> getUserTasksByUserId(@PathVariable ("userId") Long userId){
		List<UserTask> userTasksList = new ArrayList<>();
		userTasksList = userTaskRepository.selectByUserId(userId);
		return new ResponseEntity<>(userTasksList, HttpStatus.OK);
	}
	
	@PostMapping("/{userId}")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	@Transactional
	public ResponseEntity<?> createUserTask(@PathVariable("userId") Long userId, 
			 @Valid @RequestBody List<CreateUserTaskRequest> userTaskRequestList) {
		
		List<UserTask> userTaskList = new ArrayList<>();
		for(CreateUserTaskRequest userTaskRequest: userTaskRequestList) {
			
			Optional<Task> task = taskRepository.findById(userTaskRequest.getTaskId());
			if(task.isPresent()==false) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No task found with taskId=" + userTaskRequest.getTaskId());
			}
			Optional<User> user = userRepository.findById(userId);
			if(user.isPresent()==false) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with userId=" + userId);
			}
			
			user.get().setUserStatus((UserStatus.started).toString());
			Instant assignDate = Instant.now();
			UserTask newUserTask = new UserTask(user.get(), task.get(), assignDate);
			userTaskList.add(newUserTask);
		}
		userTaskRepository.saveAll(userTaskList);
		return new ResponseEntity<>(null, HttpStatus.CREATED);
	}
	
	@PutMapping("/start/{id}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<UserTask> startTask(@PathVariable ("id") Long userTaskId){
		Optional<UserTask> oldUserTask = userTaskRepository.findById(userTaskId);
		if(oldUserTask.isPresent()==false) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No userTask found with id=" + userTaskId);
		}
		if(oldUserTask.get().getStartDate()==null) {
			UserTask newUserTask = oldUserTask.get();
			newUserTask.setStartDate(Instant.now());
			userTaskRepository.save(newUserTask);
		}
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<UserTask> updateUserTask(@PathVariable("id") Long userTaskId,
			@Valid @RequestBody UpdateCommentRequest updateCommentRequest){
		Optional<UserTask> oldUserTask = userTaskRepository.findById(userTaskId);
		if(oldUserTask.isPresent()==false) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No userTask found with id=" + userTaskId);
		}
		UserTask newUserTask = oldUserTask.get();
		newUserTask.setComment(updateCommentRequest.getComment());
		return new ResponseEntity<>(userTaskRepository.save(newUserTask), HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> deleteUserTask(@PathVariable("id") Long userTaskId){
		Optional<UserTask> userTask = userTaskRepository.findById(userTaskId);
		if(userTask.isPresent()==false) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No userTask found with id=" + userTaskId);
		}
		userTaskRepository.deleteById(userTaskId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	@PostMapping("/test-launch")
	public ResponseEntity<?> runUserUnitTest(@Valid @RequestBody TestLaunchUserTaskRequest userTaskRequest){
		
		// define OS
		String fs = System.getProperty("file.separator");
    	boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    	boolean isLinux = System.getProperty("os.name").toLowerCase().startsWith("linux");
		ProcessBuilder processBuilder = new ProcessBuilder();
		
		// bind port
		Long port = rememberPort();
		
		// create docker container
	    if (isLinux){
	    	processBuilder.directory(new File(System.getProperty("user.home")));
	    	processBuilder.command("sh", "-c","docker container run --rm --name cts-server-1 -p " + port + ":8083 -d refxlexj/ctsmodule:0.1");
	    	String dockerOutput = runProcess(processBuilder);
	    	System.out.println("started container with id=" + dockerOutput + "and port=" + port);
	    }
		
		RequestConfig requestConfig = RequestConfig.custom().
			    setConnectionRequestTimeout(timeout).setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig);
		
		String result="";
		UnitTestResultResponse unitTestResult = new UnitTestResultResponse();
		ObjectMapper mapper = new ObjectMapper();
		String requestJSON = "";
		HttpPost post = new HttpPost(execModuleUrl + ":" + port + "/api/exec-module/test-launch");
		post.addHeader("content-type", "application/json");
		try {
			requestJSON = mapper.writeValueAsString(userTaskRequest);
			post.setEntity(new StringEntity(requestJSON));
		}
		catch(JsonProcessingException | UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
     
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
	             CloseableHttpResponse response = httpClient.execute(post)) {	        	
	        	int statusCode = response.getStatusLine().getStatusCode();
	        	if(statusCode == 200) {		        	
		            result = EntityUtils.toString(response.getEntity());
		            unitTestResult = mapper.readValue(result, UnitTestResultResponse.class);
	        	}
	        	else {
	        		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exec module error" + response.getStatusLine().getReasonPhrase());
	        	}
	                        
	    }
	    catch(HttpHostConnectException exception) {
	        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error connecting to exec module");
	    } catch (IOException e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
        
        String testsResult="";
        for(String iterator: unitTestResult.getReport()) {
        	testsResult = testsResult + iterator;
        }
        Map<String, Object> response = new HashMap<>();
        String encodedtestResult = Base64.getEncoder().encodeToString(testsResult.getBytes(StandardCharsets.ISO_8859_1));
        response.put("result", encodedtestResult);	  
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PutMapping("/complete/{id}")
	public ResponseEntity<?> runAllUnitTests(@PathVariable("id") Long userTaskId, @Valid @RequestBody CompleteUserTaskRequest userTaskRequest){
		
		Optional<UserTask> userTask = userTaskRepository.findById(userTaskId);
		if(userTask.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user task found with id=" + userTaskId);
		}	
		UserTask newUserTask = userTask.get();
		
		if(newUserTask.isCompleted()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task already completed");
		}
		else {
			
			// define OS
			String fs = System.getProperty("file.separator");
	    	boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
	    	boolean isLinux = System.getProperty("os.name").toLowerCase().startsWith("linux");
			ProcessBuilder processBuilder = new ProcessBuilder();
			
			// create docker container
		    if (isLinux){
		    	processBuilder.directory(new File(System.getProperty("user.home")));
		    	processBuilder.command("sh", "-c","docker container run --rm --name cts-server-1 -p 8083:8083 -d refxlexj/ctsmodule:0.1");
		    	String dockerOutput = runProcess(processBuilder);
		    	System.out.println("started container with id=" + dockerOutput);
		    }
					
			List<SolutionRequest> solutionFilesList = new ArrayList<>();
			List<UnitTestRequest> unitTestsList = new ArrayList<>();
			newUserTask.getUserTaskSolution().clear();
			for(SolutionRequest iterator: userTaskRequest.getSolution()) {
				UserTaskSolution userTaskSol = new UserTaskSolution(iterator.getCode());
				newUserTask.getUserTaskSolution().add(userTaskSol);
				solutionFilesList.add(iterator);
			}
			for(TaskUnitTest iterator: newUserTask.getTask().getUnitTest()) {
				UnitTestRequest unitTest = new UnitTestRequest();
				unitTest.setCode(iterator.getCode());
				unitTestsList.add(unitTest);
			}
			TestLaunchUserTaskRequest execModuleRequest = new TestLaunchUserTaskRequest();
			execModuleRequest.setSolution(solutionFilesList);
			execModuleRequest.setUnitTest(unitTestsList);
			
			RequestConfig requestConfig = RequestConfig.custom().
				    setConnectionRequestTimeout(timeout).setConnectTimeout(timeout).setSocketTimeout(timeout).build();
			HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig);
			
			String result="";
			UnitTestResultResponse unitTestResult = new UnitTestResultResponse();
			ObjectMapper mapper = new ObjectMapper();
			String requestJSON = "";
			HttpPost post = new HttpPost(execModuleUrl + ":8083" + "/api/exec-module/test-launch");
			post.addHeader("content-type", "application/json");
			try {
				requestJSON = mapper.writeValueAsString(execModuleRequest);
				post.setEntity(new StringEntity(requestJSON));
			}
			catch(JsonProcessingException | UnsupportedEncodingException e) {
				e.printStackTrace();
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
	     
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		             CloseableHttpResponse response = httpClient.execute(post)) {	        	
		        	int statusCode = response.getStatusLine().getStatusCode();
		        	if(statusCode == 200) {		        	
			            result = EntityUtils.toString(response.getEntity());
			            unitTestResult = mapper.readValue(result, UnitTestResultResponse.class);
		        	}
		        	else {
		        		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exec module error" + response.getStatusLine().getReasonPhrase());
		        	}
		                        
		    }
		    catch(HttpHostConnectException exception) {
		        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error connecting to exec module");
		    } catch (IOException e) {
				e.printStackTrace();
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			
		 // parse test results  
		    String encodedTestResult="";
		    for(String iterator: unitTestResult.getReport()) {
		    	encodedTestResult = encodedTestResult + Base64.getEncoder().encodeToString(iterator.getBytes(StandardCharsets.ISO_8859_1));
		    }
		    if(unitTestResult.compileIsOk()) {			                         
		    	Integer testsPassed=0;
			    Integer testsFailed=0;
			    Integer overallTestCount = 0;
			    int foundIndex = 0;
			    int failedIndex = 0;
			    int successfulIndex = 0;
			    for(String iterator: unitTestResult.getReport()) {
			    	foundIndex = iterator.indexOf("tests found");
			    	failedIndex = iterator.indexOf("tests failed");
			    	successfulIndex = iterator.indexOf("tests successful");
			    	overallTestCount = overallTestCount + Integer.parseInt(iterator.substring(foundIndex-2,foundIndex-1));
			    	testsFailed = testsFailed + Integer.parseInt(iterator.substring(failedIndex-2,failedIndex-1));
			    	testsPassed = testsPassed + Integer.parseInt(iterator.substring(successfulIndex-2,successfulIndex-1));
			    }
			    newUserTask.setTestsPassed(testsPassed);
			    newUserTask.setTestsFailed(testsFailed);
			    newUserTask.setOverallTestsCount(overallTestCount);
			    newUserTask.setCompilationResult("OK");
			    newUserTask.setResultReport(encodedTestResult);
		    } else {
		    	
	        	// number of tests unknown, set failure rate to 100%
		     	newUserTask.setTestsPassed(0);
		     	newUserTask.setTestsFailed(1);
	        	newUserTask.setOverallTestsCount(1);
	        	newUserTask.setCompilationResult("FAIL");
	        	newUserTask.setResultReport(encodedTestResult);
		    }
	    
	     // check if user done all tasks
	     	boolean tasksDone=true;
	     	List<UserTask> userTasks = userTaskRepository.selectByUserId(newUserTask.getUser().getId());
	     	for (UserTask iterator: userTasks) {
	     		if(iterator.getSubmitDate()==null) {
	     			tasksDone = false;
	     		}
	     	}
	     	if (tasksDone) {
	     		newUserTask.getUser().setUserStatus((UserStatus.submitted).toString());
	     	}
	     				
	     // calc time spent
			Instant submitDate = Instant.now();
			newUserTask.setSubmitDate(submitDate);
	     	String timeSpent="";
	     	double minutesSpent = 0;
	     	double hoursSpent = 0;
	     	double daysSpent = 0;
	     	double secondsSpent = (double) (submitDate.getEpochSecond() - newUserTask.getStartDate().getEpochSecond());
	     	if (secondsSpent > 60) {
	     		minutesSpent = Math.floor(secondsSpent/60);
	     	}
	     	if (minutesSpent > 60) {
	     		hoursSpent = Math.floor(minutesSpent/60);
	     	}
	     	if (hoursSpent > 24) {
	     		daysSpent = Math.floor(hoursSpent/24);
	     	}
	     			
	     		
	     	if (daysSpent > 0) {
	     		timeSpent = timeSpent + ((Long)Math.round(daysSpent)).toString() + "d ";
	     	}
	     	if (hoursSpent > 0) {
	     		timeSpent = timeSpent + ((Long)Math.round(hoursSpent - daysSpent*24)).toString() + "h ";
	     	}
	     	if (minutesSpent > 0) {
	     		timeSpent = timeSpent + ((Long)Math.round(minutesSpent - hoursSpent*60)).toString() + "m ";
	     	}
	     	if (secondsSpent > 0) {
	     		timeSpent = timeSpent + ((Long)Math.round(secondsSpent - minutesSpent*60)).toString() + "s";
	     	}	     							
	     	newUserTask.setTimeSpent(timeSpent);
	     	newUserTask.getUser().setLastActivity(Date.from(Instant.now()));
	     			
	     // calculate user score			
	     	Instant lastTaskAssingDate = newUserTask.getAssignDate();
	     	Instant nextDay = lastTaskAssingDate.plusSeconds(24*60*60);
	     			
	     	// include tests from current task first
	     	int allTestsSum=newUserTask.getOverallTestsCount();
	     	int allPassedTestsSum=newUserTask.getTestsPassed();
	     			
	     	// then check for tasks assigned in same day as current task
	     	List<UserTask> userTaskList = userTaskRepository.selectByLastAssingDate(lastTaskAssingDate, nextDay);
	     	if(userTaskList.isEmpty()==false) {
	     		for(UserTask iterator: userTaskList) {
	     			allTestsSum = allTestsSum + iterator.getOverallTestsCount();
	     			allPassedTestsSum = allPassedTestsSum + iterator.getTestsPassed();
	     		}
	     	}
	     	String userScore = ((Integer) Math.round((allPassedTestsSum / allTestsSum)*100)).toString();
	     	newUserTask.getUser().setLastScore(userScore + "%");
	     	newUserTask.setCompleted(true);
			return new ResponseEntity<>(userTaskRepository.save(newUserTask), HttpStatus.OK);
		}
	}
	public String runProcess(ProcessBuilder processBuilder) {
		String result = "";
		String errorMsg = "";
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line=null;
            String error=null;
            while ( ((line = reader.readLine()) != null) || ((error = errorReader.readLine()) != null) ) {
            	if (line != null) {
                	result = result + "\n" + "~" + line;
                    System.out.println(line);
            	}
            	if (error!=null) {
            		errorMsg = errorMsg + "\n" + "~" + error;
                    System.out.println(error);
            	}
            }                 
            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);
            result = result + errorMsg + "\n ~ Exited with error code : " + exitCode;
            return result;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);  
        }

	}
	
	@Transactional
	public Long rememberPort() {
		List<ExecModulePort> portList = execModulePortRepository.findAll();
		Long portNumber = Long.parseLong(execModulePortPoolStartIndex);
		if(portList.isEmpty()) {
			ExecModulePort port = new ExecModulePort(portNumber);
			execModulePortRepository.save(port);
		}else {
			portNumber = portList.get(portList.size()-1).getPort();
			ExecModulePort port = new ExecModulePort(portNumber+1);
			execModulePortRepository.save(port);
		}
		return portNumber;
	}
	
}
