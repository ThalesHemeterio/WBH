package com.wbh.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.wbh.common.entity.Category;

@DataJpaTest(showSql =false)
@AutoConfigureTestDatabase(replace = Replace.NONE)          // uses the persistent database
@Rollback(false)							// save the results on the database
public class CategoryRepositoryTests {
/*
	@Autowired
	private CategoryRepository repo;
	
	@Test
	public void testCreateCategory() {
		Category category1 = new Category("Physical well-being","Physical rellated activities");
		Category category2 = new Category("Psychological well-being","Psychological rellated activities");
		Category category3 = new Category("Engaging activities and work","Work rellated activities");
		
		Category savedCat = repo.save(category1);
		repo.save(category2);
		repo.save(category3);
		
		assertThat(savedCat.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateSubCategory() {
		Category parent1 = new Category(1);
		Category parent2 = new Category(2);
		Category parent3 = new Category(3);
		
		Category subcategory1 = new Category("Yoga","Hindu spiritual and ascetic discipline for health and relaxation",parent1);
		Category subcategory2 = new Category("Pilates","System of exercises to enhance mental awareness",parent1);
		Category subcategory3 = new Category("Therapy","Process to resolve problematic behaviors, feelings, somatic responses and others",parent2);
		Category subcategory4 = new Category("Meditation"," Set of techniques to encourage a heightened state of awareness and focused attention",parent2);
		Category subcategory5 = new Category("Coaching","Supports achieving a specific personal or professional goal by training and guidance",parent3);
		
		Category savedCat = repo.save(subcategory1);
		repo.save(subcategory2);
		repo.save(subcategory3);
		repo.save(subcategory4);
		repo.save(subcategory5);
		
		assertThat(savedCat.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testGetCategory() {
		Category category = repo.findById(1).get();
		System.out.println(category.getName());
		
		Set<Category> children = category.getChildren();
		
		for(Category subCategory :children) {
			System.out.println(subCategory.getName());
		}
		assertThat(children.size()).isGreaterThan(0);
	}
	
	@Test
	public void testPrintHierarquicalCategories() {
		Iterable<Category> categories= repo.findAll();
		
		for(Category category : categories) {
			if(category.getParent() ==null) {
				System.out.println(category.getName());
				Set<Category> children = category.getChildren();
				
				for(Category subCategory :children) {
					System.out.println("  --" + subCategory.getName());
				}
			}
		}
	}
	*/
}
