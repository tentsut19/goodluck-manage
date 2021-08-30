package th.co.infinitait.goodluck.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import th.co.infinitait.goodluck.entity.CustomerEntity;
import th.co.infinitait.goodluck.entity.OrderEntity;
import th.co.infinitait.goodluck.entity.OrderProductEntity;
import th.co.infinitait.goodluck.entity.SettingProductEntity;
import th.co.infinitait.goodluck.exception.InvalidParameterException;
import th.co.infinitait.goodluck.model.OrderDetailReport;
import th.co.infinitait.goodluck.repository.OrderProductRepository;
import th.co.infinitait.goodluck.repository.OrderRepository;
import th.co.infinitait.goodluck.repository.SettingProductRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JasperReportsService {

	private final FileSystemStorageService storageService;

	private final OrderRepository orderRepository;

	private final OrderProductRepository orderProductRepository;

	private final SettingProductRepository settingProductRepository;

	@Value("${report-generate-path}")
	private String pdfFilePath;

	public String generateReportPdf(String jasperFileName, String fileName, Map<String, Object> params) {
		log.info("jasperFileName : {}",jasperFileName);
		JasperReport jasperReport = null;
		try (ByteArrayOutputStream byteArray = new ByteArrayOutputStream()) {
			// Check if a compiled report exists
			if (storageService.jasperFileExists(jasperFileName)) {
				jasperReport = (JasperReport) JRLoader.loadObject(storageService.loadJasperFile(jasperFileName));
			}
			// Compile report from source and save
			else {
				String jrxml = storageService.loadJrxmlFile(jasperFileName);
				jasperReport = JasperCompileManager.compileReport(jrxml);
				// Save compiled report. Compiled report is loaded next time
				JRSaver.saveObject(jasperReport, storageService.loadJasperFile(jasperFileName));
			}
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
			String exportPath = pdfFilePath + "/" + fileName +".pdf";
			log.info("exportPath : {}",exportPath);
			// return the PDF in bytes
			JasperExportManager.exportReportToPdfFile(jasperPrint,exportPath);

			return exportPath;
		} catch (Exception e) {
			throw new InvalidParameterException(e.getMessage());
		}
	}

	public String createOrderReceiptReport(String jasperFileName, List<Long> orderIdList) throws IOException {
		log.info("jasperFileName : {}, orderIdList : {}",jasperFileName, orderIdList);
		SimpleDateFormat formatterDDMMYYYYHHmm = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("th", "TH"));
		SimpleDateFormat formatterDDMMYYYY = new SimpleDateFormat("dd/MM/yyyy", new Locale("th", "TH"));
		DecimalFormat df = new DecimalFormat( "#,##0.00" );
		String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		Map<String, Object> params = new HashMap<>();

		if(!CollectionUtils.isEmpty(orderIdList)){
			PDFMergerUtility ut = new PDFMergerUtility();
			int i = 0;
			for(Long orderId:orderIdList){
				List<OrderDetailReport> orderDetailReportList = new ArrayList<>();
				String fileName = "order_receipt_"+i;
				Optional<OrderEntity> optional = orderRepository.findById(orderId);
				if(!optional.isPresent()){
					continue;
				}
				BigDecimal allPrice = BigDecimal.ZERO;
				BigDecimal discountPrice = BigDecimal.ZERO;
				BigDecimal shippingCostPrice = new BigDecimal("60");
				OrderEntity orderEntity = optional.get();
				List<OrderProductEntity> orderProductEntityList = orderProductRepository.findByOrderCode(orderEntity.getCode());
				if(!CollectionUtils.isEmpty(orderProductEntityList)) {
					int no = 1;
					for(OrderProductEntity orderProductEntity:orderProductEntityList) {
						OrderDetailReport orderDetailReport = new OrderDetailReport();
						orderDetailReport.setNo((no++)+"");
						orderDetailReport.setDescription(orderProductEntity.getProductName());
						orderDetailReport.setQuantity(orderProductEntity.getProductQuantity() + "");
						String unitPrice = "0.00";
						String amount = "0.00";
						List<SettingProductEntity> settingProductEntityList = settingProductRepository.findByName(orderProductEntity.getProductName());
						if (!CollectionUtils.isEmpty(settingProductEntityList)) {
							SettingProductEntity settingProductEntity = settingProductEntityList.get(settingProductEntityList.size() - 1);
							BigDecimal productPrice = settingProductEntity.getPrice();
							if (productPrice != null) {
								unitPrice = df.format(productPrice);
								amount = df.format(productPrice.multiply(new BigDecimal(orderProductEntity.getProductQuantity())));
							}
							allPrice = allPrice.add(productPrice);
						}
						orderDetailReport.setUnitPrice(unitPrice);
						orderDetailReport.setAmount(amount);
						orderDetailReportList.add(orderDetailReport);
					}
				}

				params.put("orderDetailReportList", orderDetailReportList);

				params.put("receiptNo", "หมายเลขใบเสร็จรับเงิน : "+orderEntity.getCode().replace("OD","RC"));

				params.put("receivedBy", "ผู้รับเงิน : บริษัท อินฟินิตี้ ริช88 มาร์เก็ตติ้ง ออนไลน์ จำกัด");
				params.put("receivedTel", "ติดต่อ : 064 995 5553");
				params.put("receivedAddress", "ที่อยู่ : 199/88 หมู่บ้านเซนโทร ราชพฤกษ์ 2 หมู่ที่ 7 ตำบลบางกร่อง อำเภอเมืองนนทบุรี จังหวัดนนทบุรี 11000");
				params.put("receivedTaxId", "เลขประจำตัวผู้เสียภาษี : 0125564006363");

				String customerName = "";
				String customerTel = "";
				String customerAddress = "";
				if(orderEntity.getCustomer() != null){
					CustomerEntity customerEntity = orderEntity.getCustomer();
					customerName = customerEntity.getName();
					customerTel = customerEntity.getPhoneNumber();
					customerAddress = customerEntity.getAddress();
				}

				params.put("customerName", "ลูกค้า : " + customerName);
				params.put("customerTel", "ติดต่อ : " + customerTel);
				params.put("customerAddress", "ที่อยู่ : " + customerAddress);
				params.put("date", "วันที่ : "+formattedDate);

				BigDecimal totalNetPrice = allPrice.add(shippingCostPrice);

				BigDecimal shippingCostPriceAdd = orderEntity.getTotalAmount().subtract(totalNetPrice);
				if(shippingCostPriceAdd.compareTo(BigDecimal.ZERO) > 0){
					shippingCostPrice = shippingCostPrice.add(shippingCostPriceAdd);
				}

				params.put("all", df.format(allPrice) + " บาท");
				params.put("discount", df.format(discountPrice) + " บาท");
				params.put("shippingCost", df.format(shippingCostPrice) + " บาท");
				params.put("totalNetPrice", df.format(orderEntity.getTotalAmount()) + " บาท");

				BigDecimal notVat = orderEntity.getTotalAmount().multiply(new BigDecimal("100"));
				notVat = notVat.divide(new BigDecimal("107"), RoundingMode.CEILING);

				BigDecimal vat = orderEntity.getTotalAmount().subtract(notVat);

				params.put("vat", df.format(vat) + " บาท");
				params.put("notVat", df.format(notVat) + " บาท");

				generateReportPdf(jasperFileName, fileName, params);

				String exportPath = pdfFilePath + "/order_receipt_"+i+".pdf";
				log.info("downloadCustomerServiceCallReport exportPath1 : {}", exportPath);
				File file = new File(exportPath);
				ut.addSource(file);

				i++;
			}
			ut.setDestinationFileName(pdfFilePath + "/order_receipt.pdf");
			ut.mergeDocuments();
		}

		return "";
	}
}
