package th.co.infinitait.goodluck.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.poi.ss.formula.functions.T;
import org.jxls.common.Context;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import th.co.infinitait.goodluck.entity.*;
import th.co.infinitait.goodluck.exception.InvalidParameterException;
import th.co.infinitait.goodluck.model.OrderDetailReport;
import th.co.infinitait.goodluck.model.request.TransportRequest;
import th.co.infinitait.goodluck.model.response.*;
import th.co.infinitait.goodluck.repository.*;
import th.co.infinitait.goodluck.util.NumberFormat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class JasperReportsService {

	private final FileSystemStorageService storageService;

	private final OrderRepository orderRepository;

	private final OrderProductRepository orderProductRepository;

	private final SettingProductRepository settingProductRepository;

	private final CompanyRepository companyRepository;

	private final GenerateExcelService generateExcelService;

	private final GenTransportRepository genTransportRepository;

	@Value("${report-generate-path}")
	private String pdfFilePath;

	@Value("${environment}")
	private String environment;

	public String generateReportPdf(String jasperFileName, String fileName, Map<String, Object> params) {
		log.info("jasperFileName : {}",jasperFileName);
		JasperReport jasperReport = null;
		try (ByteArrayOutputStream byteArray = new ByteArrayOutputStream()) {
			// Check if a compiled report exists
			if (storageService.jasperFileExists(jasperFileName)) {
				log.info("Check if a compiled report exists");
//				jasperReport = (JasperReport) JRLoader.loadObject(storageService.loadJasperFile(jasperFileName));
				jasperReport = (JasperReport) JRLoader.loadObject(storageService.loadJasperFile(jasperFileName));
			}
			// Compile report from source and save
			else {
				log.info("Compile report from source and save");
				String jrxml = jasperFileName+".jrxml";
				if(!environment.equalsIgnoreCase("prod")) {
					URL url = new URL("https://ecommerce-uat-bucket.s3.ap-southeast-1.amazonaws.com/api/reports/receipt_v1.jrxml");
					FileUtils.copyURLToFile(url, new File("receipt_v1.jrxml"));
				}else{
					jrxml = storageService.loadJrxmlFile(jasperFileName);
				}
				log.info("jrxml : {}",jrxml);
				jasperReport = JasperCompileManager.compileReport(jrxml);
				log.info("=== JasperCompileManager ===");
				// Save compiled report. Compiled report is loaded next time
//				JRSaver.saveObject(jasperReport, storageService.loadJasperFile(jasperFileName));
				JRSaver.saveObject(jasperReport, new File("receipt.jasper"));
				log.info("=== JRSaver saveObject ===");
			}
			log.info("JasperFillManager.fillReport");
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());
//			String exportPath = pdfFilePath + "/" + fileName +".pdf";
			String exportPath = fileName +".pdf";
			log.info("exportPath : {}",exportPath);
			// return the PDF in bytes
			JasperExportManager.exportReportToPdfFile(jasperPrint,exportPath);

			return exportPath;
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidParameterException(e.getMessage());
		}
	}

	public String createOrderReceiptReport(String jasperFileName, List<Long> orderIdList, Long companyId) throws IOException {
		log.info("jasperFileName : {}, orderIdList : {}",jasperFileName, orderIdList);
		SimpleDateFormat formatterDDMMYYYYHHmm = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("th", "TH"));
		SimpleDateFormat formatterDDMMYYYY = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
		DecimalFormat df = new DecimalFormat( "#,##0.00" );
		String formattedDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		Map<String, Object> params = new HashMap<>();

		CompanyEntity companyEntity = new CompanyEntity();
		Optional<CompanyEntity> optionalCompanyEntity = companyRepository.findById(companyId);
		if(optionalCompanyEntity.isPresent()){
			companyEntity = optionalCompanyEntity.get();
		}

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
//				BigDecimal shippingCostPrice = new BigDecimal("60");
				OrderEntity orderEntity = optional.get();
				List<OrderProductEntity> orderProductEntityList = orderProductRepository.findByOrderCode(orderEntity.getCode());
				if(!CollectionUtils.isEmpty(orderProductEntityList)) {
					int no = 1;
					for(OrderProductEntity orderProductEntity:orderProductEntityList) {
						Integer quantity = orderProductEntity.getProductQuantity();
						OrderDetailReport orderDetailReport = new OrderDetailReport();
						orderDetailReport.setNo((no++)+"");
						orderDetailReport.setDescription(orderProductEntity.getProductName());
						orderDetailReport.setQuantity(""+quantity);

						String unitPrice = "0.00";
						String amount = "0.00";
						List<SettingProductEntity> settingProductEntityList = settingProductRepository.findByName(orderProductEntity.getProductName());
						if (!CollectionUtils.isEmpty(settingProductEntityList)) {
							SettingProductEntity settingProductEntity = settingProductEntityList.get(settingProductEntityList.size() - 1);
							BigDecimal productPrice = settingProductEntity.getPrice();
							if (productPrice != null) {
								unitPrice = df.format(productPrice);
								amount = df.format(productPrice.multiply(new BigDecimal(quantity)));
								allPrice = allPrice.add(productPrice.multiply(new BigDecimal(quantity)));
							}
						}
						orderDetailReport.setUnitPrice(unitPrice);
						orderDetailReport.setAmount(amount);
						orderDetailReportList.add(orderDetailReport);
					}
				}

				params.put("orderDetailReportList", orderDetailReportList);

				params.put("orderNo", "เลขที่ / No. : "+orderEntity.getCode());

				params.put("receiptNo", "หมายเลขใบเสร็จรับเงิน : "+orderEntity.getCode().replace("OD","RC"));

