package com.wbh.session;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;


import com.wbh.common.entity.Session;

public interface SessionRepository extends PagingAndSortingRepository<Session, Integer> {

	@Query("SELECT s FROM Session s WHERE s.name LIKE %?1% "
			+ "OR s.categories LIKE %?1% "
			+ "OR s.description LIKE %?1%")
	public Page<Session> findAll(String keyword, Pageable pageable);
	
	public Long countById(Integer id);  // method use to delete users
	
	@Query("UPDATE Session s SET s.enabled =?2 WHERE s.id = ?1")
	@Modifying
	public void updateEnableStatus(Integer id, boolean enabled);

	public Session findByName(String name);
}
