package com.wbh.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.wbh.common.entity.Role;
import com.wbh.common.entity.User;

@DataJpaTest(showSql =false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {

	@Autowired
	private UserRepository repo;
	
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
	
	*/
	
	@Test
	public void testSearch
}
