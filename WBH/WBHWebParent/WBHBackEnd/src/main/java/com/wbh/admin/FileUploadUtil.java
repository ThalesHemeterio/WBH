package com.wbh.admin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.web.multipart.MultipartFile;

// class responsible for saving the user photos

public class FileUploadUtil {
	//method use to save the photos
	public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
		Path uploadPath = Paths.get(uploadDir);
		if(!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}
		try(InputStream inputStream = multipartFile.getInputStream()){
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ex) {
			throw new IOException("Could not save the file: "+fileName, ex);
		}
	}
	//method to clean the folder when updating/deleting photos
	public static void cleanDirectory (String directory) {
		Path dirPath = Paths.get(directory);
		
		try {
			Files.list(dirPath).forEach(file -> {
				if(!Files.isDirectory(file)) {
					try {
						Files.delete(file);
					}catch (IOException ex) {
						System.out.println("could not delet file:" + file);
					}
				}
			});
		}catch (IOException ex) {
			System.out.println("could not find directory:" + dirPath);
		}
	}
}
