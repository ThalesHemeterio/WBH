package com.wbh.admin.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wbh.common.entity.Setting;
import com.wbh.common.entity.SettingCategory;
import com.wbh.admin.setting.EmailSettingBag;

@Service
public class SettingService {
	@Autowired private SettingRepository repo;
	
	public List<Setting> listAllSettings(){
		return(List<Setting>) repo.findAll();
	}
	
	public GeneralSettingBag getGenaralSettings() {
		List<Setting> settings = new ArrayList<>();
		
		List<Setting> generalSettings = repo.findByCategory(SettingCategory.GENERAL);
		List<Setting> currecySettings = repo.findByCategory(SettingCategory.CURRENCY);
		settings.addAll(generalSettings);
		settings.addAll(currecySettings);
		
		return new GeneralSettingBag(settings);
		
	}
	
	public void saveAll(Iterable<Setting> settings) {
		repo.saveAll(settings);
	}
	
	public List<Setting> getMailServerSettings(){
		return repo.findByCategory(SettingCategory.MAIL_SERVER);
	}
	
	public List<Setting> getMailTemplateSettings(){
		return repo.findByCategory(SettingCategory.MAIL_TEMPLATES);
	}
	
	public EmailSettingBag getEmailSettings() {
		List<Setting> settings = repo.findByCategory(SettingCategory.MAIL_SERVER);
		settings.addAll(repo.findByCategory(SettingCategory.MAIL_TEMPLATES));
		
		return new EmailSettingBag(settings);
	}
}
