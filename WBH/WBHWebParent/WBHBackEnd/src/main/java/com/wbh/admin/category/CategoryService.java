package com.wbh.admin.category;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wbh.admin.user.UserNotFoundException;
import com.wbh.common.entity.Category;


@Service
@Transactional
public class CategoryService {

	@Autowired
	private CategoryRepository repo;
	
	public List<Category> listAll(){
		return (List<Category>)repo.findAll();
	}
	
	public List<Category> listCategoriesUsedInForm(){
		List<Category> categoriesUsedInForm = new ArrayList<>();
		Iterable<Category> categoriesInDB = repo.findAll();
		
		for(Category category : categoriesInDB) {
			if(category.getParent() ==null) {
				categoriesUsedInForm.add(Category.copyIdAndName(category));
				
				Set<Category> children = category.getChildren();
				
				for(Category subCategory :children) {
					String name= "  --" + subCategory.getName();
					categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
					listChildren(categoriesUsedInForm, subCategory,1);
				}
			}
		}
		
		return categoriesUsedInForm;
	}
	
	private void listChildren(List<Category> categoriesUsedInForm, Category parent, int subLevel) {
		int newSubLevel = subLevel+1;
		Set<Category> children = parent.getChildren();
		
		for(Category subCategory : children) {
			String name="";
			for( int i =0; i<newSubLevel;i++) {
				name+="  --";
			}
			name += subCategory.getName();
			categoriesUsedInForm.add(Category.copyIdAndName(subCategory.getId(), name));
			
			listChildren(categoriesUsedInForm, subCategory,newSubLevel);
		}
	}
	
	public Category save(Category category) {
		return repo.save(category);
	}

	public Category get(Integer id) throws CategoryNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CategoryNotFoundException("Could not find any category with ID: "+ id);
		}
	}

		public void delete(Integer id) throws CategoryNotFoundException {
		Long countById = repo.countById(id);
		if(countById == null || countById == 0) {
			throw new CategoryNotFoundException("Could not find any category with ID: "+ id);
		}
		repo.deleteById(id);
	}
		public void updateCategoryEnabledStatus(Integer id, boolean enabled) {   // function to update enabled/disabled status of category
			repo.updateEnableStatus(id, enabled);
		}
}
