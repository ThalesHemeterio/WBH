package com.wbh.admin.professional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wbh.common.entity.Customer;
import com.wbh.common.entity.Professional;
import com.wbh.common.entity.User;

public interface ProfessionalRepository extends PagingAndSortingRepository<Professional, Integer> {
	
	@Query("SELECT p from Professional p WHERE p.email =?1")
	public Professional findByEmail(String email);
	
	@Query("SELECT p from Professional p WHERE p.verificationCode =?1")
	public Professional findByVerificationCode(String code);
	
	@Query("UPDATE Professional p SET p.enabled = true WHERE p.id =?1")
	@Modifying
	public Professional enable(Integer id);
	
	@Query("SELECT p FROM Professional p WHERE p.email = :email")
	public Professional getProfessionalByEmail(@Param("email") String email);
	
	public Long countById(Integer id);  // method use to delete users
	
	@Query("SELECT p FROM Professional p WHERE CONCAT(p.id, ' ', p.email, ' ', p.firstName, ' ',p.lastName) LIKE %?1%")
	public Page<Professional> findAll(String keyword, Pageable pageable);
	
	@Query("UPDATE Professional p SET p.enabled =?2 WHERE p.id = ?1")
	@Modifying
	public void updateEnableStatus(Integer id, boolean enabled);

}
