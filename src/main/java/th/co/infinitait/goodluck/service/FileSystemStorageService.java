/*******************************************************************************
 * Copyright 2018, Julius Krah
 * by the @authors tag. See the LICENCE in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package th.co.infinitait.goodluck.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import th.co.infinitait.goodluck.config.Properties;
import th.co.infinitait.goodluck.exception.StorageException;
import th.co.infinitait.goodluck.exception.StorageFileNotFoundException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Slf4j
@Service
public class FileSystemStorageService {

	private final Path rootLocation;
	private final Properties properties;

	public FileSystemStorageService(Properties properties) throws IOException {
		this.properties = properties;
//		if(System.getProperty("os.name").contains("indow")){
//			this.rootLocation = Paths.get(properties.getStorageLocation().getURL().getPath().substring(1,properties.getStorageLocation().getURL().getPath().length()));
//		}else{
//			this.rootLocation = Paths.get(properties.getStorageLocation().getURL().getPath());
//		}
		this.rootLocation = Paths.get("https://ecommerce-uat-bucket.s3.ap-southeast-1.amazonaws.com/api/reports/");
		// https://ecommerce-uat-bucket.s3.ap-southeast-1.amazonaws.com/api/reports/receipt.jrxml
	}

	public void init() {
		try {
			Files.createDirectory(rootLocation);
		}
		catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}

	public void deleteAll() {
		try {
			FileSystemUtils.deleteRecursively(rootLocation);
		}
		catch (IOException e) {
			throw new StorageException("Could not delete files and folders", e);
		}
	}

	public boolean jrxmlFileExists(String file) {
		// @formatter:off
		try {
			Path reportFile = Paths.get(properties.getReportLocation().getURI());
			reportFile = reportFile.resolve(file + ".jrxml");
			if (Files.exists(reportFile))
				return true;
		} catch (IOException e) {
			log.error("Error while trying to get file URI", e);
			return false;
		}
		// @formatter:on
		return false;
	}

	public boolean jasperFileExists(String file) {
		Path reportFile = rootLocation;
		log.info("reportFile1 : {}",reportFile);
		reportFile = reportFile.resolve(file + ".jasper");
		log.info("reportFile2 : {}",reportFile);
		if (Files.exists(reportFile))
			return true;
		return false;
	}

	public String loadJrxmlFile(String file) {
		// @formatter:off
		try {
			log.info("loadJrxmlFile : {}",file);
			log.info("getReportLocation : {}",properties.getReportLocation());
			log.info("getReportLocation getURI : {}",properties.getReportLocation().getURI());
			Path reportFile = Paths.get(properties.getReportLocation().getURI());
			log.info("reportFile : {}",reportFile);
			reportFile = reportFile.resolve(file + ".jrxml");
			log.info("reportFile.toString() : {}",reportFile.toString());
			return reportFile.toString();
		} catch (IOException e) {
			log.error("Error while trying to get file prefix", e);
			throw new StorageFileNotFoundException("Could not load file", e);
		}
		// @formatter:on
	}

	public File loadJasperFile(String file) {
		// @formatter:off
		try {
			Path reportFile = Paths.get(properties.getReportLocation().getURI());
			reportFile = reportFile.resolve(file + ".jasper");
			return reportFile.toFile();
		} catch (IOException e) {
			log.error("Error while trying to get file prefix", e);
			throw new StorageFileNotFoundException("Could not load file", e);
		}
		// @formatter:on
	}
}
