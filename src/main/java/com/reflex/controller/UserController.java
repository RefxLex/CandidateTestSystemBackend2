package com.reflex.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import org.springframework.web.server.ResponseStatusException;

import com.reflex.mail.EmailUtil;
import com.reflex.model.Role;
import com.reflex.model.User;
import com.reflex.model.UserTask;
import com.reflex.model.enums.ERole;
import com.reflex.model.enums.UserStatus;
import com.reflex.repository.RoleRepository;
import com.reflex.repository.UserRepository;
import com.reflex.repository.UserTaskRepository;
import com.reflex.request.SignupRequest;
import com.reflex.response.MessageResponse;
import com.reflex.response.UserProfileResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.Properties;

import jakarta.mail.Session;


@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Value("${smtpDomain}")
	private String smtpDomain;
	
	@Value("${webURL}")
	private String webURL;
	
	@Value("${scheduleMeetingEmployeeEmail}")
	private String scheduleMeetingEmployeeEmail;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	UserTaskRepository userTaskRepository;
	
	@Autowired
	PasswordEncoder encoder;
	
    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponse> getUserById(@PathVariable("id") Long id) {
        Optional<User> user = Optional.ofNullable(userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No such User")));
        
        UserProfileResponse userProfile = new UserProfileResponse(
        	user.get().getId(),
        	user.get().getEmail(),
        	user.get().getUserName(),
        	user.get().getFullName(),
        	user.get().getPhone(),
        	user.get().getInfo(),
        	user.get().getUserStatus(),
        	user.get().getLastActivity(),
        	user.get().getLastScore());
        
        return new ResponseEntity<>(userProfile, HttpStatus.OK);
    }
    
    @GetMapping("/by-user-task/{userTaskId}")
    public ResponseEntity<UserProfileResponse> getUserByUserTaskId(@PathVariable("userTaskId") Long userTaskId) {
    	Optional<UserTask> userTask = userTaskRepository.findById(userTaskId);
    	if (userTask.isEmpty()) {
    		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found");
    	}
    	User user = userTask.get().getUser();
        UserProfileResponse userProfile = new UserProfileResponse(
        	user.getId(),
        	user.getEmail(),
        	user.getUserName(),
        	user.getFullName(),
        	user.getPhone(),
        	user.getInfo(),
        	user.getUserStatus(),
        	user.getLastActivity(),
        	user.getLastScore());
        return new ResponseEntity<>(userProfile, HttpStatus.OK);
    }
    
    
    @GetMapping("/filter")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUserByStatus(
    		@RequestParam String status,
    		@RequestParam String full_name){
    	    	
    	List<User> users = new ArrayList<>();
    	if( (status!="") && (full_name!="")) {
    		users = userRepository.selectByUserStatusAndUserName(full_name, status);
    	}else if ( (status=="") && (full_name=="") ) {
    		users = userRepository.selectAll();
    	}else if ( (status!="") && (full_name=="") ) {
    		users = userRepository.selectByUserStatus(status);
    	}else if ( (status=="") && (full_name!="") ) {
    		users = userRepository.selectByUserName(full_name);
    	}
	    return new ResponseEntity<>(users, HttpStatus.OK);	
    }
    
    @GetMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> findAdmins(){
    	List<User> userList = userRepository.selectByUserStatusNone();
    	return new ResponseEntity<>(userList,HttpStatus.OK);
    }
    
	@PostMapping("/create")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	  public ResponseEntity<?> createUser(@Valid @RequestBody SignupRequest signUpRequest) {
		  
	    if (userRepository.existsByemail(signUpRequest.getEmail())) {
	      return ResponseEntity.badRequest().body(new MessageResponse("Email is already in use!"));
	    }
	    if(userRepository.existsByphone(signUpRequest.getPhone())) {
	    	return ResponseEntity.badRequest().body(new MessageResponse("Phone is already in use!"));
	    }
	    
        int passwordLength = 10 + new Random().nextInt(15 - 5 + 1);
        String generatedPassword = RandomStringUtils.random(passwordLength, true, true);
        String generatedUserName = RandomStringUtils.random(7, true, true);
	    
	    User user = new User(
	    		generatedUserName,
	            signUpRequest.getEmail(),
	            encoder.encode(generatedPassword),
	            signUpRequest.getFullName(),
	            signUpRequest.getPhone(),
	            signUpRequest.getInfo(),
	            UserStatus.invited.name());

	    Set<Role> roles = new HashSet<>();
	    Role userRole = roleRepository.findByname(ERole.ROLE_USER)
	              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	          roles.add(userRole);

	    user.setRoles(roles);

	    
	    // send email
	    // include in production testing
	    /*
	    String smtpHostServer = smtpDomain;
	    String emailID = signUpRequest.getEmail();
	    String subject = "Тестирование профессиональных навыков";
	    String body = "Здравствуйте, вы были приглашены на отбор для вакансии в компанию, так как отправляли нам своё резюме." +
	    "\nОтбор заключается в выполнении тестовых заданий на сайте "+ webURL +
	    "\nДля входа в систему используйте эту электронную почту и пароль из данного письма." + "/nПароль: " + generatedPassword;
	    
	    Properties props = System.getProperties();
	    props.put("mail.smtp.host", smtpHostServer);
	    Session session = Session.getInstance(props, null);
	    EmailUtil.sendEmail(session, emailID, subject, body); */
	    
	    return new ResponseEntity<>(userRepository.save(user), HttpStatus.CREATED);
	  }
	
	  @PostMapping("/create/admin")
	  @PreAuthorize("hasRole('ADMIN')")
	  public ResponseEntity<User> createAdmin(@RequestParam String role, @Valid @RequestBody SignupRequest signUpRequest){
		    if (userRepository.existsByemail(signUpRequest.getEmail())) {
			    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use!");
			}
			if(userRepository.existsByphone(signUpRequest.getPhone())) {
			    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Phone is already in use!");
			}
			
	        int passwordLength = 10 + new Random().nextInt(15 - 5 + 1);
	        String generatedPassword = RandomStringUtils.random(passwordLength, true, true);
	        String generatedUserName = RandomStringUtils.random(7, true, true);
		    
		    User user = new User(
		    		generatedUserName,
		            signUpRequest.getEmail(),
		            encoder.encode(generatedPassword),
		            signUpRequest.getFullName(),
		            signUpRequest.getPhone(),
		            signUpRequest.getInfo(),
		            UserStatus.none.name());

		    Set<Role> roles = new HashSet<>();
		    Role userRole = roleRepository.selectByName(role)
		              .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Couldn't find role " + role));
		          roles.add(userRole);
		    user.setRoles(roles);
		    return new ResponseEntity<>(userRepository.save(user), HttpStatus.CREATED);
	  }
	
	  @PutMapping("/status/{id}")
	  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")	
	  public ResponseEntity<?> updateUserStatus(@PathVariable("id") Long id, @RequestParam String status){
		  Optional<User> oldUser = userRepository.findById(id);
		  if(oldUser.isEmpty()) {
			  throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with id= " + id);
		  }
		  if(UserStatus.byNameIgnoreCase(status).isEmpty()) {
			  throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user status");
		  }
		  User newUser = oldUser.get();
		  newUser.setUserStatus(status);
		  userRepository.save(newUser);
		  
		  // sent email
		  // include in production testing
		  /*
		  String smtpHostServer = smtpDomain;
		  String userEmail = newUser.getEmail();
		  String subject = "Тестирование профессиональных навыков";
		  String bodyForUser = "";
		  String bodyForEmployee="";
		  Properties props = System.getProperties();
		  props.put("mail.smtp.host", smtpHostServer);
		  Session session = Session.getInstance(props, null);
		  if(newUser.getUserStatus()=="approved") {
			  bodyForUser = "Здравствуйте, вы прошли на отбор для вакансии в компанию, вас должны пригласить на собеседование в течение трёх дней";
			  bodyForEmployee = "Кандидату на вакансию" + newUser.getInfo() + "необходимо назначить собесодование." +
			  "\n ФИО " + newUser.getFullName() +
			  "\n эл.почта " + newUser.getEmail() + 
			  "\n тел." + newUser.getPhone();
			  EmailUtil.sendEmail(session, userEmail, subject, bodyForUser);
			  EmailUtil.sendEmail(session, scheduleMeetingEmployeeEmail, subject, bodyForEmployee);
		  }else if (newUser.getUserStatus()=="rejected") {
			  bodyForUser = "Здравствуйте, к сожалению вы не прошли на отбор для вакансию в компанию";
			  EmailUtil.sendEmail(session, userEmail, subject, bodyForUser);
		  } */
		  
		  return new ResponseEntity<>(HttpStatus.OK);
	  }
	
	  @PutMapping("/{id}")
	  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	  public ResponseEntity<?> updateUserProfile(@PathVariable("id") Long id, @Valid @RequestBody SignupRequest signUpRequest) {
	        Optional<User> oldUser = userRepository.findById(id);
	        if(oldUser.isEmpty()) {
	        	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user found with id= " + id);
	        }
	        
	        List<User> duplicateEmailList = userRepository.selectByEmailExcludeOneById(signUpRequest.getEmail(), id);
	        List<User> duplicatePhoneList = userRepository.selectByPhoneExcludeOneById(signUpRequest.getPhone(), id);
	        if(duplicateEmailList.isEmpty()==false) {
	        	return ResponseEntity.badRequest().body(new MessageResponse("Email is already in use!"));
	        }
	        if(duplicatePhoneList.isEmpty()==false) {
	        	return ResponseEntity.badRequest().body(new MessageResponse("Phone is already in use!"));
	        }

	        User newUser = oldUser.get();
	        newUser.setEmail(signUpRequest.getEmail());
	        newUser.setFullName(signUpRequest.getFullName());
	        newUser.setPhone(signUpRequest.getPhone());
	        newUser.setInfo(signUpRequest.getInfo());            
	        return new ResponseEntity<>(userRepository.save(newUser), HttpStatus.OK);        
	  }

	  @DeleteMapping("/{id}")
	  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	  public ResponseEntity<?> deleteUser(@PathVariable("id") Long id){
	  Optional<User> user = userRepository.findById(id);
		if (user.isPresent()) {
		  userRepository.deleteById(id);
		  return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
		  throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such User");
		}
	  }
	  
}
