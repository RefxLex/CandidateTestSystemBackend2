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

import org.springframework.beans.factory.annotation.Autowired;
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
import com.reflex.model.Task;
import com.reflex.model.TaskUnitTest;
import com.reflex.model.User;
import com.reflex.model.UserTask;
import com.reflex.model.enums.UserStatus;
import com.reflex.repository.TaskRepository;
import com.reflex.repository.UserRepository;
import com.reflex.repository.UserTaskRepository;
import com.reflex.request.CreateUserTaskRequest;
import com.reflex.request.SolutionRequest;
import com.reflex.request.TestLaunchUserTaskRequest;
import com.reflex.request.UnitTestRequest;
import com.reflex.request.UpdateCommentRequest;
import com.reflex.request.CompleteUserTaskRequest;
import com.reflex.model.UserTaskSolution;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

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
	
	@PostMapping("/setup")
	public ResponseEntity<?> setupProjectsFolder(){
		
		// define OS
		String fs = System.getProperty("file.separator");
    	boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    	boolean isLinux = System.getProperty("os.name").toLowerCase().startsWith("linux");
    	
    	Path path = Paths.get(System.getProperty("user.home") + fs + "cadidate_test_system_projects");
    	ProcessBuilder processBuilder = new ProcessBuilder();
    	
    	if(Files.exists(path)==false) {
    		
    		// create main, test_launch and lib folders
	    	processBuilder.directory(new File(System.getProperty("user.home")));
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "mkdir cadidate_test_system_projects" + fs + "test_launch" + " " + "cadidate_test_system_projects" + fs
	    				+ "lib");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "mkdir cadidate_test_system_projects" + fs + "test_launch" + " " + "cadidate_test_system_projects" + fs
	    				+ "lib");
	    	}
	    	runProcess(processBuilder);

	        // download JUnit 5 jars
	    	processBuilder.directory(new File(System.getProperty("user.home")));
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "mvn org.apache.maven.plugins:maven-dependency-plugin:3.6.0:get" +
	    	" -DrepoUrl=https://download.java.net/maven/2/ -Dartifact=org.junit.jupiter:junit-jupiter-api:5.9.2");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "mvn org.apache.maven.plugins:maven-dependency-plugin:3.6.0:get" +
	    		    	" -DrepoUrl=https://download.java.net/maven/2/ -Dartifact=org.junit.jupiter:junit-jupiter-api:5.9.2");
	    	}
	    	runProcess(processBuilder);
	        
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "mvn org.apache.maven.plugins:maven-dependency-plugin:3.6.0:get" +
	    	" -DrepoUrl=https://download.java.net/maven/2/ -Dartifact=org.junit.jupiter:junit-jupiter-api:5.9.2");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "mvn org.apache.maven.plugins:maven-dependency-plugin:3.6.0:get" +
	    	" -DrepoUrl=https://download.java.net/maven/2/ -Dartifact=org.junit.platform:junit-platform-console-standalone:1.9.3");
	    	}
	    	runProcess(processBuilder);
	    	
	    	// copy jars to lib folder
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "copy .m2" + fs + "repository" + fs + "org" + fs + "apiguardian" + fs + "apiguardian-api" + fs 
	    				+ "1.1.2" + fs + "apiguardian-api-1.1.2.jar" + " cadidate_test_system_projects" + fs + "lib");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "cp .m2" + fs + "repository" + fs + "org" + fs + "apiguardian" + fs + "apiguardian-api" + fs 
	    				+ "1.1.2" + fs + "apiguardian-api-1.1.2.jar" + " cadidate_test_system_projects" + fs + "lib");
	    	}
	    	runProcess(processBuilder);
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "copy .m2" + fs + "repository" + fs + "org" + fs + "junit" + fs + "jupiter" + fs + "junit-jupiter-api" + fs
	    				+ "5.9.2" + fs + "junit-jupiter-api-5.9.2.jar" + " cadidate_test_system_projects" + fs + "lib");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "cp .m2" + fs + "repository" + fs + "org" + fs + "junit" + fs + "jupiter" + fs + "junit-jupiter-api" + fs
	    				+ "5.9.2" + fs + "junit-jupiter-api-5.9.2.jar" + " cadidate_test_system_projects" + fs + "lib");
	    	}
	    	runProcess(processBuilder);
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "copy .m2" + fs + "repository" + fs + "org" + fs + "junit" + fs + "platform" + fs + "junit-platform-commons"
	    	+ fs + "1.9.2" + fs + "junit-platform-commons-1.9.2.jar" + " cadidate_test_system_projects" + fs + "lib");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "cp .m2" + fs + "repository" + fs + "org" + fs + "junit" + fs + "platform" + fs + "junit-platform-commons"
	    		    	+ fs + "1.9.2" + fs + "junit-platform-commons-1.9.2.jar" + " cadidate_test_system_projects" + fs + "lib");
	    	}
	    	runProcess(processBuilder);
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "copy .m2" + fs + "repository" + fs +"org" + fs +"junit" + fs +"platform" + fs +
	    				"junit-platform-console-standalone" + fs +"1.9.3" + fs + "junit-platform-console-standalone-1.9.3.jar" +
	    				" cadidate_test_system_projects" + fs + "lib");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "cp .m2" + fs + "repository" + fs +"org" + fs +"junit" + fs +"platform" + fs +
	    				"junit-platform-console-standalone" + fs +"1.9.3" + fs + "junit-platform-console-standalone-1.9.3.jar" +
	    				" cadidate_test_system_projects" + fs + "lib");
	    	}
	    	runProcess(processBuilder);
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "copy .m2" + fs + "repository" + fs +"org" + fs + "opentest4j" + fs + "opentest4j" + fs +
	    				"1.2.0" + fs +"opentest4j-1.2.0.jar" + " cadidate_test_system_projects" + fs + "lib");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "cp .m2" + fs + "repository" + fs +"org" + fs + "opentest4j" + fs + "opentest4j" + fs +
	    				"1.2.0" + fs +"opentest4j-1.2.0.jar" + " cadidate_test_system_projects" + fs + "lib");
	    	}
	    	runProcess(processBuilder);
    	}
    	else {
    		System.out.println("main directory already created, skipping...");
    	}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@PostMapping("/test-launch")
	public ResponseEntity<?> runUserUnitTest(@Valid @RequestBody TestLaunchUserTaskRequest userTaskRequest){
		// define OS
		String fs = System.getProperty("file.separator");
    	boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
    	boolean isLinux = System.getProperty("os.name").toLowerCase().startsWith("linux");
		ProcessBuilder processBuilder = new ProcessBuilder();
		
	    // create project folder
		Instant submitDate = Instant.now();
		String projectKey = submitDate.toString();
		int index = 0;
		while(index != -1) {
			index = projectKey.indexOf(":");
			if(index != -1) {
				projectKey = projectKey.substring(0, index) + "_" + projectKey.substring(index+1, projectKey.length());
			}
		}
		index = 0;
		while(index != -1) {
			index = projectKey.indexOf("-");
			if(index != -1) {
				projectKey = projectKey.substring(0, index) + "_" + projectKey.substring(index+1, projectKey.length());
			}
		}
		index = projectKey.indexOf(".");
		projectKey = projectKey.substring(0, index) + "_" + projectKey.substring(index+1, projectKey.length());
		
	    processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + "test_launch"));
	    if (isWindows) {
	    	processBuilder.command("cmd.exe", "/c", "mkdir " + projectKey + fs + "src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs
	    			+ "java_project" + projectKey + " " + projectKey + fs + "src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs
	    			+ "java_project" + projectKey + " " + projectKey + fs + "target" + fs + "classes" + " " + projectKey + fs + "target" + fs + "test_classes");
	    } 
	    else if (isLinux){
	    	processBuilder.command("sh", "-c", "mkdir " + projectKey + fs + "src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs
	    			+ "java_project" + projectKey + " " + projectKey + fs + "src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs
	    			+ "java_project" + projectKey + " " + projectKey + fs + "target" + fs + "classes" + " " + projectKey + fs + "target" + fs + "test_classes");
	    }
	    runProcess(processBuilder);
	 
	 // create main files	        
        for(SolutionRequest iterator: userTaskRequest.getSolution()) {
        
	        // add package
	        String codeBase64 = iterator.getCode();
	        byte[] decodedBytes = Base64.getDecoder().decode(codeBase64);
	        String codeDecoded = new String(decodedBytes);
	        codeDecoded = "package com.cleverhire.java_project" + projectKey + ";" + "\n" + codeDecoded;
	        
	        // find out class name
	        int startIndex = codeDecoded.indexOf("class");
	        int secondSpaceIndex = codeDecoded.indexOf(" ", startIndex + 6);
	        String className = codeDecoded.substring(startIndex + 6, secondSpaceIndex);

	        // create className.java
	        FileOutputStream fos;
			try {
				fos = new FileOutputStream(System.getProperty("user.home") + fs + "cadidate_test_system_projects"+ fs + "test_launch" + fs + projectKey + fs
						+ "src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs + "java_project" + projectKey + fs + className +".java");
				fos.write(codeDecoded.getBytes());
		        fos.flush();
		        fos.close(); 
			} catch (IOException e) {
				e.printStackTrace();
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
        }
        
     // create test files
        List<String> testClassesNameList = new ArrayList<>();
        for(UnitTestRequest iterator: userTaskRequest.getUnitTest()) {
        	
	        // add package
	        String codeBase64 = iterator.getCode();
	        byte[] decodedBytes = Base64.getDecoder().decode(codeBase64);
	        String codeDecoded = new String(decodedBytes);
	        codeDecoded = "package com.cleverhire.java_project" + projectKey + ";" + "\n" + codeDecoded;
	        
	        // find out class name
	        int startIndex = codeDecoded.indexOf("class");
	        int secondSpaceIndex = codeDecoded.indexOf(" ", startIndex + 6);
	        String className = codeDecoded.substring(startIndex + 6, secondSpaceIndex);
	        testClassesNameList.add(className);

	        // create className.java
	        FileOutputStream fos;
			try {
				fos = new FileOutputStream(System.getProperty("user.home") + fs + "cadidate_test_system_projects"+ fs + "test_launch" + fs + projectKey + fs
						+ "src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs + "java_project" + projectKey + fs + className +".java");
				fos.write(codeDecoded.getBytes());
		        fos.flush();
		        fos.close(); 
			} catch (IOException e) {
				e.printStackTrace();
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
			}
        }
        
      // compile classes      
        // compile src
	    processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + "test_launch" + fs + projectKey));
	    if (isWindows) {
	    	processBuilder.command("cmd.exe", "/c", "javac -d target" + fs + "classes" + " src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs 
	    			+ "java_project" + projectKey + fs + "*.java");
	    } 
	    else if (isLinux){
	    	processBuilder.command("sh", "-c", "javac -d target" + fs + "classes" + " src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs 
	    			+ "java_project" + projectKey + fs + "*.java");
	    }
	    String mainCompileResult = runProcess(processBuilder);
	    
	    // check compile errors
	    int errorCodeIndex = mainCompileResult.indexOf("error code");
	    Integer errorCode = Integer.parseInt(mainCompileResult.substring(errorCodeIndex+13 ,errorCodeIndex+14));	    
	    if(errorCode == 1) {
	        Map<String, Object> response = new HashMap<>();
	        String encodedMainCompileResult = Base64.getEncoder().encodeToString(mainCompileResult.getBytes(StandardCharsets.ISO_8859_1));
	        response.put("result", encodedMainCompileResult);
	        
	  	  	// delete created folder
		    processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + "test_launch"));
		    if (isWindows) {
		    	processBuilder.command("cmd.exe", "/c", "rmdir /s /q " + projectKey);
		    } 
		    else if (isLinux){
		    	processBuilder.command("sh", "-c", "rm -rf " + projectKey);
		    }
		    runProcess(processBuilder);
	        
	    	return new ResponseEntity<>(response, HttpStatus.OK);
	    }
	    
	    // compile test
	    processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + "test_launch" + fs + projectKey));
	    if (isWindows) {
	    	processBuilder.command("cmd.exe", "/c", "javac -d target" + fs + "test_classes" + " -cp %USERPROFILE%" + fs + "cadidate_test_system_projects" + fs 
	    			+ "lib" + fs + "*;target" + fs + "classes" + " src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs + "java_project" 
	    			+ projectKey + fs + "*.java");
	    } 
	    else if (isLinux){
	    	processBuilder.command("sh", "-c", "javac -d target"+ fs + "classes" + fs + "test_classes" + " -cp ~" + fs + "cadidate_test_system_projects" + fs
	    			+ "lib" + fs + "*;target" + " src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs + "java_project" + projectKey + fs + "*.java");
	    }
	    String testCompileResult = runProcess(processBuilder);
	    
	    // check compile errors
	    errorCodeIndex = testCompileResult.indexOf("error code");
	    errorCode = Integer.parseInt(testCompileResult.substring(errorCodeIndex+13 ,errorCodeIndex+14));	    
	    if(errorCode == 1) {
	        Map<String, Object> response = new HashMap<>();
	        String encodedTestCompileResult = Base64.getEncoder().encodeToString(testCompileResult.getBytes(StandardCharsets.ISO_8859_1));
	        response.put("result", encodedTestCompileResult);
	        
	  	  	// delete created folder
		    processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + "test_launch"));
		    if (isWindows) {
		    	processBuilder.command("cmd.exe", "/c", "rmdir /s /q " + projectKey);
		    } 
		    else if (isLinux){
		    	processBuilder.command("sh", "-c", "rm -rf " + projectKey);
		    }
		    runProcess(processBuilder);
	        
	    	return new ResponseEntity<>(response, HttpStatus.OK);
	    }
    
	  // run unit tests
	    String testsResult="";
	    for(String testClassName: testClassesNameList) {
		    if (isWindows) {
		    	processBuilder.command("cmd.exe", "/c", "java -jar" + " %USERPROFILE%" + fs + "cadidate_test_system_projects" + fs + "lib" + fs
		    			+ "junit-platform-console-standalone-1.9.3.jar" + " --class-path" + " target" + fs + "classes;target" + fs + "test_classes" 
		    			+ " --select-class" + " com.cleverhire." + "java_project" + projectKey + "." + testClassName);
		    } 
		    else if (isLinux){
		    	processBuilder.command("sh", "-c", "java -jar" + " ~" + fs + "cadidate_test_system_projects" + fs + "lib" + fs
		    			+ "junit-platform-console-standalone-1.9.3.jar" + " --class-path" + " target"  + fs + "classes;target" + fs + "test_classes" 
		    			+ " --select-class" + " com.cleverhire." + "java_project" + projectKey + "." + testClassName);
		    }
		    testsResult = testsResult + runProcess(processBuilder);
	    }
	  // delete created folder
	    processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + "test_launch"));
	    if (isWindows) {
	    	processBuilder.command("cmd.exe", "/c", "rmdir /s /q " + projectKey);
	    } 
	    else if (isLinux){
	    	processBuilder.command("sh", "-c", "rm -rf " + projectKey);
	    }
	    runProcess(processBuilder);

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
			
			newUserTask.getUserTaskSolution().clear();
			for(SolutionRequest iterator: userTaskRequest.getSolution()) {
				UserTaskSolution userTaskSol = new UserTaskSolution(iterator.getCode());
				newUserTask.getUserTaskSolution().add(userTaskSol);
			}
			
		// define OS
			String fs = System.getProperty("file.separator");
			String projectKey = "_" + userTask.get().getId().toString();
	    	boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
	    	boolean isLinux = System.getProperty("os.name").toLowerCase().startsWith("linux");
	    	
	    	ProcessBuilder processBuilder = new ProcessBuilder();	
	    // create project folder
		    processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects"));
		    if (isWindows) {
		    	processBuilder.command("cmd.exe", "/c", "mkdir " + projectKey + fs + "src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs
		    			+ "java_project" + projectKey + " " + projectKey + fs + "src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs
		    			+ "java_project" + projectKey + " " + projectKey + fs + "target" + fs + "classes" + " " + projectKey + fs + "target" + fs + "test_classes");
		    } 
		    else if (isLinux){
		    	processBuilder.command("sh", "-c", "mkdir " + projectKey + fs + "src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs
		    			+ "java_project" + projectKey + " " + projectKey + fs + "src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs
		    			+ "java_project" + projectKey + " " + projectKey + fs + "target" + fs + "classes" + " " + projectKey + fs + "target" + fs + "test_classes");
		    }
		    runProcess(processBuilder);
	    
	        
	      // create main files	        
	        for(UserTaskSolution iterator: newUserTask.getUserTaskSolution()) {
	        
		        // add package
		        String codeBase64 = iterator.getCode();
		        byte[] decodedBytes = Base64.getDecoder().decode(codeBase64);
		        String codeDecoded = new String(decodedBytes);
		        codeDecoded = "package com.cleverhire.java_project" + projectKey + ";" + "\n" + codeDecoded;
		        
		        // find out class name
		        int startIndex = codeDecoded.indexOf("class");
		        int secondSpaceIndex = codeDecoded.indexOf(" ", startIndex + 6);
		        String className = codeDecoded.substring(startIndex + 6, secondSpaceIndex);

		        // create className.java
		        FileOutputStream fos;
				try {
					fos = new FileOutputStream(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + projectKey + fs 
							+ "src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs + "java_project" + projectKey + fs + className +".java");
					fos.write(codeDecoded.getBytes());
			        fos.flush();
			        fos.close(); 
				} catch (IOException e) {
					e.printStackTrace();
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
				}
	        }
	                
	     // create test files
	        List<String> testClassesNameList = new ArrayList<>();
	        for(TaskUnitTest iterator: newUserTask.getTask().getUnitTest()) {
	        	
		        // add package
		        String codeBase64 = iterator.getCode();
		        byte[] decodedBytes = Base64.getDecoder().decode(codeBase64);
		        String codeDecoded = new String(decodedBytes);
		        codeDecoded = "package com.cleverhire.java_project" + projectKey + ";" + "\n" + codeDecoded;
		        
		        // find out class name
		        int startIndex = codeDecoded.indexOf("class");
		        int secondSpaceIndex = codeDecoded.indexOf(" ", startIndex + 6);
		        String className = codeDecoded.substring(startIndex + 6, secondSpaceIndex);
		        testClassesNameList.add(className);

		        // create className.java
		        FileOutputStream fos;
				try {
					fos = new FileOutputStream(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + projectKey + fs 
							+ "src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs + "java_project" + projectKey + fs + className +".java");
					fos.write(codeDecoded.getBytes());
			        fos.flush();
			        fos.close(); 
				} catch (IOException e) {
					e.printStackTrace();
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
				}
	        }
	        
	     // compile classes
	        boolean compileIsOk = true;
	        // compile src
		    processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + projectKey));
		    if (isWindows) {
		    	processBuilder.command("cmd.exe", "/c", "javac -d target" + fs + "classes" + " src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs 
		    			+ "java_project" + projectKey + fs + "*.java");
		    } 
		    else if (isLinux){
		    	processBuilder.command("sh", "-c", "javac -d target" + fs + "classes" + " src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire" + fs 
		    			+ "java_project" + projectKey + fs + "*.java");
		    }
		    String mainCompileResult = runProcess(processBuilder);
		    
		    // check compile errors
		    int errorCodeIndex = mainCompileResult.indexOf("error code");
		    Integer errorCode = Integer.parseInt(mainCompileResult.substring(errorCodeIndex+13 ,errorCodeIndex+14));	    
		    if(errorCode == 1) {
		        String encodedMainCompileResult = Base64.getEncoder().encodeToString(mainCompileResult.getBytes(StandardCharsets.ISO_8859_1));
		        compileIsOk = false;
	        	// number of tests unknown, set failure rate to 100%
		     	newUserTask.setTestsPassed(0);
		     	newUserTask.setTestsFailed(1);
	        	newUserTask.setOverallTestsCount(1);
	        	newUserTask.setCompilationResult("FAIL");
	        	newUserTask.setResultReport(encodedMainCompileResult);
		    }
		    
		    // compile test
		    processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + projectKey));
		    if (isWindows) {
		    	processBuilder.command("cmd.exe", "/c", "javac -d target" + fs + "test_classes" + " -cp %USERPROFILE%" + fs + "cadidate_test_system_projects" + fs 
		    			+ "lib" + fs + "*;target" + fs + "classes" + " src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs + "java_project" 
		    			+ projectKey + fs + "*.java");
		    } 
		    else if (isLinux){
		    	processBuilder.command("sh", "-c", "javac -d target"+ fs + "classes" + fs + "test_classes" + " -cp ~" + fs + "cadidate_test_system_projects" + fs
		    			+ "lib" + fs + "*;target" + " src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire" + fs + "java_project" + projectKey + fs + "*.java");
		    }
		    String testCompileResult = runProcess(processBuilder);
		    
		    // check compile errors
		    errorCodeIndex = testCompileResult.indexOf("error code");
		    errorCode = Integer.parseInt(testCompileResult.substring(errorCodeIndex+13 ,errorCodeIndex+14));	    
		    if(errorCode == 1) {
		        String encodedTestCompileResult = Base64.getEncoder().encodeToString(testCompileResult.getBytes(StandardCharsets.ISO_8859_1));
		        compileIsOk = false;
	        	// number of tests unknown, set failure rate to 100%
		     	newUserTask.setTestsPassed(0);
		     	newUserTask.setTestsFailed(1);
	        	newUserTask.setOverallTestsCount(1);
	        	newUserTask.setCompilationResult("FAIL");
	        	newUserTask.setResultReport(encodedTestCompileResult);

		    }
		    
		    if(compileIsOk) {
			    	
			// run unit tests
			    String testsResult="";
			    List<String> testResultList = new ArrayList<>();
			    for(String testClassName: testClassesNameList) {
				    if (isWindows) {
				    	processBuilder.command("cmd.exe", "/c", "java -jar" + " %USERPROFILE%" + fs + "cadidate_test_system_projects" + fs + "lib" + fs
				    			+ "junit-platform-console-standalone-1.9.3.jar" + " --class-path" + " target" + fs + "classes;target" + fs + "test_classes" 
				    			+ " --select-class" + " com.cleverhire." + "java_project" + projectKey + "." + testClassName);
				    } 
				    else if (isLinux){
				    	processBuilder.command("sh", "-c", "java -jar" + " ~" + fs + "cadidate_test_system_projects" + fs + "lib" + fs
				    			+ "junit-platform-console-standalone-1.9.3.jar" + " --class-path" + " target"  + fs + "classes;target" + fs + "test_classes" 
				    			+ " --select-class" + " com.cleverhire." + "java_project" + projectKey + "." + testClassName);
				    }
				    testsResult = runProcess(processBuilder);
				    testResultList.add(testsResult);
			    }                      
	 
		     // parse test results
			    Integer testsPassed=0;
			    Integer testsFailed=0;
			    Integer overallTestCount = 0;
			    int foundIndex = 0;
			    int failedIndex = 0;
			    int successfulIndex = 0;
			    String encodedTestResult="";
			    for(String iterator: testResultList) {
			    	foundIndex = iterator.indexOf("tests found");
			    	failedIndex = iterator.indexOf("tests failed");
			    	successfulIndex = iterator.indexOf("tests successful");
			    	overallTestCount = overallTestCount + Integer.parseInt(iterator.substring(foundIndex-2,foundIndex-1));
			    	testsFailed = testsFailed + Integer.parseInt(iterator.substring(failedIndex-2,failedIndex-1));
			    	testsPassed = testsPassed + Integer.parseInt(iterator.substring(successfulIndex-2,successfulIndex-1));
			    	encodedTestResult = encodedTestResult + Base64.getEncoder().encodeToString(iterator.getBytes(StandardCharsets.ISO_8859_1));
			    }
			    newUserTask.setTestsPassed(testsPassed);
			    newUserTask.setTestsFailed(testsFailed);
			    newUserTask.setOverallTestsCount(overallTestCount);
			    newUserTask.setCompilationResult("OK");
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
	
}
