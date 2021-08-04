package com.wbh.admin.review;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.wbh.admin.professional.ProfessionalNotFoundException;
import com.wbh.common.entity.Review;
import com.wbh.common.entity.Session;

@Service
@Transactional
public class ReviewService {
		public static final int USERS_PER_PAGE =4;
		
		@Autowired private ReviewRepository repo;
		
		public List<Review> listAll(){
			return (List<Review>) repo.findAll();
		}

		public Page<Review> listByPage(int pageNum, String sortField, String sortDir, String keyword){
			Sort sort = Sort.by(sortField);	
			sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
			
			Pageable pageable = PageRequest.of(pageNum -1, USERS_PER_PAGE, sort);
			
			if(keyword != null) {
				return repo.findAll(keyword, pageable);
			}
			return repo.findAll(pageable);
		}
		
		public Review save(Review review) {
			review.setCreatedTime(new Date());
			return repo.save(review);
		}
		
		public void delete(Integer id) throws ReviewNotFoundException {
			Long countById = repo.countById(id);
			if(countById == null || countById == 0) {
				throw new ReviewNotFoundException("Could not find any review with ID: "+ id);
			}
			
			repo.deleteById(id);
		}
		
		public void updateReviewEnabledStatus(Integer id, boolean enabled) {   // function to update enabled/disabled status of the user
			repo.updateEnableStatus(id, enabled);
		}
		
		public Review get(Integer id) throws ReviewNotFoundException {
			try {
				return repo.findById(id).get();
			} catch (NoSuchElementException ex) {
				throw new ReviewNotFoundException("Could not find any review with ID: "+ id);
			}
		}
}
