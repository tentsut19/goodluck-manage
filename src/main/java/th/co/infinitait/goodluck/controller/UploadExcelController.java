package th.co.infinitait.goodluck.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import th.co.infinitait.goodluck.component.CabsatPayload;
import th.co.infinitait.goodluck.exception.NotFoundException;
import th.co.infinitait.goodluck.model.request.OrderRequest;
import th.co.infinitait.goodluck.model.response.OrderResponse;
import th.co.infinitait.goodluck.service.ExcelHelperService;
import th.co.infinitait.goodluck.service.ExcelService;
import th.co.infinitait.goodluck.service.UpdateOrderService;

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
    private final ExcelHelperService excelHelperService;

    @GetMapping(value = "/update-order/state/{state}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<OrderResponse>> getUpdateOrder(@PathVariable String state) throws Exception {
        log.info("getUpdateOrder : {}", state);
        return ResponseEntity.ok(updateOrderService.getUpdateOrder(state));
    }

    @DeleteMapping(value = "/update-order/state/{state}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderResponse> deleteUpdateOrder(@PathVariable String state) throws Exception {
        log.info("deleteUpdateOrder : {}", state);
        updateOrderService.deleteUpdateOrder(state);
        return ResponseEntity.ok(OrderResponse.builder().build());
    }

    @PostMapping(value = "/excel/upload/update/parcel-code")
    public ResponseEntity<OrderResponse> uploadFileUpdateParcelCode(@RequestParam("file") MultipartFile file,
                                                                    @RequestParam("transportationService") String transportationService
                                                                    ) {
        String message = "";
        if (excelHelperService.hasExcelFormat(file)) {
            try {
                fileService.uploadFileUpdateParcelCode(file,transportationService,cabsatPayload.getUserId());
                return ResponseEntity.ok(OrderResponse.builder().build());
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                throw new NotFoundException(e.getMessage());
            }
        }
        message = "Please upload an excel file!";
        throw new NotFoundException(message);
    }

    @PostMapping(value = "/excel/upload/update/success")
    public ResponseEntity<OrderResponse> uploadFileUpdateSuccess(@RequestParam("file") MultipartFile file,
                                                                 @RequestParam("transportationService") String transportationService) {
        String message = "";
        if (excelHelperService.hasExcelFormat(file)) {
            try {
                fileService.uploadFileUpdateSuccess(file,transportationService,cabsatPayload.getUserId());
                return ResponseEntity.ok(OrderResponse.builder().build());
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                throw new NotFoundException(e.getMessage());
            }
        }
        message = "Please upload an excel file!";
        throw new NotFoundException(message);
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
