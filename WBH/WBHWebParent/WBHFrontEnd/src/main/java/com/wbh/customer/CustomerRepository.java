package com.wbh.customer;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.wbh.common.entity.Customer;
import com.wbh.common.entity.User;

public interface CustomerRepository extends CrudRepository<Customer, Integer> {
	
	@Query("SELECT c from Customer c WHERE c.email =?1")
	public Customer findByEmail(String email);
	
	@Query("SELECT c FROM Customer c WHERE c.email = :email")
	public Customer getUserByEmail(@Param("email") String email);
	
	@Query("SELECT c from Customer c WHERE c.verificationCode =?1")
	public Customer findByVerificationCode(String code);
	
	@Query("UPDATE Customer c SET c.enabled = true, c.verificationCode = null WHERE c.id =?1")
	@Modifying
	public void enable(Integer id);

}
