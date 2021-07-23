package com.wbh.admin.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wbh.common.entity.Customer;
import com.wbh.common.entity.User;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, Integer> {
	
	@Query("SELECT c from Customer c WHERE c.email =?1")
	public Customer findByEmail(String email);
	
	@Query("SELECT c from Customer c WHERE c.verificationCode =?1")
	public Customer findByVerificationCode(String code);
	
	@Query("UPDATE Customer c SET c.enabled = true WHERE c.id =?1")
	@Modifying
	public Customer enable(Integer id);
	
	@Query("SELECT c FROM Customer c WHERE c.email = :email")
	public Customer getCustomerByEmail(@Param("email") String email);
	
	public Long countById(Integer id);  // method use to delete users
	
	@Query("SELECT c FROM Customer c WHERE CONCAT(c.id, ' ', c.email, ' ', c.firstName, ' ',c.lastName) LIKE %?1%")
	public Page<Customer> findAll(String keyword, Pageable pageable);
	
	@Query("UPDATE Customer c SET c.enabled =?2 WHERE c.id = ?1")
	@Modifying
	public void updateEnableStatus(Integer id, boolean enabled);

}
