package com.wbh.admin.setting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.wbh.common.entity.Currency;
import com.wbh.common.entity.Setting;
import com.wbh.common.entity.SettingCategory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CurrencyRepositoryTests {

		@Autowired CurrencyRepository repo;
		
		@Test
		public void testCreateCurrency() {			
			Currency currency = new Currency("Euro","€", "EUR");
			
			Currency savedCurrency = repo.save(currency);
			assertThat(savedCurrency).isNotNull();
		}
}
