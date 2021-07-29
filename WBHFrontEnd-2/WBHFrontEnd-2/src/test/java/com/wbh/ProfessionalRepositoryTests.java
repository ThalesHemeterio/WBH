package com.wbh;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import com.wbh.common.entity.Customer;
import com.wbh.common.entity.Professional;
import com.wbh.common.entity.Role;
import com.wbh.professional.ProfessionalRepository;
import com.wbh.user.RoleRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProfessionalRepositoryTests {

	@Autowired private ProfessionalRepository repo;
	@Autowired private RoleRepository repo2;
	/*
	@Test
	public void testCreateProfessional() {
		Professional professional = new Professional();
		professional.setAddressLine1("584 Maranhao");
		professional.setAddressLine2("Jardim Nova America");
		professional.setCreatedTime(new Date());
		professional.setDob("03/05/1998");
		professional.setEmail("giovanna@gmail.com");
		professional.setFirstName("Giovanna");
		professional.setLastName("Morais");
		professional.setPassword("12345678");
		professional.setPostalCode("D35 MG37");
		professional.setCity("Dublin");
		professional.setCountry("Ireland");
		professional.setContact("123 456 7890");
		professional.setTitle("Psychologist");
		professional.setQualification("Degree in Psychology University of Kansas");
		Professional savedProfessional = repo.save(professional);
		
		assertThat(savedProfessional).isNotNull();
		assertThat(savedProfessional.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListProfessionals() {
		Iterable<Professional> professionals = repo.findAll();
		professionals.forEach(System.out::println);
		
		assertThat(professionals).hasSizeGreaterThan(1);
	}
	
	@Test 
	public void testUpdateProfessional() {
		Integer professionalId =1;
		String lastname = "Hemeterio";
		
		Professional professional = repo.findById(professionalId).get();
		professional.setLastName(lastname);
		professional.setEnabled(true);
		
		Professional updatedProfessional = repo.save(professional);
		assertThat(updatedProfessional.getLastName()).isEqualTo(lastname);
		
	}
	
	@Test
	public void testGetProfessional() {
		Integer professionalId =4;
		Optional<Professional> findById = repo.findById(professionalId);
		
		assertThat(findById).isPresent();
		
		Professional professional =findById.get();
		System.out.println(professional);
		
		Role roleProfessional = new Role(3);
		professional.addRole(roleProfessional);
		repo.save(professional);
	}
	
	@Test
	public void testDeleteProfessional() {
		Integer professionalId = 2;
		repo.deleteById(professionalId);
		
		Optional<Professional> findById = repo.findById(professionalId);
		assertThat(findById).isNotPresent();
	}
	
	@Test
	public void testFindByEmail() {
		String email = "giovanna@gamil";
		Professional professional = repo.findByEmail(email);
		
		assertThat(professional).isNotNull();
		System.out.println(professional);
	}
	
	@Test
	public void findByVerificationCode() {
		String code = "code_123";
		Professional professional = repo.findByVerificationCode(code);
		
		assertThat(professional).isNotNull();
		System.out.println(professional);
	}
	
	@Test
	public void testEnableProfessional() {
		Integer professionalId=3;
		repo.enable(professionalId);
		
		Professional professional = repo.findById(professionalId).get();
		assertThat(professional.isEnabled()).isTrue();
	}
	
	*/
}
