package com.wbh.session;

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

import com.wbh.FileUploadUtil;
import com.wbh.category.CategoryService;
import com.wbh.common.entity.Category;
import com.wbh.common.entity.Session;
import com.wbh.customer.CustomerService;
import com.wbh.professional.ProfessionalNotFoundException;
import com.wbh.professional.ProfessionalService;

@Controller
public class SessionController {
	
	@Autowired SessionService service;
	@Autowired ProfessionalService serviceP;
	@Autowired CustomerService serviceC;
	@Autowired CategoryService serviceCat;
	
	@GetMapping("/search")
	public String listfirstPage(Model model) {
		return listByPage(1,model, "id", "desc",null);
	}
	
	@GetMapping("/search/page/{pageNum}")
	public String listByPage(
				@PathVariable(name="pageNum") int pageNum, Model model, 
				@Param("sortField") String sortField, @Param("sortDir") String sortDir,
				@Param("keyword") String keyword) {
		Page<Session> page = service.listByPage(pageNum, sortField, sortDir,keyword);
		List<Session> listSessions = page.getContent();
		
		long startCount =(pageNum-1)*SessionService.USERS_PER_PAGE+1;
		long endCount = startCount+SessionService.USERS_PER_PAGE-1;
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
		model.addAttribute("listSessions",listSessions);
		model.addAttribute("sortField",sortField);
		model.addAttribute("sortDir",sortDir);
		model.addAttribute("reverseSortDir",reverseSortDir);
		model.addAttribute("keyword", keyword);
		
		return "search";
	}

}


