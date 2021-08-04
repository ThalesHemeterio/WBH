package com.wbh.customer;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wbh.common.entity.User;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {

	
	@Autowired private CustomerRepository customerRepo;
	@Autowired private PasswordEncoder passwordEncoder;
	
	public List<User> listAll(){
		return (List<User>) customerRepo.findAll();
	}
	
	public boolean isEmailUnique(String email) {
		User customer = customerRepo.findByEmail(email);
		return customer ==null;
	}
	
	public void registerCustomer(User customer) {
		encodePassword(customer);
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		
		customerRepo.save(customer);
	}

	private void encodePassword(User customer) {
		String encodedPassword = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);
	}
	
	public boolean verify(String verificationCode) {
		User customer = customerRepo.findByVerificationCode(verificationCode);
		if(customer == null || customer.isEnabled()) {
			return false;
		}else {
			customerRepo.enable(customer.getId());
			return true;
		}
	}
}
