package com.wbh.professional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.wbh.common.entity.Customer;
import com.wbh.common.entity.Professional;

public interface ProfessionalRepository extends CrudRepository<Professional, Integer> {
	
	@Query("SELECT p from Professional p WHERE p.email =?1")
	public Professional findByEmail(String email);
	
	@Query("SELECT p from Professional p WHERE p.verificationCode =?1")
	public Professional findByVerificationCode(String code);
	
	@Query("UPDATE Professional p SET p.enabled = true WHERE p.id =?1")
	@Modifying
	public Professional enable(Integer id);
	
	@Query("UPDATE Professional p SET p.enabled = false, p.verificationCode = null WHERE p.id =?1")
	@Modifying
	public void verify(Integer id);

}
