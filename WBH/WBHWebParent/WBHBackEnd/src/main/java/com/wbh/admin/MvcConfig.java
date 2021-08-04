package com.wbh.admin;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// class in order to display the photos of the users
@Configuration
public class MvcConfig implements WebMvcConfigurer {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		exposeDirectory("user-photos",registry);
		exposeDirectory("../site-logo",registry);
	}
	
	private void exposeDirectory(String pathPatter, ResourceHandlerRegistry registry) {
		
		Path path = Paths.get(pathPatter);
		String absolutPath = path.toFile().getAbsolutePath();
		String logicalPath = pathPatter.replace("../", "") +"/**";
		
		registry.addResourceHandler(logicalPath)
		.addResourceLocations("file:/"+absolutPath +"/");
	}


}
