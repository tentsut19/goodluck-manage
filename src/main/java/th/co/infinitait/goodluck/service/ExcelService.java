package th.co.infinitait.goodluck.service;

import com.groupdocs.conversion.Converter;
import com.groupdocs.conversion.options.convert.SpreadsheetConvertOptions;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import th.co.infinitait.goodluck.client.CabsatClient;
import th.co.infinitait.goodluck.component.CabsatPayload;
import th.co.infinitait.goodluck.entity.*;
import th.co.infinitait.goodluck.model.request.OrderRequest;
import th.co.infinitait.goodluck.model.response.OrderResponse;
import th.co.infinitait.goodluck.repository.*;

import java.io.*;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExcelService {

    private final ExcelHelperService excelHelperService;
    private final OrderRepository orderRepository;
    private final UpdateOrderRepository updateOrderRepository;
    private final CabsatClient cabsatClient;

    @Value("${report-generate-path}")
    private String pdfFilePath;

    public void createUpdateOrder(String userId, String uuid) {
        UpdateOrderEntity updateOrder = new UpdateOrderEntity();
        updateOrder.setUuid(uuid);
        updateOrder.setStatus("start");
        updateOrder.setState("Shipping");
        updateOrder.setCurrent(0);
        updateOrder.setTotal(1);
        updateOrder.setErrorMessage("เริ่มการอัพเดทข้อมูล");
        updateOrder.setCreatedBy(userId);
        updateOrder.setCreatedAt(new Date());
        updateOrderRepository.save(updateOrder);
    }

    @Async("taskExecutor")
    public void uploadFileUpdateParcelCode(MultipartFile file, String transportationService, String userId, String uuid) throws Exception {
        try {
            List<OrderRequest> orderRequestList = excelHelperService.excelToMapParcelCode(file.getInputStream(),transportationService,"Order Template");
            log.info("orderRequestList : {}", orderRequestList.size());
            updateParcelCode(orderRequestList,transportationService,userId,uuid);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            UpdateOrderEntity updateOrder = new UpdateOrderEntity();
            updateOrder.setUuid(uuid);
            updateOrder.setStatus("fail");
            updateOrder.setState("Shipping");
            updateOrder.setCurrent(0);
            updateOrder.setTotal(0);
            updateOrder.setErrorMessage("ไฟล์เกิดข้อผิดพลาด");
            updateOrder.setCreatedBy(userId);
            updateOrder.setCreatedAt(new Date());
            updateOrderRepository.save(updateOrder);
        }
    }

    public void updateParcelCode(List<OrderRequest> orderRequestList, String transportationService, String userId, String uuid) {
        List<UpdateOrderEntity> updateOrderEntityList = new ArrayList<>();
        if (CollectionUtils.isEmpty(orderRequestList)) {
            UpdateOrderEntity updateOrder = new UpdateOrderEntity();
            updateOrder.setUuid(uuid);
            updateOrder.setStatus("fail");
            updateOrder.setState("Shipping");
            updateOrder.setCurrent(0);
            updateOrder.setTotal(0);
            updateOrder.setErrorMessage("ไฟล์เกิดข้อผิดพลาด");
            updateOrder.setCreatedBy(userId);
            updateOrder.setCreatedAt(new Date());
            updateOrderRepository.save(updateOrder);
            updateOrderEntityList.add(updateOrder);
            log.info("fail size : {}", updateOrderEntityList.size());
            return;
        }
        updateOrderRepository.deleteByState("Shipping");
        int current = 0;
        int total = orderRequestList.size();
        for (OrderRequest orderRequest : orderRequestList) {
            log.info("current : {}, total : {}", current++,total);
            try {
                List<OrderEntity> orderList = orderRepository.findByRecipientName(orderRequest.getRecipientName().trim());
                if (!CollectionUtils.isEmpty(orderList)) {
                    if(orderList.size() > 1){
                        int i = 0;
                        StringBuilder orders = new StringBuilder();
                        for(OrderEntity order:orderList){
                            if(i == 0){
                                orders = new StringBuilder(order.getCode());
                            }else{
                                orders.append(",").append(order.getCode());
                            }
                            i++;
                        }

                        UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                        updateOrder.setUuid(uuid);
                        updateOrder.setStatus("fail");
                        updateOrder.setState("Shipping");
                        updateOrder.setCurrent(current);
                        updateOrder.setTotal(total);
                        updateOrder.setErrorMessage("ชื่อผู้รับ : "+orderRequest.getRecipientName().trim()+" มีคำสั่งซื้อซ้ำ "+orders.toString());
                        updateOrder.setParcelCode(orderRequest.getParcelCode());
                        updateOrder.setRecipientName(orderRequest.getRecipientName());
                        updateOrder.setCreatedBy(userId);
                        updateOrder.setCreatedAt(new Date());
                        updateOrderRepository.save(updateOrder);
                        updateOrderEntityList.add(updateOrder);
                    }else{
                        OrderEntity orderEntity = orderList.get(0);
                        if (orderEntity.getStatus().equalsIgnoreCase("Draft") ||
                                orderEntity.getStatus().equalsIgnoreCase("Shipping")) {
                            orderEntity.setParcelCode(orderRequest.getParcelCode());
                            orderEntity.setStatus("Shipping");
                            orderEntity.setTransportationService(transportationService);
                            orderEntity.setUpdatedBy(userId);
                            orderEntity.setUpdatedAt(new Date());
                            orderEntity.setDeliveryDate(new Date());
                            orderRepository.save(orderEntity);

                            UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                            updateOrder.setUuid(uuid);
                            updateOrder.setStatus("success");
                            updateOrder.setState("Shipping");
                            updateOrder.setCurrent(current);
                            updateOrder.setTotal(total);
                            updateOrder.setTotalAmount(orderEntity.getTotalAmount());
                            updateOrder.setParcelCode(orderRequest.getParcelCode());
                            updateOrder.setRecipientName(orderRequest.getRecipientName());
                            updateOrder.setCreatedBy(userId);
                            updateOrder.setCreatedAt(new Date());
                            updateOrderRepository.save(updateOrder);
                            updateOrderEntityList.add(updateOrder);
                        } else {
                            UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                            updateOrder.setUuid(uuid);
                            updateOrder.setStatus("fail");
                            updateOrder.setState("Shipping");
                            updateOrder.setCurrent(current);
                            updateOrder.setTotal(total);
                            updateOrder.setErrorMessage("คำสั่งซื้อของชื่อผู้รับนี้อยู่ในสถานะ "+orderEntity.getStatus());
                            updateOrder.setParcelCode(orderRequest.getParcelCode());
                            updateOrder.setRecipientName(orderRequest.getRecipientName());
                            updateOrder.setCreatedBy(userId);
                            updateOrder.setCreatedAt(new Date());
                            updateOrderRepository.save(updateOrder);
                            updateOrderEntityList.add(updateOrder);
                        }
                    }
                } else {
                    UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                    updateOrder.setUuid(uuid);
                    updateOrder.setStatus("fail");
                    updateOrder.setState("Shipping");
                    updateOrder.setCurrent(current);
                    updateOrder.setTotal(total);
                    updateOrder.setErrorMessage("ไม่มีชื่อผู้รับในระบบ");
                    updateOrder.setParcelCode(orderRequest.getParcelCode());
                    updateOrder.setRecipientName(orderRequest.getRecipientName());
                    updateOrder.setCreatedBy(userId);
                    updateOrder.setCreatedAt(new Date());
                    updateOrderRepository.save(updateOrder);
                    updateOrderEntityList.add(updateOrder);
                }
            } catch (Exception e) {
                UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                updateOrder.setUuid(uuid);
                updateOrder.setStatus("fail");
                updateOrder.setState("Shipping");
                updateOrder.setCurrent(current);
                updateOrder.setTotal(total);
                updateOrder.setErrorMessage(e.getMessage());
                updateOrder.setParcelCode(orderRequest.getParcelCode());
                updateOrder.setRecipientName(orderRequest.getRecipientName());
                updateOrder.setCreatedBy(userId);
                updateOrder.setCreatedAt(new Date());
                updateOrderRepository.save(updateOrder);
                updateOrderEntityList.add(updateOrder);
            }
        }

        log.info("updateOrderEntityList size : {}", updateOrderEntityList.size());
    }

    public void converterPdfToExcel(MultipartFile file, String userId, String uuid) throws IOException {
        try {
            log.info("Start converterPdfToExcel uuid : {}", uuid);
            PdfReader pdfReader = new PdfReader(file.getInputStream());
            int pages = pdfReader.getNumberOfPages();
            FileWriter csvWriter = new FileWriter(pdfFilePath+"/cod-kerry.csv");
            for (int i = 1; i <= pages; i++) {
                String content = PdfTextExtractor.getTextFromPage(pdfReader,i);
                String[] splitContents = content.split("\n");
                boolean isTitle = true;
                for (int j = 0; j < splitContents.length; j++) {
                    if (isTitle) {
                        isTitle = false;
                        continue;
                    }
                    csvWriter.append(splitContents[j].replaceAll(" ", ","));
                    csvWriter.append("\n");
                }
            }
            csvWriter.flush();
            csvWriter.close();

            UpdateOrderEntity updateOrder = new UpdateOrderEntity();
            updateOrder.setUuid(uuid);
            updateOrder.setStatus("start");
            updateOrder.setState("Success");
            updateOrder.setCurrent(0);
            updateOrder.setTotal(1);
            updateOrder.setErrorMessage("เริ่มการอัพเดทข้อมูล");
            updateOrder.setCreatedBy(userId);
            updateOrder.setCreatedAt(new Date());
            updateOrderRepository.save(updateOrder);
            log.info("End converterPdfToExcel uuid : {}", uuid);
        } catch (Exception e) {
            log.error("=== Exception ===");
            log.error(e.getMessage(),e);
            UpdateOrderEntity updateOrder = new UpdateOrderEntity();
            updateOrder.setUuid(uuid);
            updateOrder.setStatus("fail");
            updateOrder.setState("Success");
            updateOrder.setCurrent(0);
            updateOrder.setTotal(0);
            updateOrder.setErrorMessage("ไฟล์เกิดข้อผิดพลาด");
            updateOrder.setCreatedBy(userId);
            updateOrder.setCreatedAt(new Date());
            updateOrderRepository.save(updateOrder);
        }
    }

    private List<OrderRequest> getRecordCsv() throws Exception {
        List<OrderRequest> orderRequestList = new ArrayList<>();
        List<List<String>> records = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(pdfFilePath+"/cod-kerry.csv"));) {
            while (scanner.hasNextLine()) {
                records.add(getRecordFromLine(scanner.nextLine()));
            }
        }
        log.info("records : {}", records);
        if(!CollectionUtils.isEmpty(records)){
            for(List<String> record:records){
                if(CollectionUtils.isEmpty(record)){
                    continue;
                }
                int index = 0;
                OrderRequest orderRequest = new OrderRequest();
                for(String text:record){
                    log.info("text : {}", text);
                    if(text.contains("KEX")){
                        log.info("code : {}", text);
                        orderRequest = new OrderRequest();
                        orderRequest.setParcelCode(text);
                        orderRequest.setRecipientName(record.get(index+3));
                        orderRequestList.add(orderRequest);
                    }
                    index++;
                }
            }
        }
        return orderRequestList;
    }

    private List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }

    @Async("taskExecutor")
    public void uploadFileUpdateSuccess(MultipartFile file, String transportationService, String userId, String uuid) throws IOException {
        try {
            List<OrderRequest> orderRequestList = new ArrayList<>();
            if("kerry".equalsIgnoreCase(transportationService)){
                orderRequestList = getRecordCsv();
            }else if("flash".equalsIgnoreCase(transportationService)){
                orderRequestList = excelHelperService.excelToMapSuccess(file.getInputStream(),transportationService,"Order Template");
            }
            log.info("orderRequestList : {}", orderRequestList.size());
            updateSuccess(orderRequestList,userId,uuid);
        }catch (Exception e){
            log.error(e.getMessage(),e);
            UpdateOrderEntity updateOrder = new UpdateOrderEntity();
            updateOrder.setUuid(uuid);
            updateOrder.setStatus("fail");
            updateOrder.setState("Success");
            updateOrder.setCurrent(0);
            updateOrder.setTotal(0);
            updateOrder.setErrorMessage("ไฟล์เกิดข้อผิดพลาด");
            updateOrder.setCreatedBy(userId);
            updateOrder.setCreatedAt(new Date());
            updateOrderRepository.save(updateOrder);
        }
    }

    public void updateSuccess(List<OrderRequest> orderRequestList, String userId, String uuid) {
        List<UpdateOrderEntity> updateOrderEntityList = new ArrayList<>();
        if (CollectionUtils.isEmpty(orderRequestList)) {
            UpdateOrderEntity updateOrder = new UpdateOrderEntity();
            updateOrder.setUuid(uuid);
            updateOrder.setStatus("fail");
            updateOrder.setState("Success");
            updateOrder.setCurrent(0);
            updateOrder.setTotal(0);
            updateOrder.setErrorMessage("ไฟล์เกิดข้อผิดพลาด");
            updateOrder.setCreatedBy(userId);
            updateOrder.setCreatedAt(new Date());
            updateOrderRepository.save(updateOrder);
            updateOrderEntityList.add(updateOrder);

            log.info("fail size : {}", updateOrderEntityList.size());

            return;
        }
        updateOrderRepository.deleteByState("Success");
        int current = 0;
        int total = orderRequestList.size();
        for (OrderRequest orderRequest : orderRequestList) {
            log.info("current : {}, total : {}", current++,total);
            try {
                Optional<OrderEntity> optional = orderRepository.findByParcelCode(orderRequest.getParcelCode().trim());
                if (optional.isPresent()) {
                    OrderEntity orderEntity = optional.get();
                    orderEntity.setStatus("Success");
                    orderEntity.setUpdatedBy(userId);
                    orderEntity.setUpdatedAt(new Date());
                    orderEntity.setDepositDate(new Date());
                    orderRepository.save(orderEntity);

                    UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                    updateOrder.setUuid(uuid);
                    updateOrder.setStatus("success");
                    updateOrder.setState("Success");
                    updateOrder.setCurrent(current);
                    updateOrder.setTotal(total);
                    updateOrder.setTotalAmount(orderEntity.getTotalAmount());
                    updateOrder.setParcelCode(orderRequest.getParcelCode());
                    updateOrder.setRecipientName(orderEntity.getRecipientName());
                    updateOrder.setCreatedBy(userId);
                    updateOrder.setCreatedAt(new Date());
                    updateOrderRepository.save(updateOrder);
                    updateOrderEntityList.add(updateOrder);
                } else {
                    UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                    updateOrder.setUuid(uuid);
                    updateOrder.setStatus("fail");
                    updateOrder.setState("Success");
                    updateOrder.setCurrent(current);
                    updateOrder.setTotal(total);
                    updateOrder.setErrorMessage("ไม่มีรหัสพัสดุนี้ในระบบ");
                    updateOrder.setParcelCode(orderRequest.getParcelCode());
                    updateOrder.setRecipientName(orderRequest.getRecipientName());
                    updateOrder.setCreatedBy(userId);
                    updateOrder.setCreatedAt(new Date());
                    updateOrderRepository.save(updateOrder);
                    updateOrderEntityList.add(updateOrder);
                }
            } catch (Exception e) {
                UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                updateOrder.setUuid(uuid);
                updateOrder.setStatus("fail");
                updateOrder.setState("Success");
                updateOrder.setCurrent(current);
                updateOrder.setTotal(total);
                updateOrder.setErrorMessage(e.getMessage());
                updateOrder.setParcelCode(orderRequest.getParcelCode());
                updateOrder.setRecipientName(orderRequest.getRecipientName());
                updateOrder.setCreatedBy(userId);
                updateOrder.setCreatedAt(new Date());
                updateOrderRepository.save(updateOrder);
                updateOrderEntityList.add(updateOrder);
            }
        }

        log.info("updateOrderEntityList size : {}", updateOrderEntityList.size());
    }

    @Async("taskExecutor")
    public void uploadFileUpdateCancel(MultipartFile file, String transportationService, String userId) throws IOException {
        List<OrderRequest> orderRequestList = excelHelperService.excelToMap(file.getInputStream(),transportationService,"Order Template");
        log.info("orderRequestList : {}", orderRequestList.size());
        updateCancel(orderRequestList,userId);
    }

    public void updateCancel(List<OrderRequest> orderRequestList, String userId) {
        List<UpdateOrderEntity> updateOrderEntityList = new ArrayList<>();
        if (CollectionUtils.isEmpty(orderRequestList)) {
            UpdateOrderEntity updateOrder = new UpdateOrderEntity();
            updateOrder.setStatus("fail");
            updateOrder.setState("Cancel");
            updateOrder.setErrorMessage("ไฟล์เกิดข้อผิดพลาด");
            updateOrder.setCreatedBy(userId);
            updateOrder.setCreatedAt(new Date());
            updateOrderRepository.save(updateOrder);
            updateOrderEntityList.add(updateOrder);

            log.info("fail size : {}", updateOrderEntityList.size());

            return;
        }
        updateOrderRepository.deleteByState("Cancel");
        for (OrderRequest orderRequest : orderRequestList) {
            try {
                Optional<OrderEntity> optional = orderRepository.findByParcelCode(orderRequest.getParcelCode().trim());
                if (optional.isPresent()) {
                    OrderEntity orderEntity = optional.get();
                    orderEntity.setStatus("Cancel");
                    orderEntity.setUpdatedBy(userId);
                    orderEntity.setUpdatedAt(new Date());
                    orderRepository.save(orderEntity);

                    UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                    updateOrder.setStatus("success");
                    updateOrder.setState("Cancel");
                    updateOrder.setTotalAmount(orderEntity.getTotalAmount());
                    updateOrder.setParcelCode(orderRequest.getParcelCode());
                    updateOrder.setRecipientName(orderRequest.getRecipientName());
                    updateOrder.setCreatedBy(userId);
                    updateOrder.setCreatedAt(new Date());
                    updateOrderRepository.save(updateOrder);
                    updateOrderEntityList.add(updateOrder);
                } else {
                    UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                    updateOrder.setStatus("fail");
                    updateOrder.setState("Cancel");
                    updateOrder.setErrorMessage("ไม่มีรหัสพัสดุนี้ในระบบ");
                    updateOrder.setParcelCode(orderRequest.getParcelCode());
                    updateOrder.setRecipientName(orderRequest.getRecipientName());
                    updateOrder.setCreatedBy(userId);
                    updateOrder.setCreatedAt(new Date());
                    updateOrderRepository.save(updateOrder);
                    updateOrderEntityList.add(updateOrder);
                }
            } catch (Exception e) {
                UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                updateOrder.setStatus("fail");
                updateOrder.setState("Cancel");
                updateOrder.setErrorMessage(e.getMessage());
                updateOrder.setParcelCode(orderRequest.getParcelCode());
                updateOrder.setRecipientName(orderRequest.getRecipientName());
                updateOrder.setCreatedBy(userId);
                updateOrder.setCreatedAt(new Date());
                updateOrderRepository.save(updateOrder);
                updateOrderEntityList.add(updateOrder);
            }
        }

        log.info("updateOrderEntityList size : {}", updateOrderEntityList.size());
    }

}
