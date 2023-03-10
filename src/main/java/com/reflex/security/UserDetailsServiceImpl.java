package com.reflex.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reflex.model.User;
import com.reflex.repository.UserRepository;



@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	  @Autowired
	  UserRepository userRepository;

	  /*
	  @Override
	  @Transactional
	  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    User user = userRepository.findByuserName(username)
	        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

	    return UserDetailsImpl.build(user);
	  } */
	  
	  @Override
	  @Transactional
	  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	    User user = userRepository.findByemail(email)
	        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

	    return UserDetailsImpl.build(user);
	    
	  }
	  
}


