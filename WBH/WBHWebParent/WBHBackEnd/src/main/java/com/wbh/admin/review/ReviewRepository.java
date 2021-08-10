package com.wbh.admin.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.wbh.common.entity.Review;
import com.wbh.common.entity.Session;
import com.wbh.common.entity.User;

public interface ReviewRepository extends PagingAndSortingRepository<Review, Integer> {

	@Query("SELECT r FROM Review r WHERE r.title LIKE %?1%"
			+ "OR r.text LIKE %?1%")
	public Page<Review> findAll(String keyword, Pageable pageable);
	
	public Page<Review> findReviewByCustomer(User keyword, Pageable pageable);
	
	public Long countById(Integer id);  // method use to delete users
	
	@Query("UPDATE Review r SET r.enabled =?2 WHERE r.id = ?1")
	@Modifying
	public void updateEnableStatus(Integer id, boolean enabled);

	public Page<Review> findReviewByProfessionalAndEnabled(User keyword, boolean enabled, Pageable pageable);
}
