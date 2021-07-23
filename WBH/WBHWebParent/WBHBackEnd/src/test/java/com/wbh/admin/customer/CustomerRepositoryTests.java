package com.wbh.admin.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import com.wbh.admin.customer.CustomerRepository;
import com.wbh.common.entity.Customer;

@DataJpaTest(showSql = true)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTests {

	@Autowired private CustomerRepository repo;
		
	@Test
	public void testCreateCustomer() {
		Customer customer = new Customer("thales@customer.com", "123456", "Thales", "Hemeterio", "23/09/1990", "123 456 7890",
				"Street 1", "County X", "Z14 Z567", "Dublin", "Ireland");

		customer.setEnabled(true);
		Customer savedCustomer = repo.save(customer);
		
		assertThat(savedCustomer.getId()).isGreaterThan(0);
		
	}
	
	
	@Test
	public void testListAllUsers() {
		Iterable<Customer> listUsers = repo.findAll();
		
		listUsers.forEach(user -> System.out.println(user));
	}
	
	@Test
	public void getCustomerById() {
		Customer userThales = repo.findById(2).get();
		System.out.println(userThales);
		assertThat(userThales).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetails() {
		Customer userThales = repo.findById(2).get();
		userThales.setEnabled(false);
		userThales.setLastName("Morais");
		repo.save(userThales);
	}
	

	
	@Test
	public void deleteUser() {
		Integer userId = 2;
		repo.deleteById(userId);
	}
	
	
	@Test
	public void testGetUserByEmail() {
		String email = "giovanna@gmail.com";
		Customer user=repo.getCustomerByEmail(email);
		
		assertThat(user).isNotNull();
	}
	
	
	@Test
	public void testCountById() {
		Integer id=1;
		Long countById =repo.countById(id);
		
		assertThat(countById).isNotNull().isGreaterThan(0);
	}
	
	
	@Test
	public void testEnableUser() {
		Integer id=1;
		repo.updateEnableStatus(id, true);
		
	}
	
	@Test
	public void testDisableUser() {
		Integer id=3;
		repo.updateEnableStatus(id, false);
		
	}
	
	@Test 
	public void testListFirstPage() {
		int pageNumber = 1;
		int pageSize = 4;
		Pageable pegeable = PageRequest.of(pageNumber,pageSize);
		Page<Customer> page = repo.findAll(pegeable);
		
		List<Customer> listUsers = page.getContent();
		
		listUsers.forEach(customer -> System.out.println(customer));
		
		assertThat(listUsers.size()).isEqualTo(pageSize);
	}
	
	
	/*
	@Test
	public void testSearchUsers() {
		String keyword = "Thales";
		int pageNumber = 0;
		int pageSize = 4;
		Pageable pegeable = PageRequest.of(pageNumber,pageSize);
		Page<User> page = repo.findAll(keyword,pegeable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isGreaterThan(0);
	}

	@Test
	public void getUserById() {
		User userThales = repo.findById(1).get();
		System.out.println(userThales);
		userThales.setEnabled(true);
		assertThat(userThales).isNotNull();
	}
	
	@Test
	public void testCreateUser() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User userAmdin = new User("thales@admin.com", "123456", "Thales", "Hemeterio",  "23/03/1992", "083 067 8284", "34,Royston Kimmage");
		userAmdin.addRole(roleAdmin);
		userAmdin.setEnabled(true);
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String rawPassword = "123456";
		String encodedPassword = passwordEncoder.encode(rawPassword);
		userAmdin.setPassword(encodedPassword);
		
		repo.save(userAmdin);

	}
	*/	
}
