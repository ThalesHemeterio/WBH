package com.wbh.category;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.wbh.common.entity.Category;
import com.wbh.common.entity.Role;
import com.wbh.common.entity.User;

@Controller
public class CategoryController {

	@Autowired
	private CategoryService service;
	
	@GetMapping("/categories")
	public String listAll(Model model) {
		List<Category> listCategories = service.listAll();
		model.addAttribute("listCategories", listCategories);
		
		return "categories/categories";
	}
	
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		
		List<Category> listCategories = service.listCategoriesUsedInForm();
		model.addAttribute("category", new Category());
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Create New Category");
		
		return "categories/category_form";
	}
	
	@PostMapping("/categories/save")
	public String saveCategory(Category category, RedirectAttributes ra) {
		
		Category savedCategory = service.save(category);
		ra.addFlashAttribute("message","The category has been saved successfully");
		
		return "redirect:/categories";
	}
	
	@GetMapping("/category/edit/{id}")
	public String editCategory(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Category category = service.get(id);
			List<Category> listCategories = service.listCategoriesUsedInForm();
			model.addAttribute("category", category);
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("pageTitle", "Edit Category (ID: "+ id+")");

			return "categories/category_form";
		} catch(CategoryNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return "redirect:/categories";
		}
	}
	
	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The Category ID "+ id + " has been deleted successfully");
		} catch(CategoryNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/categories";
	}
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateCategoryEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateCategoryEnabledStatus(id, enabled);
		String status = enabled ?"enabled" :"disabled";
		String message ="The category Id " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/categories";
	}
}