//				params.put("receivedBy", "ผู้รับเงิน : บริษัท อินฟินิตี้ ริช88 มาร์เก็ตติ้ง ออนไลน์ จำกัด");
//				params.put("receivedTel", "ติดต่อ : 064 995 5553");
//				params.put("receivedAddress", "ที่อยู่ : 199/88 หมู่บ้านเซนโทร ราชพฤกษ์ 2 หมู่ที่ 7 ตำบลบางกร่อง อำเภอเมืองนนทบุรี จังหวัดนนทบุรี 11000");
//				params.put("receivedTaxId", "เลขประจำตัวผู้เสียภาษี : 0125564006363");

				params.put("receivedBy", companyEntity.getName());
				params.put("receivedTel", "โทร : "+companyEntity.getPhoneNumber());

				String receivedAddress = "";
				if(!CollectionUtils.isEmpty(companyEntity.getAddressList())){
					AddressEntity address = companyEntity.getAddressList().get(companyEntity.getAddressList().size()-1);
					if(address != null){
						receivedAddress =
								(StringUtils.isEmpty(address.getDetail()) ? "" : address.getDetail()) + " " +
								(StringUtils.isEmpty(address.getSubdistrict()) ? "" : "ตำบล/แขวง "+address.getSubdistrict()) + " " +
								(StringUtils.isEmpty(address.getDistrict()) ? "" : "อำเภอ/เขต "+address.getDistrict()) + " " +
								(StringUtils.isEmpty(address.getProvince()) ? "" : "จังหวัด " + address.getProvince()) + " " +
								(StringUtils.isEmpty(address.getPostCode()) ? "" : address.getPostCode());
					}
				}

				params.put("receivedAddress", receivedAddress);
				params.put("receivedTaxId", "เลขประจำตัวผู้เสียภาษี : "+companyEntity.getTaxIdentificationNumber());

				String customerName = "";
				String customerTel = "";
				String customerAddress = "";
				if(orderEntity.getCustomer() != null){
					CustomerEntity customerEntity = orderEntity.getCustomer();
					customerName = customerEntity.getName();
					customerTel = customerEntity.getPhoneNumber();
					customerAddress = customerEntity.getAddress();
				}

				String dateOrder = "";
				if(orderEntity.getUpdatedAt() != null) {
					dateOrder = formatterDDMMYYYY.format(orderEntity.getUpdatedAt());
				}

				params.put("customerName", "ชื่อลูกค้า/Customers : " + customerName);
				params.put("customerTel", "ติดต่อ / Tel : " + customerTel);
				params.put("customerAddress", "ที่อยู่ / Address : " + customerAddress);
				params.put("date", "วันที่ / Date : "+dateOrder);

//				BigDecimal totalNetPrice = allPrice.add(shippingCostPrice);
//				BigDecimal shippingCostPriceAdd = orderEntity.getTotalAmount().subtract(totalNetPrice);
//				if(shippingCostPriceAdd.compareTo(BigDecimal.ZERO) > 0){
//					shippingCostPrice = shippingCostPrice.add(shippingCostPriceAdd);
//				}

//				BigDecimal amountDiff = orderEntity.getTotalAmount().subtract(allPrice);
//				if(shippingCostPriceAdd.compareTo(BigDecimal.ZERO) <= 0){
//					shippingCostPrice = BigDecimal.ZERO;
//					discountPrice = amountDiff;
//				}

				BigDecimal notVat = orderEntity.getTotalAmount().multiply(new BigDecimal("100"));
				notVat = notVat.divide(new BigDecimal("107"), RoundingMode.HALF_UP);

				BigDecimal vat = orderEntity.getTotalAmount().subtract(notVat);

				params.put("vat", df.format(vat) + " บาท");
				params.put("notVat", df.format(notVat) + " บาท");

				discountPrice = notVat.subtract(allPrice);
				if(discountPrice.compareTo(BigDecimal.ZERO) <= 0){
					discountPrice = discountPrice.multiply(new BigDecimal("-1"));
				}

				params.put("all", df.format(allPrice) + " บาท");
				params.put("discount", df.format(discountPrice) + " บาท");
