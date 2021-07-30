package com.wbh.professional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.wbh.common.entity.Professional;

@Repository
public interface ProfessionalRepository extends PagingAndSortingRepository<Professional, Integer> {
	
	@Query("SELECT p FROM Professional p WHERE p.email = ?1")
	public Professional findByEmail(String email);
	
	@Query("UPDATE Professional p SET p.enabled = true WHERE p.id =?1")
	@Modifying
	public Professional enable(Integer id);
	

}
