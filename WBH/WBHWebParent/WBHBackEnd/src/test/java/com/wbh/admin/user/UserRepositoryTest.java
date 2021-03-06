package com.wbh.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import com.wbh.admin.session.SessionRepository;
import com.wbh.common.entity.Role;
import com.wbh.common.entity.Session;
import com.wbh.common.entity.User;

@DataJpaTest(showSql =false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

	@Autowired
	private UserRepository repo;
	
	@Autowired SessionRepository sessionRepo;
	
	@Autowired
	private TestEntityManager entityManager;
	/*	
	@Test
	public void testCreateUser() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User userAmdin = new User("thaleshemeterio@gmail.com", "123456", "Thales", "Hemeterio",  "23/03/1992", "083 067 8284", "34,Royston Kimmage");
		userAmdin.addRole(roleAdmin);
		
		User savedUser = repo.save(userAmdin);
		
		assertThat(savedUser.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testCreateWithTwoRoles() {
		User userGeneric = new User("fulano@gmail.com", "098765", "Fulano", "de Tal",  "09/04/1456", "123 456 7890", "1, Street, Area");
		
		Role roleAdmin = new Role(1);
		Role roleClient = new Role(2);
		userGeneric.addRole(roleAdmin);
		userGeneric.addRole(roleClient);
		
		User savedUser = repo.save(userGeneric);
		
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAllUsers() {
		Iterable<User> listUsers = repo.findAll();
		
		listUsers.forEach(user -> System.out.println(user));
	}
	
	@Test
	public void getUserById() {
		User userThales = repo.findById(1).get();
		System.out.println(userThales);
		assertThat(userThales).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetails() {
		User userThales = repo.findById(1).get();
		userThales.setEnabled(true);
		userThales.setEmail("hemeterio@hotmail.com");
		repo.save(userThales);
	}
	
	@Test
	public void testUpdateUserRoles() {
		User userGeneric = repo.findById(2).get();
		Role roleAdmin = new Role(1);
		userGeneric.getRoles().remove(roleAdmin);   // removing a role
	}
	
	@Test
	public void deleteUser() {
		Integer userId = 2;
		repo.deleteById(userId);
	}
	
	
	@Test
	public void testGetUserByEmail() {
		String email = "bruna@live.com";
		User user=repo.getUserByEmail(email);
		
		assertThat(user).isNotNull();
	}
	
	
	@Test
	public void testCountById() {
		Integer id=1;
		Long countById =repo.countById(id);
		
		assertThat(countById).isNotNull().isGreaterThan(0);
	}
	
	
	@Test
	public void testEnableUser() {
		Integer id=1;
		repo.updateEnableStatus(id, true);
		
	}
	
	@Test
	public void testDisableUser() {
		Integer id=1;
		repo.updateEnableStatus(id, false);
		
	}
	
	@Test 
	public void testListFirstPage() {
		int pageNumber = 1;
		int pageSize = 4;
		Pageable pegeable = PageRequest.of(pageNumber,pageSize);
		Page<User> page = repo.findAll(pegeable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isEqualTo(pageSize);
	}
	
	
	
	@Test
	public void testSearchUsers() {
		String keyword = "Thales";
		int pageNumber = 0;
		int pageSize = 4;
		Pageable pegeable = PageRequest.of(pageNumber,pageSize);
		Page<User> page = repo.findAll(keyword,pegeable);
		
		List<User> listUsers = page.getContent();
		
		listUsers.forEach(user -> System.out.println(user));
		
		assertThat(listUsers.size()).isGreaterThan(0);
	}

	@Test
	public void getUserById() {
		User userThales = repo.findById(1).get();
		System.out.println(userThales);
		userThales.setEnabled(true);
		assertThat(userThales).isNotNull();
	}
	
	@Test
	public void testCreateUser() {
		Role roleAdmin = entityManager.find(Role.class, 1);
		User userAmdin = new User("thales@admin.com", "12345678", "Thales", "Hemeterio",  "23/03/1992", "083 067 8284", "34,Royston Kimmage","D12R5P9", "Dublin","Ireland");
		userAmdin.addRole(roleAdmin);
		userAmdin.setEnabled(true);
		System.out.println(userAmdin);
		System.out.println(roleAdmin);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String rawPassword = "12345678";
		String encodedPassword = passwordEncoder.encode(rawPassword);
		userAmdin.setPassword(encodedPassword);
		
		repo.save(userAmdin);

	}
	
	@Test
	public void testCreateUser() {
		Role roleAdmin = entityManager.find(Role.class, 3);
		User userAmdin = new User("thales@client.com", "12345678", "Thales", "Hemeterio",  "23/03/1992", "083 067 8284", "34,Royston Kimmage","D12R5P9", "Dublin","Ireland");
		userAmdin.addRole(roleAdmin);
		userAmdin.setEnabled(true);
		System.out.println(userAmdin);
		System.out.println(roleAdmin);
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String rawPassword = "12345678";
		String encodedPassword = passwordEncoder.encode(rawPassword);
		userAmdin.setPassword(encodedPassword);
		
		repo.save(userAmdin);

	}
	
	@Test
	public void testEditPassword() {
		User userThales = repo.findById(8).get();
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String rawPassword = "12345678";
		String encodedPassword = passwordEncoder.encode(rawPassword);
		userThales.setPassword(encodedPassword);
		
		repo.save(userThales);

	}
	
	@Test
	public void testDate() {
	Date date = new Date();
	
	Session session = new Session();
	session = sessionRepo.findByName("Acupuncture");
	System.out.println(session.getDate());
	System.out.println(date.UTC(0, 0, 0, 0, 0, 0)+"-"+date.getMonth()+"-"+date.getDay());
	}
	*/
	
}