//				params.put("shippingCost", df.format(shippingCostPrice) + " บาท");
				params.put("totalNetPrice", df.format(orderEntity.getTotalAmount()) + " บาท");

				params.put("totalNetPriceText", "("+new NumberFormat().getThaiBaht(orderEntity.getTotalAmount())+")");

				params.put("pathLogo", "images/logo1.jpg");

				generateReportPdf(jasperFileName, fileName, params);

//				String exportPath = pdfFilePath + "/order_receipt_"+i+".pdf";
				String exportPath = "order_receipt_"+i+".pdf";
				log.info("downloadCustomerServiceCallReport exportPath1 : {}", exportPath);
				File file = new File(exportPath);
				ut.addSource(file);

				i++;
			}
//			ut.setDestinationFileName(pdfFilePath + "/order_receipt.pdf");
			ut.setDestinationFileName("order_receipt.pdf");
			ut.mergeDocuments();
		}

		return "";
	}

	public GenTransportResponse getTransportStatus(String uuid) throws Exception {
		GenTransportResponse response = new GenTransportResponse();
		Optional<GenTransportEntity> optionalGenTransport = genTransportRepository.findById(uuid);
		if(optionalGenTransport.isPresent()){
			GenTransportEntity genTransportEntity = optionalGenTransport.get();
			response.setId(genTransportEntity.getUuid());
			response.setStatus(genTransportEntity.getStatus().name());
			response.setState(genTransportEntity.getState().name());
			response.setErrorMessage(genTransportEntity.getErrorMessage());
			response.setCreatedAt(genTransportEntity.getCreatedAt());
			response.setCreatedBy(genTransportEntity.getCreatedBy());
			response.setUpdatedAt(genTransportEntity.getUpdatedAt());
			response.setUpdatedBy(genTransportEntity.getUpdatedBy());
		}
		return response;
	}

	public String startTransportExcel(TransportRequest request) throws Exception {
		String uuid = RandomStringUtils.randomAlphanumeric(64);
		GenTransportEntity genTransportEntity = new GenTransportEntity();
		genTransportEntity.setUuid(uuid);
		genTransportEntity.setStatus(EStatus.PROCESSING);
		genTransportEntity.setState(EState.PROCESSING);
		genTransportEntity.setCreatedAt(new Date());
		genTransportEntity.setCreatedBy("test");
		genTransportRepository.save(genTransportEntity);
		return uuid;
	}

	@Async("taskExecutor")
	public void createTransportExcel(String uuid,TransportRequest request) throws Exception {
		try {
			List<TransportResponse> transportResponseList = new ArrayList<>();
			String template = "";
			List<Long> orderIdList = request.getOrderIdList();
			if (!CollectionUtils.isEmpty(orderIdList)) {
				int no = 1;
				for (Long orderId : orderIdList) {
					Optional<OrderEntity> optional = orderRepository.findById(orderId);
					if (!optional.isPresent()) {
						continue;
					}
					OrderEntity orderEntity = optional.get();
					TransportResponse transportResponse = new TransportResponse();
					transportResponse.setNo(no);
					transportResponse.setConsigneeName(orderEntity.getRecipientName());
					if (orderEntity.getCustomer() != null) {
						CustomerEntity customerEntity = orderEntity.getCustomer();
						String address = customerEntity.getAddress();

						if ("kerry".equalsIgnoreCase(request.getTransport())) {
							String[] address1 = address.split("ต\\.");
							if (address1.length == 2) {
								transportResponse.setAddress1(address1[0]);
								transportResponse.setAddress2("ต." + address1[1]);
							} else {
								address1 = address.split("แขวง");
								if (address1.length == 2) {
									transportResponse.setAddress1(address1[0]);
									transportResponse.setAddress2("แขวง" + address1[1]);
								}
							}
							if(StringUtils.isEmpty(transportResponse.getAddress1())){
								address1 = address.split("ตำบล");
								if (address1.length == 2) {
									transportResponse.setAddress1(address1[0]);
									transportResponse.setAddress2("ตำบล" + address1[1]);
								}
							}
							if(StringUtils.isEmpty(transportResponse.getAddress1())){
								address1 = address.split("เขต");
								if (address1.length == 2) {
									transportResponse.setAddress1(address1[0]);
									transportResponse.setAddress2("เขต" + address1[1]);
								}
							}
							if(StringUtils.isEmpty(transportResponse.getAddress1())){
								transportResponse.setAddress1(address);
							}
							transportResponse.setProduct(orderEntity.getProductDraftName()+" "+orderEntity.getQuantity());
						}else if ("flash".equalsIgnoreCase(request.getTransport())) {
							if("cod".equalsIgnoreCase(orderEntity.getPaymentChannel())) {
								address = "Cod-" + orderEntity.getTotalAmount() + " "
										+ address;
							}
							transportResponse.setAddress1(address);
						}
						if (!StringUtils.isEmpty(customerEntity.getAddress())) {
							String postalCode = "";
							Pattern p = Pattern.compile("[0-9๐-๙]{5}");
							Matcher m = p.matcher(customerEntity.getAddress());
							while (m.find()) {
								postalCode = m.group();
							}
							transportResponse.setPostalCode(postalCode);
						}
						transportResponse.setPhoneNumber(customerEntity.getPhoneNumber());
					}
					if("cod".equalsIgnoreCase(orderEntity.getPaymentChannel())) {
						transportResponse.setCod(orderEntity.getTotalAmount());
					}

					float weightKg = 1f;
					List<OrderProductEntity> orderProductEntityList = orderProductRepository.findByOrderCode(orderEntity.getCode());
					if (!CollectionUtils.isEmpty(orderProductEntityList)) {
						StringBuilder product = new StringBuilder();
						int i = 0;
						for(OrderProductEntity orderProduct:orderProductEntityList){
							String productName = orderProduct.getProductName();
							Integer productQuantity = orderProduct.getProductQuantity();
							if(i==0){
								product = new StringBuilder(productName + " " + productQuantity);
							}else{
								product.append(" ").append(productName).append(" ").append(productQuantity);
							}
							i++;
						}

						if ("kerry".equalsIgnoreCase(request.getTransport())) {
							transportResponse.setProduct(product.toString());
						}else if ("flash".equalsIgnoreCase(request.getTransport())) {
							transportResponse.setCustomerOrderNumber(product.toString());
//							for (OrderProductEntity orderProductEntity : orderProductEntityList) {
//								List<SettingProductEntity> settingProductEntityList = settingProductRepository.findByName(orderProductEntity.getProductName());
//								if (!CollectionUtils.isEmpty(settingProductEntityList)) {
//									SettingProductEntity settingProductEntity = settingProductEntityList.get(settingProductEntityList.size() - 1);
//									weightKg += settingProductEntity.getWeightKg();
//								}
//							}
						}
					}
					transportResponse.setWeightKg(weightKg);
					transportResponseList.add(transportResponse);
					no++;
				}
			}

			if ("flash".equalsIgnoreCase(request.getTransport())) {
				template = "template/transport_order_flash_template.xlsx";

			} else if ("kerry".equalsIgnoreCase(request.getTransport())) {
				template = "template/transport_order_kerry_template.xlsx";

			}
			String pathOutput = "report/excel/transport";
			String fileName = "transport_order_" + request.getTransport() + ".xlsx";

			genExcel(transportResponseList, template, pathOutput, fileName);
			Optional<GenTransportEntity> optionalGenTransport = genTransportRepository.findById(uuid);
			if(optionalGenTransport.isPresent()){
				GenTransportEntity genTransportEntity = optionalGenTransport.get();
				genTransportEntity.setState(EState.SUCCESS);
				genTransportEntity.setStatus(EStatus.SUCCESS);
				genTransportEntity.setUpdatedAt(new Date());
				genTransportEntity.setUpdatedBy("test");
				genTransportRepository.save(genTransportEntity);
			}
			log.info("End createTransportExcel");
		}catch (Exception e){
			Optional<GenTransportEntity> optionalGenTransport = genTransportRepository.findById(uuid);
			if(optionalGenTransport.isPresent()){
				GenTransportEntity genTransportEntity = optionalGenTransport.get();
				genTransportEntity.setState(EState.FAIL);
				genTransportEntity.setStatus(EStatus.FAIL);
				genTransportEntity.setErrorMessage(e.getMessage());
				genTransportEntity.setUpdatedAt(new Date());
				genTransportEntity.setUpdatedBy("test");
				genTransportRepository.save(genTransportEntity);
			}
		}
	}

	public void genExcel(List<?> models, String template, String pathOutput, String fileName) throws Exception {

		Context context = new Context();
		// Header
//        context.putVar("exportDate", exportDate);
		// Detail
		context.putVar("models", models);
		// Footer
//        context.putVar("totals", total);
//        context.putVar("timePerWorkTotal", timePerWorkTotal);
//        context.putVar("dayWork", dayWork);

		File file = generateExcelService.genExcel(template, fileName, pathOutput, context);
		byte[] fileContent = FileUtils.readFileToByteArray(file);
//        fileService.deleteFile(file);
	}
}
