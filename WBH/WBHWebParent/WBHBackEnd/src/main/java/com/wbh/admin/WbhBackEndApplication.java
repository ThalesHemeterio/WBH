package com.wbh.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.wbh.common.entity","com.wbh.admin.user"})
public class WbhBackEndApplication {

	public static void main(String[] args) {
		SpringApplication.run(WbhBackEndApplication.class, args);
	}

}
