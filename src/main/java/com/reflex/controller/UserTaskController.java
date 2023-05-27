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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
//import com.reflex.model.TaskTestInput;
import com.reflex.model.TaskUnitTest;
import com.reflex.model.User;
import com.reflex.model.UserTask;
//import com.reflex.model.UserTaskResult;
import com.reflex.model.enums.UserStatus;
import com.reflex.repository.TaskRepository;
import com.reflex.repository.UserRepository;
import com.reflex.repository.UserTaskRepository;
import com.reflex.request.CreateUserTaskRequest;
import com.reflex.request.SolutionRequest;
import com.reflex.request.UpdateCommentRequest;
import com.reflex.request.UserTaskRequest;
//import com.reflex.response.LanguageResponse;
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
			
			/*	judge integration
			overallTestsCount = task.get().getTaskTestInput().size();
			Set<TaskTestInput> tastTestInputSet = task.get().getTaskTestInput();
			for(TaskTestInput testInput: tastTestInputSet) {
				UserTaskResult userTaskResult = new UserTaskResult(testInput.getInput(), testInput.getOutput());
				newUserTask.getUserTaskResult().add(userTaskResult);
			} */
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
		UserTask newUserTask = oldUserTask.get();
		newUserTask.setStartDate(Instant.now());
		return new ResponseEntity<>(userTaskRepository.save(newUserTask), HttpStatus.OK);
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
    	
    	// delete main folder if exists
    	/*
    	if(Files.exists(path)) {
	    	processBuilder.directory(new File(System.getProperty("user.home")));
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "rmdir /s /q cadidate_test_system_projects");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "rm -rf cadidate_test_system_projects");
	    	}
	    	runProcess(processBuilder);
    	} */
    	
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
	    	
	    	
	        
	        
    	}
    	else {
    		System.out.println("main directory already created, skipping...");
    	}
		
		
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@PutMapping("/test-launch")
	public ResponseEntity<?> runUserUnitTest(){
		
		return new ResponseEntity<>(null, HttpStatus.OK);
	}
	
	@PutMapping("/complete/{id}")
	public ResponseEntity<?> runAllUnitTests(@PathVariable("id") Long userTaskId, @Valid @RequestBody UserTaskRequest userTaskRequest){
		
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
	    // create main folder
	    	/*
	    	Path path = Paths.get(System.getProperty("user.home") + fs + "cadidate_test_system_projects");
	    	ProcessBuilder processBuilder = new ProcessBuilder();
	    	if(Files.exists(path)==false) {
		    	processBuilder.directory(new File(System.getProperty("user.home")));
		    	if (isWindows) {
		    		processBuilder.command("cmd.exe", "/c", "mkdir cadidate_test_system_projects");
		    	} 
		    	else if (isLinux){
		    		processBuilder.command("sh", "-c", "mkdir cadidate_test_system_projects");
		    	}
		
		        try {
		            Process process = processBuilder.start();
		            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		            String line;
		            while ((line = reader.readLine()) != null) {
		                System.out.println(line);
		            }
		            int exitCode = process.waitFor();
		            System.out.println("\nExited with error code : " + exitCode);
		        } catch (IOException | InterruptedException e) {
		            e.printStackTrace();
		            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		        }
	    	} else {
	    		System.out.println("main directory already created, skipping...");
	    	} */
	    	
	    // create project folder
		    processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects"));
		    if (isWindows) {
		    	processBuilder.command("cmd.exe", "/c", "mkdir " + projectKey);
		    } 
		    else if (isLinux){
		    	processBuilder.command("sh", "-c", "mkdir " + projectKey);
		    }
		
		    try {
		        Process process = processBuilder.start();
		        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		        String line;
		        while ((line = reader.readLine()) != null) {
		            System.out.println(line);
		        }
		        int exitCode = process.waitFor();
		        System.out.println("\nExited with error code : " + exitCode);
		        System.out.println("Created project directory");
		    } catch (IOException | InterruptedException e) {
		        e.printStackTrace();
		        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
		    }
	    
	     // init maven project
	        processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + projectKey));
	        String mvnInitCommand = "mvn archetype:generate -DgroupId=com.cleverhire" + " -DartifactId=java_project"+ projectKey 
	        		+ " -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false";
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", mvnInitCommand);
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", mvnInitCommand);
	    	}	        
	        
	        try {
	            Process process = processBuilder.start();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	            String line;
	            while ((line = reader.readLine()) != null) {
	                System.out.println(line);
	            }
	            int exitCode = process.waitFor();
	            System.out.println("\nExited with error code : " + exitCode);
	        } catch (IOException | InterruptedException e) {
	            e.printStackTrace();
	            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	        
	     // remove AppTest.java
	        processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + projectKey + fs 
	        		+ "java_project"+ projectKey + fs + "src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire"));
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "del AppTest.java");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "rm AppTest.java");
	    	}
	        try {
	            Process process = processBuilder.start();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	            String line;
	            while ((line = reader.readLine()) != null) {
	                System.out.println(line);
	            }
	            int exitCode = process.waitFor();
	            System.out.println("\nExited with error code : " + exitCode);
	        } catch (IOException | InterruptedException e) {
	            e.printStackTrace();
	            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);  
	        }
	        
	     // edit pom.xml
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        try (InputStream is = new FileInputStream(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + projectKey + fs 
	        		+ "java_project"+ projectKey + fs + "pom.xml")) {
	            DocumentBuilder db = dbf.newDocumentBuilder();
	            Document doc = db.parse(is);
	            
	            // add properties 
	            Node project = doc.getFirstChild();
	            Element properties = doc.createElement("properties");
	            Element encoding = doc.createElement("project.build.sourceEncoding");
	            Element mavenComplierSource = doc.createElement("maven.compiler.source");
	            Element target = doc.createElement("maven.compiler.target");
	            encoding.setTextContent("UTF-8");
	            mavenComplierSource.setTextContent("17");
	            target.setTextContent("17");
	            properties.appendChild(encoding);
	            properties.appendChild(mavenComplierSource);
	            properties.appendChild(target);
	            project.appendChild(properties);
	            
	            // update to JUnit 5
	            Node jUnitGroupId = doc.getElementsByTagName("groupId").item(1);
	            Node jUnitArtifactId = doc.getElementsByTagName("artifactId").item(1);
	            Node jUnitVersion = doc.getElementsByTagName("version").item(1);
	            jUnitGroupId.setTextContent("org.junit.jupiter");
	            jUnitArtifactId.setTextContent("junit-jupiter-api");
	            jUnitVersion.setTextContent("5.9.2");
	            
	            // add maven plugins
	            Element build = doc.createElement("build");
	            Element pluginManagement = doc.createElement("pluginManagement");
	            Element plugins = doc.createElement("plugins");
	            Element compilerPlugin = doc.createElement("plugin");
	            Element compilerPluginArtifactId = doc.createElement("artifactId");
	            Element compilerPluginVersion = doc.createElement("version");
	            Element sureFirePlugin = doc.createElement("plugin");
	            Element sureFirePluginArtifactId = doc.createElement("artifactId");
	            Element sureFirePluginVersion = doc.createElement("version");
	            compilerPluginArtifactId.setTextContent("maven-compiler-plugin");
	            compilerPluginVersion.setTextContent("3.8.0");
	            sureFirePluginArtifactId.setTextContent("maven-surefire-plugin");
	            sureFirePluginVersion.setTextContent("3.1.0");
	            compilerPlugin.appendChild(compilerPluginArtifactId);
	            compilerPlugin.appendChild(compilerPluginVersion);
	            sureFirePlugin.appendChild(sureFirePluginArtifactId);
	            sureFirePlugin.appendChild(sureFirePluginVersion);
	            plugins.appendChild(compilerPlugin);
	            plugins.appendChild(sureFirePlugin);
	            pluginManagement.appendChild(plugins);
	            build.appendChild(pluginManagement);
	            project.appendChild(build);
	            	           
	            try (FileOutputStream output = new FileOutputStream(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs 
	            		+ projectKey + fs + "java_project"+ projectKey + fs + "pom.xml")) {
	           
	        		TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        		Transformer transformer = transformerFactory.newTransformer();
	        		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        		transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
	        		DOMSource source = new DOMSource(doc);
	        		StreamResult result = new StreamResult(output);
	        		transformer.transform(source, result);
	            	
	            }
	
	        } catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
		       e.printStackTrace();
		       throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	        
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
							+ "java_project"+ projectKey + fs + "src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire"
							+ fs + className +".java");
					fos.write(codeDecoded.getBytes());
			        fos.flush();
			        fos.close(); 
				} catch (IOException e) {
					e.printStackTrace();
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
				}
	        }
	        
	     // create test files
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

		        // create className.java
		        FileOutputStream fos;
				try {
					fos = new FileOutputStream(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + projectKey + fs 
							+ "java_project"+ projectKey + fs + "src" + fs + "test" + fs + "java" + fs + "com" + fs + "cleverhire"
							+ fs + className +".java");
					fos.write(codeDecoded.getBytes());
			        fos.flush();
			        fos.close(); 
				} catch (IOException e) {
					e.printStackTrace();
					throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
				}
	        }
	        
	    // run maven test
	        processBuilder.directory(new File(System.getProperty("user.home") + fs + "cadidate_test_system_projects" + fs + projectKey + fs 
	        		+ "java_project"+ projectKey));
	        String mvnTestCommand = "mvn test";
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", mvnTestCommand);
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", mvnTestCommand);
	    	}
	    	
	    	String testResult="";
	        try {
	            Process process = processBuilder.start();
	            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	            String line;
	            while ((line = reader.readLine()) != null) {
	            	testResult = testResult + "\n" + line;
	                System.out.println(line);
	            }
	            int exitCode = process.waitFor();
	            System.out.println("\nExited with error code : " + exitCode);
	        } catch (IOException | InterruptedException e) {
	            e.printStackTrace();
	            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	        String encodedtestResult = Base64.getEncoder().encodeToString(testResult.getBytes(StandardCharsets.ISO_8859_1));
	        newUserTask.setResultReport(encodedtestResult);
	        
	     // parse test results
	        Integer overallTestCount = 0;
	        if(testResult.contains("COMPILATION ERROR")) {
	        	newUserTask.setCompilationResult("FAIL");
	        	// number of tests unknown, set failure rate to 100%
	        	overallTestCount = 1;
	        	newUserTask.setOverallTestsCount(overallTestCount);
	        }
	        else {
		     	Integer testsPassed=0;
		     	Integer testsFailed=0;
		     	int resultsStartIndex = testResult.indexOf("Results");
		     	String total = testResult.substring(resultsStartIndex, resultsStartIndex + 80);
		     	int testsRunIndex = total.indexOf("Tests run");
		     	int testsFailIndex = total.indexOf("Failures");
		     	String testsRunSubstr = total.substring(testsRunIndex + 11, testsRunIndex + 12);
		     	String testsFailSubstr = total.substring(testsFailIndex + 10, testsFailIndex + 11);
		     	overallTestCount = Integer.parseInt(testsRunSubstr);
		     	testsFailed = Integer.parseInt(testsFailSubstr);
		     	testsPassed = overallTestCount - testsFailed;
		     	newUserTask.setTestsPassed(testsPassed);
		     	newUserTask.setTestsFailed(testsFailed);
		     	newUserTask.setOverallTestsCount(overallTestCount);
		     	newUserTask.setCompilationResult("OK");
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
	
	public void runProcess(ProcessBuilder processBuilder) {
        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);  
        }

	}
	
}
