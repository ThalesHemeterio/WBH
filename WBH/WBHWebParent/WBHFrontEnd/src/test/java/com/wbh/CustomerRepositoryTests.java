package com.wbh;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import com.wbh.common.entity.Customer;
import com.wbh.customer.CustomerRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTests {

	@Autowired private CustomerRepository repo;

	@Test
	public void testCreateCustomer() {
		Customer customer = new Customer();
		customer.setAddressLine1("584 Maranhao");
		customer.setAddressLine2("Jardim Nova America");
		customer.setCreatedTime(new Date());
		customer.setDob("03/05/1998");
		customer.setEmail("giovanna@gmail.com");
		customer.setFirstName("Giovanna");
		customer.setLastName("Morais");
		customer.setPassword("12345678");
		customer.setPostalCode("D35 MG37");
		customer.setCity("Dublin");
		customer.setCountry("Ireland");
		customer.setContact("123 456 7890");

		Customer savedCustomer = repo.save(customer);
		
		assertThat(savedCustomer).isNotNull();
		assertThat(savedCustomer.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListCustomers() {
		Iterable<Customer> customers = repo.findAll();
		customers.forEach(System.out::println);
		
		assertThat(customers).hasSizeGreaterThan(1);
	}
	
	@Test 
	public void testUpdateCustomer() {
		Integer customerId =1;
		String lastname = "Hemeterio";
		
		Customer customer = repo.findById(customerId).get();
		customer.setLastName(lastname);
		customer.setEnabled(true);
		
		Customer updatedCustomer = repo.save(customer);
		assertThat(updatedCustomer.getLastName()).isEqualTo(lastname);
		
	}
	
	@Test
	public void testGetCustomer() {
		Integer customerId =2;
		Optional<Customer> findById = repo.findById(customerId);
		
		assertThat(findById).isPresent();
		
		Customer customer =findById.get();
		System.out.println(customer);
	}
	
	@Test
	public void testDeleteCustomer() {
		Integer customerId = 2;
		repo.deleteById(customerId);
		
		Optional<Customer> findById = repo.findById(customerId);
		assertThat(findById).isNotPresent();
	}
	
	@Test
	public void testFindByEmail() {
		String email = "giovanna@gamil";
		Customer customer = repo.findByEmail(email);
		
		assertThat(customer).isNotNull();
		System.out.println(customer);
	}
	
	@Test
	public void findByVerificationCode() {
		String code = "code_123";
		Customer customer = repo.findByVerificationCode(code);
		
		assertThat(customer).isNotNull();
		System.out.println(customer);
	}
	
	@Test
	public void testEnableCustomer() {
		Integer customerId=3;
		repo.enable(customerId);
		
		Customer customer = repo.findById(customerId).get();
		assertThat(customer.isEnabled()).isTrue();
	}
	
	
}
