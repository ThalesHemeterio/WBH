package com.wbh.admin.session;

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
import com.wbh.common.entity.Session;
import com.wbh.common.entity.User;

@Service
@Transactional
public class SessionService {
		public static final int USERS_PER_PAGE =5;
		
		@Autowired private SessionRepository repo;
		
		public List<Session> listAll(){
			return (List<Session>) repo.findAll();
		}

		public Page<Session> listByPage(int pageNum, String sortField, String sortDir, String keyword){
			Sort sort = Sort.by(sortField);	
			sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
			
			Pageable pageable = PageRequest.of(pageNum -1, USERS_PER_PAGE, sort);
			
			if(keyword != null) {
				return repo.findAll(keyword, pageable);
			}
			return repo.findAll(pageable);
		}
		
		public Page<Session> listByPageByProfessional(int pageNum,String sortField,String sortDir, User user,String keyword){
			Sort sort = Sort.by(sortField);	
			sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
			
			Pageable pageable = PageRequest.of(pageNum -1, USERS_PER_PAGE, sort);
			
			if(keyword != null) {
				return repo.findSessionByProfessional(user, pageable);
			}
			return repo.findSessionByProfessional(user,pageable);
		}
		
		//retunr to seacrh page session not booked and with/without filter
		public Page<Session> listByPageAndCustomer(int pageNum,String sortField,String sortDir,String keyword){
			Sort sort = Sort.by(sortField);	
			sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
			User user =null;
			Pageable pageable = PageRequest.of(pageNum -1, USERS_PER_PAGE, sort);
			
			if(keyword != null) {
				return repo.findSessionByCustomerAndDescriptionOrCustomerAndNameOrCustomerAndProfessionalFirstNameOrCustomerAndCategoriesName(user,keyword,user,keyword,user,keyword,user,keyword, pageable);
			}
			return repo.findSessionByCustomer(user,pageable);
		}
		
		public Session save(Session session) {
			session.setCreatedTime(new Date());
			return repo.save(session);
		}
		
		public void delete(Integer id) throws SessionNotFoundException {
			Long countById = repo.countById(id);
			if(countById == null || countById == 0) {
				throw new SessionNotFoundException("Could not find any session with ID: "+ id);
			}
			
			repo.deleteById(id);
		}
		
		public void updateSessionEnabledStatus(Integer id, boolean enabled) {   // function to update enabled/disabled status of the user
			repo.updateEnableStatus(id, enabled);
		}
		
		public Session get(Integer id) throws SessionNotFoundException {
			try {
				return repo.findById(id).get();
			} catch (NoSuchElementException ex) {
				throw new SessionNotFoundException("Could not find any session with ID: "+ id);
			}
		}

		public Page<Session> listByPageAndCustomerBooked(User user, int pageNum, String sortField, String sortDir,
				String keyword) {
			Sort sort = Sort.by(sortField);	
			sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
			Pageable pageable = PageRequest.of(pageNum -1, USERS_PER_PAGE, sort);
			
			return repo.findSessionByCustomer(user,pageable);
		}


}
