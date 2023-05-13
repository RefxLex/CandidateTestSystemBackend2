package com.reflex.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.TransformerException;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reflex.model.UserTask;
import com.reflex.repository.UserTaskRepository;
import com.reflex.response.SonarTokenResponse;



@CrossOrigin
@RestController
@RequestMapping("/api/metrics")
public class CodeAnalysisController {
	
	@Value("${sonarUrl}")
	private String sonarUrl;
	
	@Value("${sonarLogin}")
	private String sonarLogin;
	
	@Value("${sonarPassword}")
	private String sonarPassword;
	
	private static final int timeout=60000;
	
	@Autowired
	UserTaskRepository userTaskRepository;
	
    private static void writeXml(Document doc, OutputStream output) 
    		throws UnsupportedEncodingException, TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(output);
		transformer.transform(source, result);
	}	
	
	@PutMapping("/run")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> runCodeAnalysis(@RequestParam (name="id") long userTaskId) 
			throws IOException, URISyntaxException, TransformerException{
		Optional <UserTask> userTask = userTaskRepository.findById(userTaskId);
		if(userTask.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user task found with id=" + userTaskId);
		}
		UserTask newUserTask = userTask.get();
		
		if(userTask.get().isAnalyzed()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Task already analyzed.");
		}
		else {
			
			RequestConfig requestConfig = RequestConfig.custom().
				    setConnectionRequestTimeout(timeout).setConnectTimeout(timeout).setSocketTimeout(timeout).build();
			HttpClientBuilder builder = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig);
			
			String projectKey = "";
	        HttpPost post = new HttpPost();
	        String auth = sonarLogin + ":" + sonarPassword;
	        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.ISO_8859_1));
	        String authHeader = "Basic " + new String(encodedAuth);
	        post.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
	        
			// create new project
			String projectName = "_" + userTask.get().getId().toString();
			projectKey = "_" + userTask.get().getId().toString();
		    URI createProjectUri = new URI(sonarUrl + "/api/projects/create?name=" + projectName + "&project=" + projectKey);
		    post.setURI(createProjectUri);
		        
		    try (CloseableHttpClient httpClient = HttpClients.createDefault();
			        CloseableHttpResponse response = httpClient.execute(post)) {	        	
			       	int statusCode = response.getStatusLine().getStatusCode();
			        if(statusCode == 200) {		        	
	
			        }
			        else {
			        	System.err.println(response.getStatusLine());
			        	throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Sonar qube error" + response.getStatusLine().getReasonPhrase());
			        }                
			}
			catch(HttpHostConnectException exception) {
			    throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error connecting to sonar qube");
			}
		            
	        // acciure token for analysis
		    String sonarAuthToken = "";
	        String generatedTokenName = RandomStringUtils.random(10, true, true);
	        String tokenName = projectKey + generatedTokenName;
	        URI createTokenUri = new URI(sonarUrl + "/api/user_tokens/generate?login=" + sonarLogin + "&name=" + tokenName);
	        post.setURI(createTokenUri);
			String result="";
			ObjectMapper mapper = new ObjectMapper();
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		             CloseableHttpResponse response = httpClient.execute(post)) {	        	
		        	int statusCode = response.getStatusLine().getStatusCode();
		        	if(statusCode == 200) {		        	
			            result = EntityUtils.toString(response.getEntity());
			            SonarTokenResponse tokenResponse = mapper.readValue(result, SonarTokenResponse.class);
			            sonarAuthToken = tokenResponse.getToken();
		        	}
		        	else {
		        		System.err.println(response.getStatusLine());
		        		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Sonar qube error" + response.getStatusLine().getReasonPhrase());
		        	}          
		        }
		    catch(HttpHostConnectException exception) {
		    	throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Error connecting to sonar qube");
		    }
			
		
			// define OS
			String fs = System.getProperty("file.separator");
	    	boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
	    	boolean isLinux = System.getProperty("os.name").toLowerCase().startsWith("linux");
	    	
	    	// create project folder
	    	ProcessBuilder processBuilder = new ProcessBuilder();
	    	processBuilder.directory(new File(System.getProperty("user.home")));
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "mkdir sonar_projects" + fs + projectKey);
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "mkdir sonar_projects" + fs + projectKey);
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
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        
	        // init maven project
	        processBuilder.directory(new File(System.getProperty("user.home") + fs + "sonar_projects" + fs + projectKey));
	        String mvnInitCommand = "mvn archetype:generate -DgroupId=com.cleverhire" + " -DartifactId=java-project"+ projectKey 
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
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        
	        // remove unnecessary file
	        processBuilder.directory(new File(System.getProperty("user.home") + fs + "sonar_projects" + fs + projectKey + fs 
	        		+ "java-project"+ projectKey + fs + "src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire"));
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", "del App.java");
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", "rm App.java");
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
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        
	        // add package
	        String codeBase64 = userTask.get().getCode();
	        byte[] decodedBytes = Base64.getDecoder().decode(codeBase64);
	        String codeDecoded = new String(decodedBytes);
	        codeDecoded = "package com.cleverhire;\n" + codeDecoded;
	        
	        // create Main.java
	        FileOutputStream fos = new FileOutputStream(System.getProperty("user.home") + fs + "sonar_projects" + fs + projectKey + fs 
	        		+ "java-project"+ projectKey + fs + "src" + fs + "main" + fs + "java" + fs + "com" + fs + "cleverhire"
	        		+ fs +"Main.java");
	        fos.write(codeDecoded.getBytes());
	        fos.flush();
	        fos.close();
	        
	        // edit pom.xml
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        try (InputStream is = new FileInputStream(System.getProperty("user.home") + fs + "sonar_projects" + fs + projectKey + fs 
	        		+ "java-project"+ projectKey + fs + "pom.xml")) {
	            DocumentBuilder db = dbf.newDocumentBuilder();
	            Document doc = db.parse(is);
	            
	            Node project = doc.getFirstChild();
	            Element properties = doc.createElement("properties");
	            Element encoding = doc.createElement("project.build.sourceEncoding");
	            encoding.setTextContent("UTF-8");
	            Element source = doc.createElement("maven.compiler.source");
	            source.setTextContent("11");
	            Element target = doc.createElement("maven.compiler.target");
	            target.setTextContent("11");
	            properties.appendChild(encoding);
	            properties.appendChild(source);
	            properties.appendChild(target);
	            project.appendChild(properties);
	
	            try (FileOutputStream output = new FileOutputStream(System.getProperty("user.home") + fs + "sonar_projects" + fs 
	            		+ projectKey + fs + "java-project"+ projectKey + fs + "pom.xml")) {
	            	writeXml(doc, output);
	            }
	
		   } catch (ParserConfigurationException | SAXException
		           | IOException | TransformerException e) {
		       e.printStackTrace();
		   }
	        
	       // run sonar analysis
	        processBuilder.directory(new File(System.getProperty("user.home") + fs + "sonar_projects" + fs + projectKey + fs 
	        		+ "java-project"+ projectKey));
	        String mvnRunAnalisysCommand = "mvn clean verify sonar:sonar -Dsonar.projectKey=" + projectKey 
	        		+ " -Dsonar.host.url="+ sonarUrl + " -Dsonar.login=" + sonarAuthToken;
	        
	    	if (isWindows) {
	    		processBuilder.command("cmd.exe", "/c", mvnRunAnalisysCommand);
	    	} 
	    	else if (isLinux){
	    		processBuilder.command("sh", "-c", mvnRunAnalisysCommand);
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
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        
		    newUserTask.setSonarKey(projectKey);
		    newUserTask.setAnalyzed(true);
		    userTaskRepository.save(newUserTask);
        
		}
		return new ResponseEntity<>(null, HttpStatus.OK);
		
	}
	
}
