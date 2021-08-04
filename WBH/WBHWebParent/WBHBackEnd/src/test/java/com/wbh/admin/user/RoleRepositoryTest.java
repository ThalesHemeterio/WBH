package com.wbh.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.wbh.common.entity.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RoleRepositoryTest {
	
	@Autowired
	private RoleRepository repo;
	/*	
	@Test
	public void testCreateFirstRole() {
		Role roleAdmin = new Role("Admin", "Manage everything");
		Role savedRole = repo.save(roleAdmin);
		
		assertThat(savedRole.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateRestofRoles() {
		Role roleClient = new Role("Client", "Manage their accounts, publish reviews of professionals,"
				+"search and filter options, book and pay sessions and manage bookings");
		Role roleProfessional = new Role("Professional", "Manage their accounts, publish sessions,"
				+"menage sessions, search and filter options");
		Role savedRole2 = repo.save(roleClient);
		Role savedRole3 = repo.save(roleProfessional);
		assertThat(savedRole2.getId()).isGreaterThan(0);
		assertThat(savedRole3.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateRestofRoles() {
		Role roleAss = new Role("Assistant", "Manage their accounts, enable user professionals, approve reviews");
		repo.save(roleAss);
		}
	*/
	
	@Test
	public void testgetRoles() {
		Role rolProf = repo.getRoleById(3);
		System.out.println(rolProf);
		}
}
