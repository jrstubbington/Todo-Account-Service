package org.example.todo.accounts.service;

import lombok.extern.slf4j.Slf4j;
import org.example.todo.accounts.exceptions.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class StorageService {

	@Value("${upload.path}")
	private String path;

	public String uploadFile(MultipartFile file) {

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		if (file.isEmpty()) {
			throw new StorageException("Failed to store empty file");
		}

		try {
			UUID filePrefix = UUID.randomUUID();
			String fileName = filePrefix + "_" + file.getOriginalFilename();
			InputStream is = file.getInputStream();
			log.info("Saving file to {}{}", path, fileName);
			Files.copy(is, Paths.get(path + fileName),
					StandardCopyOption.REPLACE_EXISTING);

			stopWatch.stop();
			log.info("Saving file {} took {}", file.getOriginalFilename(), stopWatch.getTotalTimeSeconds());

			return path + fileName;
		}
		catch (IOException e) {

			String msg = String.format("Failed to store file %s", file.getName());

			throw new StorageException(msg, e);
		}

	}

	public void deleteFile(String fileLocation) throws IOException {
		Files.delete(Paths.get(fileLocation));
	}
}
