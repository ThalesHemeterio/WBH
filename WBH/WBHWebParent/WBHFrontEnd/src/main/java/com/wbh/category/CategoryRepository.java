package com.wbh.category;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.wbh.common.entity.Category;

@Repository
public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {
	
	public Long countById(Integer id);  // method use to delete categories
	
	@Query("UPDATE Category c SET c.enabled =?2 WHERE c.id = ?1")
	@Modifying
	public void updateEnableStatus(Integer id, boolean enabled);
}
