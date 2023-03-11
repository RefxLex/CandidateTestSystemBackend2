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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.reflex.model.Role;
import com.reflex.model.User;
import com.reflex.model.enums.ERole;
import com.reflex.model.enums.UserStatus;
import com.reflex.repository.RoleRepository;
import com.reflex.repository.UserRepository;
import com.reflex.request.SignupRequest;
import com.reflex.response.MessageResponse;
import com.reflex.response.UserProfileResponse;

import jakarta.validation.Valid;



@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;
	
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
        	user.get().getUserStatus().toString());
        
        return new ResponseEntity<>(userProfile, HttpStatus.OK);
    }
    
    @GetMapping("/filter")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserByStatus(
    		@RequestParam String status,
    		@RequestParam (defaultValue = "0") int page,
    		@RequestParam (defaultValue = "10") int size,
    		@RequestParam (required = false) String orderBy){
    	
    // TODO:check if user is deleted
    	
    	List<User> users = new ArrayList<User>();
    	Pageable paging = PageRequest.of(page, size);
    	Page<User> pageUsers;
    	
    	try {
    		
	    	if(status=="submitted" || status=="approved" || status=="rejected") {
	    		
	        	switch (orderBy) {
	    		case "name": {
	    			pageUsers = userRepository.findByuserStatusContainingOrderByfirstNameDesc(status, paging);
	    		}
	    		case "score": {
	    			// TODO: order by score
	    			System.out.println("order by score");
	    		}
	    		case "activity": {
	    			pageUsers = userRepository.findByuserStatusContainingOrderBylastActivityDesc(status, paging);
	    		}
	    		default:
	    			pageUsers = userRepository.findByuserStatusContainingOrderBylastActivityDesc(status, paging);
	    		}
	    		
	    	}else if(status=="invited" || status=="started" ) {
	    		
	    		pageUsers = userRepository.findByuserStatusContainingOrderByfirstNameDesc(status, paging);
	    	}
	    	else {
	    		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No such status=" + status);
	    	}
	    	
	    	users = pageUsers.getContent();
	    	
	        Map<String, Object> response = new HashMap<>();
	        response.put("tutorials", users);
	        response.put("currentPage", pageUsers.getNumber());
	        response.put("totalItems", pageUsers.getTotalElements());
	        response.put("totalPages", pageUsers.getTotalPages());
	        return new ResponseEntity<>(response, HttpStatus.OK);
    	}
    	catch (Exception e) {
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    	
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserByName(
		@RequestParam String name,
		@RequestParam (defaultValue = "0") int page,
		@RequestParam (defaultValue = "10") int size) {
			
	// TODO:check if user is deleted
    	
	    List<User> users = new ArrayList<User>();
	    Pageable paging = PageRequest.of(page, size);
	    Page<User> pageUsers;
	    try {
	    	pageUsers = userRepository.findByfullNameLikeOrderByfullNameDesc(name, paging);
	    	users = pageUsers.getContent();
	    	
	        Map<String, Object> response = new HashMap<>();
	        response.put("tutorials", users);
	        response.put("currentPage", pageUsers.getNumber());
	        response.put("totalItems", pageUsers.getTotalElements());
	        response.put("totalPages", pageUsers.getTotalPages());
	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }
    	catch (Exception e) {
    		throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
    	}
	
	}

	@PostMapping("/create")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	  public ResponseEntity<?> createUser(@Valid @RequestBody SignupRequest signUpRequest) {
		  
	    if (userRepository.existsByemail(signUpRequest.getEmail())) {
	      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
	    }
	    if(userRepository.existsByphone(signUpRequest.getPhone())) {
	    	return ResponseEntity.badRequest().body(new MessageResponse("Error: Phone is already in use!"));
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
	            UserStatus.INVITED );

	    Set<Role> roles = new HashSet<>();
	    Role userRole = roleRepository.findByname(ERole.ROLE_USER)
	              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	          roles.add(userRole);

	    user.setRoles(roles);
	    userRepository.save(user);

	    return ResponseEntity.ok(new MessageResponse("User created successfully!"));
	  }
	  
	  @PutMapping("/{id}")
	  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	    public ResponseEntity<User> updateUser(@PathVariable("id") Long id, @Valid @RequestBody SignupRequest signUpRequest) {
	        Optional<User> oldUser = Optional.ofNullable(userRepository.findById(id).orElseThrow(() ->
	                new ResponseStatusException(HttpStatus.NOT_FOUND, "No such User")));
	        if (oldUser.isPresent()) {
	            User newUser = oldUser.get();
	            newUser.setEmail(signUpRequest.getEmail());
	            newUser.setFullName(signUpRequest.getFullName());
	            newUser.setPhone(signUpRequest.getPhone());
	            newUser.setInfo(signUpRequest.getInfo());

	            return new ResponseEntity<>(userRepository.save(newUser), HttpStatus.OK);
	        } else {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such User");
	        }
	    }
	  // soft delete
	  @PutMapping("/delete/{id}")
	  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	  public ResponseEntity<User> deleteUser(@PathVariable("id") Long id){
	  Optional<User> oldUser = Optional.ofNullable(userRepository.findById(id).orElseThrow(() ->
		      new ResponseStatusException(HttpStatus.NOT_FOUND, "No such User")));
		if (oldUser.isPresent()) {
		  User newUser = oldUser.get();
		  newUser.setDeleted(true);
		  return new ResponseEntity<>(userRepository.save(newUser), HttpStatus.OK);
		} else {
		  throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such User");
		}
	  }
	  
    
}
