package th.co.infinitait.goodluck.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import th.co.infinitait.goodluck.model.request.ReportRequest;
import th.co.infinitait.goodluck.model.response.ReportResponse;
import th.co.infinitait.goodluck.service.JasperReportsService;

import javax.validation.Valid;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/report/")
@Slf4j
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReportController {

    private final JasperReportsService reportService;

    @Value("${report-generate-path}")
    private String pdfFilePath;

    @PostMapping(value = "/receipt", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportResponse> createOrderReceiptReport(@Valid @RequestBody ReportRequest reportRequest) throws Exception {
        log.info("reportRequest : {}", reportRequest);
        String exportPath = reportService.createOrderReceiptReport("receipt_v1",reportRequest.getOrderIdList());
        return ResponseEntity.ok(ReportResponse.builder().status("SUCCESS").exportPath(exportPath).build());
    }

    @GetMapping(value = "/receipt", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> downloadOrderReceiptReport() throws Exception {
//        String exportPath = pdfFilePath + "/order_receipt.pdf";
        String exportPath = "order_receipt.pdf";
        log.info("downloadCustomerServiceCallReport exportPath : {}", exportPath);
        File file = new File(exportPath);
        byte[] fileContent = FileUtils.readFileToByteArray(file);
        return ResponseEntity
                .ok()
                // Specify content type as PDF
                .header("Content-Type", "application/pdf; charset=UTF-8")
                // Tell browser to display PDF if it can
                .header("Content-Disposition", "inline; filename=\"receipt.pdf\"")
                .body(fileContent);
    }
}
