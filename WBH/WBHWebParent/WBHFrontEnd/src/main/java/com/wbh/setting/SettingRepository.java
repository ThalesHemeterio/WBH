package com.wbh.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.wbh.common.entity.Setting;
import com.wbh.common.entity.SettingCategory;

public interface SettingRepository extends CrudRepository<Setting, String> {

	public List<Setting> findByCategory(SettingCategory category);

	
	
}
