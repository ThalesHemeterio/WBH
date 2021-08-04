package com.wbh.customer;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.wbh.common.entity.User;

public interface CustomerRepository extends CrudRepository<User, Integer> {
	
	@Query("SELECT u from User u WHERE u.email =?1")
	public User findByEmail(String email);
	
	@Query("SELECT u FROM User u WHERE u.email = :email")
	public User getUserByEmail(@Param("email") String email);
	
	@Query("SELECT u from User u WHERE u.verificationCode =?1")
	public User findByVerificationCode(String code);
	
	@Query("UPDATE User u SET u.enabled = true, u.verificationCode = null WHERE u.id =?1")
	@Modifying
	public void enable(Integer id);

}
