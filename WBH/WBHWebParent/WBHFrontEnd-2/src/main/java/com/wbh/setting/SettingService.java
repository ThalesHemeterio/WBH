package com.wbh.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wbh.common.entity.Setting;
import com.wbh.common.entity.SettingCategory;

@Service
public class SettingService {
	@Autowired private SettingRepository repo;
	


	
	public EmailSettingBag getEmailSettings() {
		List<Setting> settings = repo.findByCategory(SettingCategory.MAIL_SERVER);
		settings.addAll(repo.findByCategory(SettingCategory.MAIL_TEMPLATES));
		
		return new EmailSettingBag(settings);
	}
}
