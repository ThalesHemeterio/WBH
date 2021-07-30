package com.wbh.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.wbh.common.entity.Professional;
import com.wbh.professional.ProfessionalRepository;

public class ProfessionalDetailsService implements UserDetailsService {

	@Autowired private ProfessionalRepository repo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Professional professional = repo.findByEmail(email);
		if (professional == null) 
			throw new UsernameNotFoundException("No professional found with the email " + email);
		
		return new ProfessionalUserDetails(professional);
	}

}
