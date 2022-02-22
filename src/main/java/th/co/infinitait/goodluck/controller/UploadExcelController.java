package th.co.infinitait.goodluck.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import th.co.infinitait.goodluck.component.CabsatPayload;
import th.co.infinitait.goodluck.entity.UpdateOrderEntity;
import th.co.infinitait.goodluck.exception.NotFoundException;
import th.co.infinitait.goodluck.model.request.OrderRequest;
import th.co.infinitait.goodluck.model.response.OrderResponse;
import th.co.infinitait.goodluck.model.response.ReportResponse;
import th.co.infinitait.goodluck.repository.UpdateOrderRepository;
import th.co.infinitait.goodluck.service.ExcelHelperService;
import th.co.infinitait.goodluck.service.ExcelService;
import th.co.infinitait.goodluck.service.UpdateOrderService;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UploadExcelController {

    private final ExcelService fileService;
    private final UpdateOrderService updateOrderService;
    private final CabsatPayload cabsatPayload;
    private final UpdateOrderRepository updateOrderRepository;
    private final ExcelHelperService excelHelperService;

    @GetMapping(value = "/update-order/uuid/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponse>> getUpdateOrder(@PathVariable String uuid) throws Exception {
        log.info("uuid : {}", uuid);
        return ResponseEntity.ok(updateOrderService.getUpdateOrder(uuid));
    }

    @DeleteMapping(value = "/update-order/state/{state}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> deleteUpdateOrder(@PathVariable String state) throws Exception {
        log.info("deleteUpdateOrder : {}", state);
        updateOrderService.deleteUpdateOrder(state);
        return ResponseEntity.ok(OrderResponse.builder().build());
    }

    @PostMapping(value = "/excel/upload/update/parcel-code")
    public ResponseEntity<ReportResponse> uploadFileUpdateParcelCode(@RequestParam("file") MultipartFile file,
                                                                    @RequestParam("transportationService") String transportationService
                                                                    ) {
        log.info("uploadFileUpdateParcelCode transportationService : {}", transportationService);
        String message = "";
        if (excelHelperService.hasExcelFormat(file)) {
            try {
                String uuid = RandomStringUtils.randomAlphanumeric(64);
                log.info("uploadFileUpdateParcelCode uuid : {}", uuid);
                fileService.createUpdateOrder(cabsatPayload.getUserId(),uuid);
                fileService.uploadFileUpdateParcelCode(file,transportationService,cabsatPayload.getUserId(),uuid);
                return ResponseEntity.ok(ReportResponse.builder().uuid(uuid).status("SUCCESS").build());
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                throw new NotFoundException(e.getMessage());
            }
        }
        message = "Please upload an excel file!!";
        throw new NotFoundException(message);
    }

    @PostMapping(value = "/excel/upload/update/success")
    public ResponseEntity<ReportResponse> uploadFileUpdateSuccess(@RequestParam("file") MultipartFile file,
                                                                 @RequestParam("transportationService") String transportationService) {
        log.info("uploadFileUpdateSuccess transportationService : {}", transportationService);
        try {
            String uuid = RandomStringUtils.randomAlphanumeric(64);
            log.info("uploadFileUpdateParcelCode uuid : {}", uuid);

            UpdateOrderEntity updateOrder = new UpdateOrderEntity();
            updateOrder.setUuid(uuid);
            updateOrder.setStatus("start");
            updateOrder.setState("Success");
            updateOrder.setCurrent(0);
            updateOrder.setTotal(1);
            updateOrder.setErrorMessage("เริ่มการอัพเดทข้อมูล");
            updateOrder.setCreatedBy(cabsatPayload.getUserId());
            updateOrder.setCreatedAt(new Date());
            updateOrderRepository.save(updateOrder);

            if("kerry".equalsIgnoreCase(transportationService)){
                fileService.converterPdfToExcel(file,cabsatPayload.getUserId(),uuid);
            }

            fileService.uploadFileUpdateSuccess(file,transportationService,cabsatPayload.getUserId(),uuid);
            return ResponseEntity.ok(ReportResponse.builder().uuid(uuid).status("SUCCESS").build());
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            throw new NotFoundException(e.getMessage());
        }
    }

    @PostMapping(value = "/excel/upload/update/cancel")
    public ResponseEntity<OrderResponse> uploadFileUpdateCancel(@RequestParam("file") MultipartFile file,
                                                                @RequestParam("transportationService") String transportationService) {
        String message = "";
        if (excelHelperService.hasExcelFormat(file)) {
            try {
                fileService.uploadFileUpdateCancel(file,transportationService,cabsatPayload.getUserId());
                return ResponseEntity.ok(OrderResponse.builder().build());
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                throw new NotFoundException(e.getMessage());
            }
        }
        message = "Please upload an excel file!";
        throw new NotFoundException(message);
    }


}
