package th.co.infinitait.goodluck.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateExcelService {

	public File genExcel(String template, String fileName, String pathOutput, Context context) throws IOException {
		String fullPathName = pathOutput + File.separator + fileName;

		createFolder(pathOutput);

		File outputFile = new File(fullPathName);

		InputStream inputTemplate = null;
		OutputStream outputStream = null;
		try {
			log.info("template : {}",template);
			log.info("outputFile : {}",outputFile);
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			inputTemplate = classloader.getResourceAsStream(template);
			outputStream = new FileOutputStream(outputFile);

			// Write excel file
			JxlsHelper.getInstance().processTemplate(inputTemplate, outputStream, context);

			return outputFile;
		} catch (IOException ex) {
			log.error("Generate {} fail, {}", fileName, ex.getCause());
			log.error("Exception {}", ex.getMessage());
			throw ex;
		} finally {
			IOUtils.closeQuietly(inputTemplate);
			IOUtils.closeQuietly(outputStream);
		}
	}

	public void createFolder(String tmpPath) throws IOException {
		org.apache.tomcat.util.http.fileupload.FileUtils.forceMkdir(new java.io.File(tmpPath));
	}

	public void deleteFile(java.io.File file) throws IOException {
		org.apache.tomcat.util.http.fileupload.FileUtils.forceDelete(file);
	}
}
