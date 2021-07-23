package com.wbh.admin.setting;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.wbh.common.entity.Currency;

public interface CurrencyRepository extends CrudRepository<Currency, Integer> {
	
	public List<Currency> findAllByOrderByNameAsc();
}
