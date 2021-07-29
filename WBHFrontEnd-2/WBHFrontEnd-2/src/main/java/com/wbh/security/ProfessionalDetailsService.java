package com.wbh.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wbh.common.entity.Professional;
import com.wbh.common.entity.User;
import com.wbh.professional.ProfessionalRepository;
import com.wbh.user.UserRepository;

@Service
public class ProfessionalDetailsService implements UserDetailsService {

	@Autowired private ProfessionalRepository repo;
	//@Autowired private UserRepository repo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Professional professional = repo.getUserByEmail(email);
		//User professional = repo.getUserByEmail(email);
		System.out.println(professional);
		if(professional == null)
			throw new UsernameNotFoundException("No professional found with the email: "+ email);
		
		return new ProfessionalUserDetails(professional);
	}

}
