package com.wbh.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wbh.common.entity.Customer;

@Service
public class CustomerService {

	
	@Autowired private CustomerRepository customerRepo;
	
	public boolean isEmailUnique(String email) {
		Customer customer = customerRepo.findByEmail(email);
		return customer ==null;
	}
}
