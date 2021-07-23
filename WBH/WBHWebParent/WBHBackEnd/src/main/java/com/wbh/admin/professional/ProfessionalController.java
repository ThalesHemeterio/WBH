package com.wbh.admin.professional;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wbh.admin.FileUploadUtil;
import com.wbh.common.entity.Professional;


@Controller
public class ProfessionalController {

	@Autowired
	private ProfessionalService service;
	
	@GetMapping("/professionals")
	public String listfirstPage(Model model) {
		return listByPage(1,model, "firstName", "asc",null);
	}
	
	@GetMapping("/professionals/page/{pageNum}")
	public String listByPage(
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		Page<Professional> page = service.listByPage(pageNum, sortField, sortDir,keyword);
		List<Professional> listProfessionals = page.getContent();
		
		long startCount =(pageNum-1)*ProfessionalService.USERS_PER_PAGE+1;
		long endCount = startCount+ProfessionalService.USERS_PER_PAGE-1;
		if(endCount >page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		
		//passing the attributes to the view
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems",page.getTotalElements());
		model.addAttribute("listProfessionals",listProfessionals);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("reverseSortDir",reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "professionals/professionals";
	}
	
	@GetMapping("/professionals/new")
	public String newCustomer(Model model) {
		Professional professional = new Professional();
		professional.setEnabled(false);
		model.addAttribute("professional",professional);
		model.addAttribute("pageTitle", "Create new Professional");
		return "professionals/professional_form";
	}
	
	@PostMapping("/professionals/save")
	public String saveProfessional(Professional professional, RedirectAttributes redirectAttributes, 
			@RequestParam("image") MultipartFile multipartFile ) throws IOException {
		System.out.println(professional);
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			professional.setPhotos(fileName);
			Professional savedProfessional = service.save(professional);
			String uploadDir = "professional-photos/"+savedProfessional.getId();
			FileUploadUtil.cleanDirectory(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		}else {
			if(professional.getPhotos().isEmpty()) professional.setPhotos(null);
			service.save(professional);
		}
		redirectAttributes.addFlashAttribute("message", "The Professional has been saved successfully.");	
		return getRedirectURLtoAffectedProfessional(professional);
	}

	private String getRedirectURLtoAffectedProfessional(Professional professional) {
		String firstPartOfEmail = professional.getEmail().split("@")[0];
		return "redirect:/professionals/page/1?sortField=id&sortDir=asc&keyword="+ firstPartOfEmail;
	}
	
	@GetMapping("/professional/edit/{id}")
	public String editProfessional(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			Professional professional = service.get(id);
			model.addAttribute("professional", professional);
			model.addAttribute("pageTitle", "Edit Professional (ID: "+ id+")");
			return "professionals/professional_form";
		} catch(ProfessionalNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/professionals";
	}
	
	@GetMapping("/professional/delete/{id}")
	public String deleteProfessional(@PathVariable(name="id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The professional "+ id + " has been deleted successfully");
		} catch(ProfessionalNotFoundException ex){
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/professionals";
	}
	
	@GetMapping("/professionals/{id}/enabled/{status}")
	public String updateProfessionalEnabledStatus(@PathVariable(name="id") Integer id, @PathVariable(name="status") boolean enabled, RedirectAttributes redirectAttributes) {
		service.updateProfessionalEnabledStatus(id, enabled);
		String status = enabled ?"enabled" :"disabled";
		String message ="The Professional Id " + id + " has been " + status;
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/professionals";
	}
}
