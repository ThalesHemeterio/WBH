package com.wbh.professional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.wbh.common.entity.User;


public interface ProfessionalRepository extends CrudRepository<User, Integer> {
	
	@Query("SELECT u from User u WHERE u.email =?1")
	public User findByEmail(String email);
	
	@Query("SELECT u from User u WHERE u.verificationCode =?1")
	public User findByVerificationCode(String code);
	
	@Query("UPDATE User u SET u.enabled = true WHERE u.id =?1")
	@Modifying
	public User enable(Integer id);
	
	@Query("UPDATE User u SET u.enabled = false, u.verificationCode = null WHERE u.id =?1")
	@Modifying
	public void verify(Integer id);

}
