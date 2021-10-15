package th.co.infinitait.goodluck.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import th.co.infinitait.goodluck.exception.NotFoundException;
import th.co.infinitait.goodluck.model.response.OrderResponse;
import th.co.infinitait.goodluck.service.ExcelHelperService;
import th.co.infinitait.goodluck.service.ExcelService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UploadExcelController {

    private final ExcelService fileService;
    private final ExcelHelperService excelHelperService;

    @PostMapping(value = "/excel/upload/update/parcel-code")
    public ResponseEntity<List<OrderResponse>> uploadFileUpdateParcelCode(@RequestParam("file") MultipartFile file,
                                                                          @RequestParam("sheet_name") String sheetName) {
        String message = "";
        if (excelHelperService.hasExcelFormat(file)) {
            try {
                return ResponseEntity.ok(fileService.uploadFileUpdateParcelCode(file,sheetName));
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                throw new NotFoundException(e.getMessage());
            }
        }
        message = "Please upload an excel file!";
        throw new NotFoundException(message);
    }

    @PostMapping(value = "/excel/upload/update/success")
    public ResponseEntity<List<OrderResponse>> uploadFileUpdateSuccess(@RequestParam("file") MultipartFile file,
                                                                          @RequestParam("sheet_name") String sheetName) {
        String message = "";
        if (excelHelperService.hasExcelFormat(file)) {
            try {
                return ResponseEntity.ok(fileService.uploadFileUpdateSuccess(file,sheetName));
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                throw new NotFoundException(e.getMessage());
            }
        }
        message = "Please upload an excel file!";
        throw new NotFoundException(message);
    }

    @PostMapping(value = "/excel/upload/update/cancel")
    public ResponseEntity<List<OrderResponse>> uploadFileUpdateCancel(@RequestParam("file") MultipartFile file,
                                                                       @RequestParam("sheet_name") String sheetName) {
        String message = "";
        if (excelHelperService.hasExcelFormat(file)) {
            try {
                return ResponseEntity.ok(fileService.uploadFileUpdateSuccess(file,sheetName));
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                throw new NotFoundException(e.getMessage());
            }
        }
        message = "Please upload an excel file!";
        throw new NotFoundException(message);
    }

}
