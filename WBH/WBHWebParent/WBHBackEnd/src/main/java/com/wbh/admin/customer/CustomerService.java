package com.wbh.admin.customer;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wbh.common.entity.Customer;

@Service
@Transactional
public class CustomerService {

	public static final int USERS_PER_PAGE =4;
	
	@Autowired 
	private CustomerRepository customerRepo;
	
	@Autowired 
	private PasswordEncoder passwordEncoder;
	
	public Customer getByEmail(String email) {
		return customerRepo.getCustomerByEmail(email);
	}
	
	public List<Customer> listAll(){
		return (List<Customer>) customerRepo.findAll();
	}
	
	public Page<Customer> listByPage(int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);	
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum -1, USERS_PER_PAGE, sort);
		
		if(keyword != null) {
			return customerRepo.findAll(keyword, pageable);
		}
		return customerRepo.findAll(pageable);
	}
	

	public Customer save(Customer customer) {
		boolean isUpdatingCustomer = (customer.getId()!=null);								
		
		if(isUpdatingCustomer) {														// checking if you're are editing or creating a user if the same email.
			Customer existingCustomer = customerRepo.findById(customer.getId()).get();
			
			if(customer.getPassword().isEmpty()) {                                      // password is not required in changing you can keep the same just not fill the field
				customer.setPassword(existingCustomer.getPassword());                
			}else {
				encodePassword(customer);
			}
		}else {
			encodePassword(customer);
		}
		return customerRepo.save(customer);
	}
	
	public void encodePassword(Customer customer) {											// Function responsible for encoding the password 		
		String encodedPassowrd = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassowrd);
		
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		Customer customerByEmail = customerRepo.getCustomerByEmail(email);
		
		if(customerByEmail == null) return true;
		
		boolean isCreatingNew = (id==null);
		
		if(isCreatingNew) {                                   // checking if you're are editing or creating a user if the same email.
			if(customerByEmail !=null) return false;
			} else {
				if(customerByEmail.getId() != id) {
				return false;
			}
		}
		return true;
	}

	public Customer get(Integer id) throws CustomerNotFoundException {
		try {
			return customerRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CustomerNotFoundException("Could not find any user with ID: "+ id);
		}
	}
	
	public void delete(Integer id) throws CustomerNotFoundException {
		Long countById = customerRepo.countById(id);
		if(countById == null || countById == 0) {
			throw new CustomerNotFoundException("Could not find any user with ID: "+ id);
		}
		
		customerRepo.deleteById(id);
	}
	
	public Customer updateAccount(Customer customerInForm) {     // updates an account of a logged user
		Customer customerInDB = customerRepo.findById(customerInForm.getId()).get();
		
		if(!customerInForm.getPassword().isEmpty()) {
			customerInDB.setPassword(customerInForm.getPassword());
			encodePassword(customerInDB);
		}
		
		if(customerInForm.getPhotos() !=null) {
			customerInDB.setPhotos(customerInForm.getPhotos());
		}

		customerInDB.setFirstName(customerInForm.getFirstName());
		customerInDB.setLastName(customerInForm.getLastName());
		return customerRepo.save(customerInDB);
	}
	
	public void updateCustomerEnabledStatus(Integer id, boolean enabled) {   // function to update enabled/disabled status of the user
		customerRepo.updateEnableStatus(id, enabled);
	}
}
