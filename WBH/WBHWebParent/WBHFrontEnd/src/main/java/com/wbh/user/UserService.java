package com.wbh.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.wbh.common.entity.Role;
import com.wbh.common.entity.User;

@Service
@Transactional
public class UserService {

	public static final int USERS_PER_PAGE =4;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired 
	private PasswordEncoder passwordEncoder;
	
	public User getByEmail(String email) {
		return userRepo.getUserByEmail(email);
	}
	
	public List<User> listAll(){
		return (List<User>) userRepo.findAll();
	}
	
	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword){
		Sort sort = Sort.by(sortField);	
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum -1, USERS_PER_PAGE, sort);
		
		if(keyword != null) {
			return userRepo.findAll(keyword, pageable);
		}
		return userRepo.findAll(pageable);
	}
	
	public List<Role> listRoles(){
		return (List<Role>) roleRepo.findAll();
		
	}

	public User save(User user) {
		boolean isUpdatingUser = (user.getId()!=null);								
		
		if(isUpdatingUser) {														// checking if you're are editing or creating a user if the same email.
			User existingUser = userRepo.findById(user.getId()).get();
			
			if(user.getPassword().isEmpty()) {                                      // password is not required in changing you can keep the same just not fill the field
				user.setPassword(existingUser.getPassword());                
			}else {
				encodePassword(user);
			}
		}else {
			encodePassword(user);
		}
		return userRepo.save(user);
	}
	
	public void encodePassword(User user) {											// Function responsible for encoding the password 		
		String encodedPassowrd = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassowrd);
		
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		User userByEmail = userRepo.getUserByEmail(email);
		
		if(userByEmail == null) return true;
		
		boolean isCreatingNew = (id==null);
		
		if(isCreatingNew) {                                   // checking if you're are editing or creating a user if the same email.
			if(userByEmail !=null) return false;
			} else {
				if(userByEmail.getId() != id) {
				return false;
			}
		}
		return true;
	}

	public User get(Integer id) throws UserNotFoundException {
		try {
			return userRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user with ID: "+ id);
		}
	}
	
	public void delete(Integer id) throws UserNotFoundException {
		Long countById = userRepo.countById(id);
		if(countById == null || countById == 0) {
			throw new UserNotFoundException("Could not find any user with ID: "+ id);
		}
		
		userRepo.deleteById(id);
	}
	
	public User updateAccount(User userInForm) {     // updates an account of a logged user
		User userInDB = userRepo.findById(userInForm.getId()).get();
		
		if(!userInForm.getPassword().isEmpty()) {
			userInDB.setPassword(userInForm.getPassword());
			encodePassword(userInDB);
		}
		
		if(userInForm.getPhotos() !=null) {
			userInDB.setPhotos(userInForm.getPhotos());
		}

		userInDB.setFirstName(userInForm.getFirstName());
		userInDB.setLastName(userInForm.getLastName());
		return userRepo.save(userInDB);
	}
	
	public void updateUserEnabledStatus(Integer id, boolean enabled) {   // function to update enabled/disabled status of the user
		userRepo.updateEnableStatus(id, enabled);
	}
}
