package th.co.infinitait.goodluck.controller;

import com.groupdocs.conversion.Converter;
import com.groupdocs.conversion.options.convert.SpreadsheetConvertOptions;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.cos.COSDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import th.co.infinitait.goodluck.model.request.OrderRequest;
import th.co.infinitait.goodluck.model.request.ReportRequest;
import th.co.infinitait.goodluck.model.request.TransportRequest;
import th.co.infinitait.goodluck.model.response.GenTransportResponse;
import th.co.infinitait.goodluck.model.response.OrderResponse;
import th.co.infinitait.goodluck.model.response.ReportResponse;
import th.co.infinitait.goodluck.service.JasperReportsService;

import javax.validation.Valid;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        String exportPath = reportService.createOrderReceiptReport("receipt_v1",reportRequest.getOrderIdList(),reportRequest.getCompanyId());
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

    @PostMapping(value = "/transport/excel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ReportResponse> createTransportExcel(@Valid @RequestBody TransportRequest request) throws Exception {
        log.info("request : {}", request);
        String uuid = reportService.startTransportExcel(request);
        reportService.createTransportExcel(uuid,request);
        return ResponseEntity.ok(ReportResponse.builder().uuid(uuid).status("SUCCESS").build());
    }

    @GetMapping(value = "/transport/status/{uuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenTransportResponse> getTransportStatus(@PathVariable String uuid) throws Exception {
        return ResponseEntity.ok(reportService.getTransportStatus(uuid));
    }

    @GetMapping(value = "/transport/exportExcel/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> loadTransportExcel(@PathVariable String name) throws IOException {
        String fileName = "transport_order_"+name+".xlsx";
        File file = new File("report/excel/transport/"+fileName);

        byte[] fileContent = FileUtils.readFileToByteArray(file);
        return ResponseEntity
                .ok()
                // Specify content type as PDF
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                // Tell browser to display PDF if it can
                .header("Content-Disposition", "inline; filename=\""+fileName+"\"")
                .body(fileContent);
    }

    @GetMapping(value = "/convert-pdf-to-excel", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GenTransportResponse> convertPdfToExcel() throws Exception {
//        Converter converter = new Converter("report/pdf/cod-kerry.pdf");
//        SpreadsheetConvertOptions options = new SpreadsheetConvertOptions();
//        converter.convert("report/excel/cod-kerry.xlsx", options);

//        File f = new File("report/pdf/cod-kerry.pdf");
//        String parsedText;
//        PDFParser parser = new PDFParser(new RandomAccessFile(f, "r"));
//        parser.parse();
//
//        COSDocument cosDoc = parser.getDocument();
//        PDFTextStripper pdfStripper = new PDFTextStripper();
//        PDDocument pdDoc = new PDDocument(cosDoc);
//        parsedText = pdfStripper.getText(pdDoc);
//
//        PrintWriter pw = new PrintWriter("report/excel/pdf.txt");
//        pw.print(parsedText);
//        pw.close();

        PdfReader pdfReader = new PdfReader("report/pdf/cod-kerry.pdf");

        int pages = pdfReader.getNumberOfPages();

        FileWriter csvWriter = new FileWriter("report/excel/cod-kerry.csv");

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

        List<List<String>> records = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("report/excel/cod-kerry.csv"));) {
            while (scanner.hasNextLine()) {
                records.add(getRecordFromLine(scanner.nextLine()));
            }
        }
        log.info("records : {}", records);
        List<OrderRequest> orderRequestList = new ArrayList<>();
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
        log.info("orderRequestList : {}", orderRequestList);
        return ResponseEntity.ok(GenTransportResponse.builder().build());
    }

    private List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(",");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }
}
