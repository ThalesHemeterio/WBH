package com.wbh.admin.session;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.wbh.admin.session.SessionRepository;
import com.wbh.common.entity.Category;
import com.wbh.common.entity.Customer;
import com.wbh.common.entity.Professional;
import com.wbh.common.entity.Session;

@DataJpaTest
@AutoConfigureTestDatabase(replace= Replace.NONE)
@Rollback(false)
public class SessionRepositoryTests {

	@Autowired
	private SessionRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateSession() {
		Category category = entityManager.find(Category.class,3);
		Professional professional = entityManager.find(Professional.class,1);
		Customer customer = entityManager.find(Customer.class,4);
		
		Session session = new Session();
		
		session.setCategories(category);
		session.setCustomer(customer);
		session.setProfessional(professional);
		session.setName("LifeStyle Coaching");
		session.setDescription("Helping to improve your control of your life");
		session.setDuration("60");
		session.setDate("25/08/2021");
		session.setStartTime("19:00:00");
		session.setPrice(150);
		session.setCreatedTime(new Date());
		session.setEnabled(true);
		
		Session savedSession = repo.save(session);
		
		assertThat(savedSession).isNotNull();
		assertThat(savedSession.getId()).isGreaterThan(0);
		
	}
	
	@Test
	public void testListAllSessions() {
		Iterable<Session> iterableSesssions = repo.findAll();
		
		iterableSesssions.forEach(System.out::println);
	}
	
	@Test
	public void testGetSession() {
		Integer id=2;
		Session session = repo.findById(id).get();
		System.out.println(session);
		
		assertThat(session).isNotNull();
	}
	
	@Test
	public void testUpdateSession() {
		Integer id =1;
		Session session = repo.findById(id).get();
		session.setPrice(99);
		
		repo.save(session);
		Session updatedSession =  entityManager.find(Session.class, id);
		
		assertThat(updatedSession.getPrice()).isEqualTo(99);
		
	}
	
	@Test
	public void testDeleteSession() {
		Integer id =3;
		repo.deleteById(id);
		
		Optional<Session> result = repo.findById(id);
		
		assertThat(!result.isPresent());
	}
}
