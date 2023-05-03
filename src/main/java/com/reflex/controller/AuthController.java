package com.reflex.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.reflex.model.Role;
import com.reflex.model.User;
import com.reflex.model.enums.ERole;
import com.reflex.model.enums.UserStatus;
import com.reflex.repository.RoleRepository;
import com.reflex.repository.UserRepository;
import com.reflex.request.LoginRequest;
import com.reflex.request.SignupRequest;
import com.reflex.response.MessageResponse;
import com.reflex.response.UserInfoResponse;
import com.reflex.security.UserDetailsImpl;
import com.reflex.security.UserDetailsServiceImpl;
import com.reflex.security.jwt.JwtUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	  AuthenticationManager authenticationManager;

	  @Autowired
	  UserRepository userRepository;

	  @Autowired
	  RoleRepository roleRepository;

	  @Autowired
	  PasswordEncoder encoder;

	  @Autowired
	  JwtUtils jwtUtils;
	  
	  @Autowired
	  private UserDetailsServiceImpl userDetailsService;

	  @PostMapping("/signin")
	  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

	  /*
	  Authentication authentication = authenticationManager
	        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())); */
		  
	  Authentication authentication = authenticationManager
		        .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

	    SecurityContextHolder.getContext().setAuthentication(authentication);
	    
	    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
	    
	    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);
	    
	    List<String> roles = userDetails.getAuthorities().stream()
	        .map(item -> item.getAuthority())
	        .collect(Collectors.toList());
	    
	    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
	        .body(new UserInfoResponse(userDetails.getId(),
	                                   userDetails.getUsername(),
	                                   userDetails.getEmail(),
	                                   roles));
	  }
	  /*
	  @PostMapping("/signup")
	  public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		  
	    if (userRepository.existsByuserName(signUpRequest.getUsername())) {
	      return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
	    }
	    if (userRepository.existsByemail(signUpRequest.getEmail())) {
	      return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
	    }
	    if(userRepository.existsByphone(signUpRequest.getPhone())) {
	    	return ResponseEntity.badRequest().body(new MessageResponse("Error: Phone is already in use!"));
	    }
	    
	    User user = new User(
	    		signUpRequest.getUsername(),
	            signUpRequest.getEmail(),
	            encoder.encode(signUpRequest.getPassword()),
	            signUpRequest.getFirstName(),
	            signUpRequest.getSecondName(),
	            signUpRequest.getLastName(),
	            signUpRequest.getPhone(),
	            signUpRequest.getInfo(),
	            UserStatus.INVITED );

	    Set<Role> roles = new HashSet<>();
	    Role userRole = roleRepository.findByname(ERole.ROLE_USER)
	              .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
	          roles.add(userRole);

	    user.setRoles(roles);
	    userRepository.save(user);

	    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	  } */

	  @PostMapping("/signout")
	  public ResponseEntity<?> logoutUser() {
	    ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
	    return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
	        .body(new MessageResponse("You've been signed out!"));
	  }
	  
	  /*
	  @PostMapping("/refresh")
	  public ResponseEntity<?> refresh(HttpServletRequest request){
		
	  } */
	
}
