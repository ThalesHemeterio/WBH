package com.wbh.admin.customer;

import java.util.Date;
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

import com.wbh.common.entity.Role;
import com.wbh.common.entity.User;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerService {

	public static final int USERS_PER_PAGE = 4;

	@Autowired
	private CustomerRepository customerRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;

	// get user by Email
	public User getByEmail(String email) {
		return customerRepo.getCustomerByEmail(email);
	}

	// List all users
	public List<User> listAll() {
		return (List<User>) customerRepo.findAll();
	}

	// list all users with pagination
	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();

		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);

		if (keyword != null) {
			return customerRepo.findAll(keyword, pageable);
		}
		return customerRepo.findAll(pageable);
	}

	// save new customer
	public User save(User customer) {
		boolean isUpdatingCustomer = (customer.getId() != null);

		if (isUpdatingCustomer) { // checking if you're are editing or creating a user if the same email.
			User existingCustomer = customerRepo.findById(customer.getId()).get();

			if (customer.getPassword().isEmpty()) { // password is not required in changing you can keep the same just
													// not fill the field
				customer.setPassword(existingCustomer.getPassword());
			} else {
				encodePassword(customer);
			}
		} else {
			encodePassword(customer);
		}
		return customerRepo.save(customer);
	}

	// encode password
	public void encodePassword(User customer) { // Function responsible for encoding the password
		String encodedPassowrd = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassowrd);

	}

	// checks if email is unique
	public boolean isEmailUnique(Integer id, String email) {
		User customerByEmail = customerRepo.getCustomerByEmail(email);

		if (customerByEmail == null)
			return true;

		boolean isCreatingNew = (id == null);

		if (isCreatingNew) { // checking if you're are editing or creating a user if the same email.
			if (customerByEmail != null)
				return false;
		} else {
			if (customerByEmail.getId() != id) {
				return false;
			}
		}
		return true;
	}

	// get customer by id
	public User get(Integer id) throws CustomerNotFoundException {
		try {
			return customerRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CustomerNotFoundException("Could not find any user with ID: " + id);
		}
	}

	// delet customer
	public void delete(Integer id) throws CustomerNotFoundException {
		Long countById = customerRepo.countById(id);
		if (countById == null || countById == 0) {
			throw new CustomerNotFoundException("Could not find any user with ID: " + id);
		}

		customerRepo.deleteById(id);
	}

	// update customer account
	public User updateAccount(User customerInForm) { // updates an account of a logged user
		User customerInDB = customerRepo.findById(customerInForm.getId()).get();

		if (!customerInForm.getPassword().isEmpty()) {
			customerInDB.setPassword(customerInForm.getPassword());
			encodePassword(customerInDB);
		}

		if (customerInForm.getPhotos() != null) {
			customerInDB.setPhotos(customerInForm.getPhotos());
		}

		customerInDB.setFirstName(customerInForm.getFirstName());
		customerInDB.setLastName(customerInForm.getLastName());
		return customerRepo.save(customerInDB);
	}

	// update enabled status of customers
	public void updateCustomerEnabledStatus(Integer id, boolean enabled) { // function to update enabled/disabled status
																			// of the user
		customerRepo.updateEnableStatus(id, enabled);
	}

	public void registerCustomer(User customer) {
		encodePassword(customer);
		customer.setEnabled(false);
		customer.setCreatedTime(new Date());

		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);

		customerRepo.save(customer);
	}

	// enables the custumer to access the website after client verifying email
	// confirmation
	public boolean verify(String verificationCode) {
		User customer = customerRepo.findByVerificationCode(verificationCode);
		if (customer == null || customer.isEnabled()) {
			return false;
		} else {
			customerRepo.enable(customer.getId());
			return true;
		}
	}

	// list all users based on their Role professional
	public Page<User> listByPageByRole(int pageNum, String sortField, String sortDir, String keyword, Role role) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);

		if (keyword != null && role != null) {
			return customerRepo.findUserByRolesAndFirstNameOrLastNameOrEmailAllIgnoreCase(role, keyword, keyword,
					keyword, pageable);
		}
		return customerRepo.findUserByRoles(role, pageable);
	}
}
