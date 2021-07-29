package com.wbh.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wbh.common.entity.Customer;
import com.wbh.common.entity.User;
import com.wbh.customer.CustomerRepository;
import com.wbh.user.UserRepository;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

	//@Autowired private CustomerRepository repo;
	@Autowired private UserRepository repo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		//Customer customer = repo.getUserByEmail(email);
		User customer = repo.getUserByEmail(email);
		if(customer != null) {
			return new CustomerUserDetails(customer);
		}
		throw new UsernameNotFoundException("No customer found with the email: "+ email);
	}

}
