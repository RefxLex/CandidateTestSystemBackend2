package com.reflex.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.TimerTask;
import java.util.Timer;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import com.fasterxml.jackson.core.JsonProcessingException;
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
import com.reflex.response.UserTaskSolutionResponse;
import com.reflex.request.CompleteUserTaskRequest;
import com.reflex.model.UserTaskSolution;
import com.reflex.response.UserTaskResponse;
import com.reflex.security.jwt.JwtUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
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
	
    @Autowired 
    private PlatformTransactionManager transactionManager;

    @Autowired 
    private EntityManager entityManager;

	@Value("${execModuleUrl}")
	private String execModuleUrl;
	
	@Value("${execModulePortPoolStartIndex}")
	private String execModulePortPoolStartIndex;
	
	private static final int timeout=15000;
	
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
	
	@GetMapping("/find-one/{id}")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<UserTask> getUserTask(@PathVariable ("id") Long userTaskId){
		Optional <UserTask> userTask = userTaskRepository.findById(userTaskId);
		if(userTask.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user task found with id=" + userTaskId);
		}
		return new ResponseEntity<>(userTask.get(), HttpStatus.OK);
	}
	
	@GetMapping("/find-one-sol-unexposed/{id}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<UserTaskResponse> getUserTaskSolutionUnexposed(@PathVariable ("id") Long userTaskId){
		Optional <UserTask> userTask = userTaskRepository.findById(userTaskId);
		if(userTask.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user task found with id=" + userTaskId);
		}
		UserTask userTaskObj = userTask.get();
		UserTaskResponse response = new UserTaskResponse();
		response.setId(userTaskObj.getId());
		response.setAssignDate(userTaskObj.getAssignDate());
		response.setComment(userTaskObj.getComment());
		response.setCompilationResult(userTaskObj.getCompilationResult());
		response.setCompleted(userTaskObj.isCompleted());
		response.setOverallTestsCount(userTaskObj.getOverallTestsCount());
		response.setResultReport(userTaskObj.getResultReport());
		response.setStartDate(userTaskObj.getStartDate());
		response.setSubmitDate(userTaskObj.getSubmitDate());
		response.setTaskDescription(userTaskObj.getTask().getDescription());
		response.setTaskLanguageName(userTaskObj.getTask().getLanguageName());
		response.setTaskName(userTaskObj.getTask().getName());
		response.setTestsFailed(userTaskObj.getTestsFailed());
		response.setTestsPassed(userTaskObj.getTestsPassed());
		response.setTimeSpent(userTaskObj.getTimeSpent());
		for(UserTaskSolution sol: userTaskObj.getUserTaskSolution()) {
			UserTaskSolutionResponse solResponse = new UserTaskSolutionResponse(sol.getId(), sol.getCode());
			response.getUserTaskSolution().add(solResponse);
		}	
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/find-list-sol-unexposed/{userId}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<List<UserTaskResponse>> getUserTasksByUserIdSolutionUnexposed(@PathVariable ("userId") Long userId){
		List<UserTask> userTasksList = new ArrayList<>();
		userTasksList = userTaskRepository.selectByUserId(userId);
		
		List<UserTaskResponse> responseList = new ArrayList<>();
		for(UserTask iterator: userTasksList) {
			UserTaskResponse userTaskObj = new UserTaskResponse();
			userTaskObj.setId(iterator.getId());
			userTaskObj.setAssignDate(iterator.getAssignDate());
			userTaskObj.setComment(iterator.getComment());
			userTaskObj.setCompilationResult(iterator.getCompilationResult());
			userTaskObj.setCompleted(iterator.isCompleted());
			userTaskObj.setOverallTestsCount(iterator.getOverallTestsCount());
			userTaskObj.setResultReport(iterator.getResultReport());
			userTaskObj.setStartDate(iterator.getStartDate());
			userTaskObj.setSubmitDate(iterator.getSubmitDate());
			userTaskObj.setTaskDescription(iterator.getTask().getDescription());
			userTaskObj.setTaskLanguageName(iterator.getTask().getLanguageName());
			userTaskObj.setTaskName(iterator.getTask().getName());
			userTaskObj.setTestsFailed(iterator.getTestsFailed());
			userTaskObj.setTestsPassed(iterator.getTestsPassed());
			userTaskObj.setTimeSpent(iterator.getTimeSpent());
			for(UserTaskSolution sol: iterator.getUserTaskSolution()) {
				UserTaskSolutionResponse solResponse = new UserTaskSolutionResponse(sol.getId(), sol.getCode());
				userTaskObj.getUserTaskSolution().add(solResponse);
			}
			responseList.add(userTaskObj);
		}
		return new ResponseEntity<>(responseList, HttpStatus.OK);
	}
	
	@GetMapping("/find-list/{userId}")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
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
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> runUserUnitTest(@Valid @RequestBody TestLaunchUserTaskRequest userTaskRequest){
		
		// bind port

		ProcessBuilder processBuilder = new ProcessBuilder();
		Long port = rememberPort();

		// create container
		String containerId = "";
	    containerId = createContainer(processBuilder, port);
	    		  
		RequestConfig requestConfig = RequestConfig.custom().
			    setConnectionRequestTimeout(timeout).setConnectTimeout(timeout).setSocketTimeout(timeout).build();
		HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig);
		
		String result="";
		UnitTestResultResponse unitTestResult = new UnitTestResultResponse();
		ObjectMapper mapper = new ObjectMapper();
		String requestJSON = "";
		HttpPost post = new HttpPost(execModuleUrl + ":" + port + "/api/exec-module/test-launch");
		post.addHeader("content-type", "application/json");
		
		// set timer for container to complete task
		int hardTimeout = 15000;
		Timer timer = new Timer();
		TimerTask timerTask = new TimerTask() {
		    @Override
		    public void run() {	    		
		    	// container stuck in loop
			    post.abort();
		    }
		};
		timer.schedule(timerTask, hardTimeout);
		
		try {
			requestJSON = mapper.writeValueAsString(userTaskRequest);
			post.setEntity(new StringEntity(requestJSON));
		}
		catch(JsonProcessingException | UnsupportedEncodingException e) {
			e.printStackTrace();
			timer.cancel();
			killContainer(processBuilder, containerId);
			releasePort(port);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		}
     
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
        		CloseableHttpResponse response = httpClient.execute(post)) {	        	
	        	int statusCode = response.getStatusLine().getStatusCode();
	        	if(statusCode == 200) {		        	
		            result = EntityUtils.toString(response.getEntity());
		            unitTestResult = mapper.readValue(result, UnitTestResultResponse.class);
		            timer.cancel();
	        	}
	        	else {
	        		timer.cancel();
	        		killContainer(processBuilder, containerId);
	        		releasePort(port);
	        		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exec module error" + response.getStatusLine().getReasonPhrase());
	        	}
	                        
	    }
	    catch(HttpHostConnectException exception) {
	    	exception.printStackTrace();
	    	timer.cancel();
	    	killContainer(processBuilder, containerId);
	    	releasePort(port);
	        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error connecting to exec module");
	    }
        catch (IOException e) {
			e.printStackTrace();
			logger.error("io excp " + e.getMessage());
        	timer.cancel();
			killContainer(processBuilder, containerId);
			releasePort(port);
	        Map<String, Object> response = new HashMap<>();
	        response.put("result", "container killed, reason: request execution timeout exeeded");	
	        return new ResponseEntity<>(response, HttpStatus.OK);
		}
        
        // stop and remove container    
	    processBuilder.directory(new File(System.getProperty("user.home")));
	    processBuilder.command("sh", "-c","docker stop " + containerId);
	    runProcess(processBuilder);
	    
	    // release port
	    releasePort(port);
        
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
	@PreAuthorize("hasRole('USER')")
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
			
			// bind port
			ProcessBuilder processBuilder = new ProcessBuilder();
			Long port = rememberPort();
			
			// create container
			String containerId = "";
		    containerId = createContainer(processBuilder, port);
					
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
			HttpPost post = new HttpPost(execModuleUrl + ":" + port + "/api/exec-module/test-launch");
			post.addHeader("content-type", "application/json");
			
			// set timer for container to complete task
			int hardTimeout = 15000;
			Timer timer = new Timer();
			TimerTask timerTask = new TimerTask() {
			    @Override
			    public void run() {	    		
			    	// container stuck in loop
				    post.abort();
			    }
			};
			timer.schedule(timerTask, hardTimeout);
			
			try {
				requestJSON = mapper.writeValueAsString(execModuleRequest);
				post.setEntity(new StringEntity(requestJSON));
			}
			catch(JsonProcessingException | UnsupportedEncodingException e) {
				e.printStackTrace();
				timer.cancel();
				killContainer(processBuilder, containerId);
				releasePort(port);
				logger.error("encoding" + e.getMessage());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
	     
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		             CloseableHttpResponse response = httpClient.execute(post)) {	        	
		        	int statusCode = response.getStatusLine().getStatusCode();
		        	if(statusCode == 200) {		        	
			            result = EntityUtils.toString(response.getEntity());
			            unitTestResult = mapper.readValue(result, UnitTestResultResponse.class);
			            timer.cancel();
		        	}
		        	else {
		        		timer.cancel();
		        		killContainer(processBuilder, containerId);
		        		releasePort(port);
		        		logger.error("Exec module error");
		        		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Exec module error" + response.getStatusLine().getReasonPhrase());
		        	}
		                        
		    }
		    catch(HttpHostConnectException exception) {
		    	exception.printStackTrace();
		    	timer.cancel();
		    	killContainer(processBuilder, containerId);
		    	releasePort(port);
		        throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error connecting to exec module");
		    }
		    catch (IOException e) {
		    	timer.cancel();
				killContainer(processBuilder, containerId);
				releasePort(port);
			    Map<String, Object> response = new HashMap<>();
			    response.put("result", "container killed, reason: request execution timeout exeeded");	
			    return new ResponseEntity<>(response, HttpStatus.OK);
			}
	        
	        // stop and remove container    
		    processBuilder.directory(new File(System.getProperty("user.home")));
		    processBuilder.command("sh", "-c","docker stop " + containerId);
		    runProcess(processBuilder);
		    
		    // release port
		    releasePort(port);
			
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
	     	List<UserTask> userTasks = userTaskRepository.selectByUserIdExcludeOneById(newUserTask.getUser().getId(), userTaskId);
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
            logger.error("io excp" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);  
        }

	}
	
	public String createContainer(ProcessBuilder processBuilder, Long port) {
    	processBuilder.directory(new File(System.getProperty("user.home")));
    	processBuilder.command("sh", "-c","docker container run --rm -p " + port + ":8083 -d refxlexj/ctsmodule:0.1");
    	
		String containerId = "";
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line=null;

            while (((line = reader.readLine()) != null)) {
            	if (line != null) {
            		containerId = containerId + line;
                    System.out.println(line);
            	}
            }                 
            int exitCode = process.waitFor();
            if(exitCode != 0) {
            	logger.error("failed to create docker container");
            	throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR); 
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            logger.error("io excp" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);  
        }	    	
    	System.out.println("started container with id= " + containerId + " and port=" + port);	    	
    	try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	return containerId;
	}
	
	public void killContainer(ProcessBuilder processBuilder, String containerId) {
	    processBuilder.directory(new File(System.getProperty("user.home")));
	    processBuilder.command("sh", "-c","docker kill " + containerId);
	    runProcess(processBuilder);
	}
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "300")})
	public Long rememberPort() {
		
		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		definition.setTimeout(-1);
		
	    TransactionStatus status = transactionManager.getTransaction(definition);	    
	    Long portNumber = Long.parseLong(execModulePortPoolStartIndex);
	    try {

	    	List<ExecModulePort> portList = execModulePortRepository.findAll();
			List<ExecModulePort> availablePortList = execModulePortRepository.selectNotActive();
			ExecModulePort port = new ExecModulePort();
			if(portList.isEmpty() == false) {	// set port = execModulePortPoolStartIndex if empty	
				if(availablePortList.isEmpty()) {
					// get last
					portNumber = portList.get(portList.size()-1).getPort() + 1;
				}else {
					portNumber = availablePortList.get(0).getPort();
				}
			}
			port.setPort(portNumber);
			port.setActive(true);
			execModulePortRepository.save(port);
	        //entityManager.persist(port);
	        transactionManager.commit(status);
	    } catch (Exception ex) {
	        transactionManager.rollback(status);
	        logger.error("bind port transaction error" + ex.getMessage());
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
		return portNumber;
	}
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "300")})
	public void releasePort(Long portNumber) {
		
		DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
		definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		definition.setTimeout(-1);
	    TransactionStatus status = transactionManager.getTransaction(definition);
	    
	    try {
	    	Optional<ExecModulePort> port = execModulePortRepository.findByport(portNumber);
	    	if(port.isEmpty()) {
	    		logger.error("closable port not found");
	    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
	    	}
	    	ExecModulePort newPort = port.get();
	    	newPort.setActive(false);
	    	//entityManager.persist(portObj);
			execModulePortRepository.save(newPort);
	        transactionManager.commit(status);
	    } catch (Exception ex) {
	        transactionManager.rollback(status);
	        logger.error("remove port transaction error" + ex.getMessage());
	        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
		
	}
	
}
