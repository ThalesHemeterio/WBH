package com.wbh.admin.setting;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.wbh.admin.FileUploadUtil;
import com.wbh.common.entity.Currency;
import com.wbh.common.entity.Setting;

@Controller
public class SettingController {

		@Autowired private SettingService service;
		
		@Autowired private CurrencyRepository currencyRepo;
		
		@GetMapping("/admin/settings")
		public String listAll(Model model) {
			List<Setting> listSettings = service.listAllSettings();
			List<Currency> listCurrencies = currencyRepo.findAllByOrderByNameAsc();
			
			
			model.addAttribute("listCurrencies", listCurrencies);
			
			for(Setting setting : listSettings) {
				model.addAttribute(setting.getKey(), setting.getValue());
			}
					
			return"admin/settings/settings";
		}
		
		@PostMapping("/admin/settings/save_general")
		public String saveGeneralSettings(@RequestParam("fileImage") MultipartFile multipartFile,
				HttpServletRequest request, RedirectAttributes ra ) throws IOException {
			GeneralSettingBag settingBag = service.getGenaralSettings();
			
			saveSiteLogo(multipartFile, settingBag);
			saveCurrencySymbol(request,settingBag);
			
			updateSettingValuesFromForm(request, settingBag.list());
			
				ra.addFlashAttribute("message","General Settings have been saved.");

			return"redirect:/admin/settings";
		}

		private void saveSiteLogo(MultipartFile multipartFile, GeneralSettingBag settingBag) throws IOException {
			if(!multipartFile.isEmpty()) {
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				String value = "/site-logo/" + fileName;
				settingBag.updateSiteLogo(value);
				String uploadDir = "../site-logo/";
				FileUploadUtil.cleanDirectory(uploadDir);
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			}
		}
		
		private void saveCurrencySymbol(HttpServletRequest request, GeneralSettingBag settingBag) {
			Integer currencyId=Integer.parseInt(request.getParameter("CURRENCY_ID"));
			Optional<Currency> findByIdResult = currencyRepo.findById(currencyId);
			
			if(findByIdResult.isPresent()) {
				Currency currency = findByIdResult.get();
				settingBag.updateCurrencySymbol(currency.getSymbol());
			}
		}
		
		private void updateSettingValuesFromForm(HttpServletRequest request, List<Setting> listSettings) {
			for(Setting setting:listSettings) {
				String value = request.getParameter(setting.getKey());
				if(value != null) {
					setting.setValue(value);
				}
			}
			service.saveAll(listSettings);
		}
		
		@PostMapping("/admin/settings/save_mail_server")
			public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes ra ){
				List<Setting> mailServerSettings = service.getMailServerSettings();
				
				updateSettingValuesFromForm(request, mailServerSettings);
				
					ra.addFlashAttribute("message","Mail Server Settings have been saved.");

				return"redirect:/admin/settings";
		}
		
		@PostMapping("/admin/settings/save_mail_templates")
		public String saveMailTemplateSettings(HttpServletRequest request, RedirectAttributes ra ){
			List<Setting> mailTemplateSettings = service.getMailTemplateSettings();
			
			updateSettingValuesFromForm(request, mailTemplateSettings);
			
				ra.addFlashAttribute("message","Mail Template Settings have been saved.");

			return"redirect:/admin/settings";
	}
}
