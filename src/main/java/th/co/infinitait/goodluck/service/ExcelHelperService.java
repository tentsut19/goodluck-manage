package th.co.infinitait.goodluck.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import th.co.infinitait.goodluck.exception.InvalidParameterException;
import th.co.infinitait.goodluck.model.request.OrderRequest;
import th.co.infinitait.goodluck.util.DoubleUtil;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExcelHelperService {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public boolean hasExcelFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public List<OrderRequest> excelToMap(InputStream is, String transportationService, String sheetName) throws IOException {
        try {
            Workbook workbook = new XSSFWorkbook(is);

//            Sheet sheet = workbook.getSheet(sheetName);
            Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Iterator<Row> rows = null;
            try {
                rows = sheet.iterator();
            }catch (Exception e){
                throw new InvalidParameterException("sheet name : "+sheetName+" ไม่มีในไฟล์");
            }

            List<OrderRequest> orderRequestList = new ArrayList<>();

            boolean isHeader = true;
            int countHeader = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                if("kerry".equalsIgnoreCase(transportationService)) {
                    if(countHeader < 5){
                        countHeader++;
                        continue;
                    }
                }else{
                    // skip header
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }
                }

                OrderRequest orderRequest = new OrderRequest();

                CellReference cr = new CellReference("A"); // รหัสพัสดุ
                if("kerry".equalsIgnoreCase(transportationService)){
                    cr = new CellReference("J"); // รหัสพัสดุ
                }else if("flash".equalsIgnoreCase(transportationService)){
                    cr = new CellReference("B"); // รหัสพัสดุ
                }
                Cell cell = currentRow.getCell(cr.getCol());
                CellValue cellValue = evaluator.evaluate(cell);
                String parcelCode = getValueString(cellValue);
                orderRequest.setParcelCode(parcelCode);

                if(StringUtils.isEmpty(parcelCode)){
                    continue;
                }

                cr = new CellReference("C"); // ชื่อผู้รับ
                if("kerry".equalsIgnoreCase(transportationService)){
                    cr = new CellReference("C"); // รหัสพัสดุ
                }else if("flash".equalsIgnoreCase(transportationService)){
                    cr = new CellReference("C"); // รหัสพัสดุ
                }
                cell = currentRow.getCell(cr.getCol());
                cellValue = evaluator.evaluate(cell);
                String recipientName = getValueString(cellValue);
                orderRequest.setRecipientName(recipientName);

                orderRequestList.add(orderRequest);
            }

            workbook.close();
            return orderRequestList;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<OrderRequest> excelToMapParcelCode(InputStream is, String transportationService, String sheetName) throws IOException {
        try {
            Workbook workbook = new XSSFWorkbook(is);

//            Sheet sheet = workbook.getSheet(sheetName);
            Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Iterator<Row> rows = null;
            try {
                rows = sheet.iterator();
            }catch (Exception e){
                throw new InvalidParameterException("sheet name : "+sheetName+" ไม่มีในไฟล์");
            }

            List<OrderRequest> orderRequestList = new ArrayList<>();

            boolean isHeader = true;
            CellReference crParcelCode = null;
            CellReference crRecipientName = null;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                OrderRequest orderRequest = new OrderRequest();

                if("kerry".equalsIgnoreCase(transportationService)){
                    if(isHeader) {
                        isHeader = false;
                        CellReference crHeader = new CellReference("A");// check header
                        Cell cellHeader = currentRow.getCell(crHeader.getCol());
                        CellValue cellValueHeader = evaluator.evaluate(cellHeader);
                        String cellA = getValueString(cellValueHeader);
                        CellReference crBHeader = new CellReference("B");// check header
                        Cell cellBHeader = currentRow.getCell(crBHeader.getCol());
                        CellValue cellBValueHeader = evaluator.evaluate(cellBHeader);
                        String cellB = getValueString(cellBValueHeader);
                        if (cellA.contains("consignment")) {
                            crParcelCode = new CellReference("A"); // รหัสพัสดุ
                            crRecipientName = new CellReference("B"); // ผู้ส่ง
                            continue;
                        } else {
                            if(cellA.contains("KEX")) {
                                crParcelCode = new CellReference("A"); // รหัสพัสดุ
                                crRecipientName = new CellReference("C"); // ผู้ส่ง
                            }else if(cellB.contains("KEX")) {
                                crParcelCode = new CellReference("B"); // รหัสพัสดุ
                                crRecipientName = new CellReference("E"); // ผู้ส่ง
                            }else{
                                crParcelCode = new CellReference("B"); // รหัสพัสดุ
                                crRecipientName = new CellReference("E"); // ผู้ส่ง
                            }
                        }
                    }
                }else if("flash".equalsIgnoreCase(transportationService)){
                    if(isHeader){
                        isHeader = false;
                        continue;
                    }
                    crParcelCode = new CellReference("B"); // รหัสพัสดุ
                    crRecipientName = new CellReference("C"); // ชื่อผู้รับ
                }
                Cell cell = currentRow.getCell(crParcelCode.getCol());
                CellValue cellValue = evaluator.evaluate(cell);
                String parcelCode = getValueString(cellValue);
                orderRequest.setParcelCode(parcelCode);

                if(StringUtils.isEmpty(parcelCode)){
                    continue;
                }

                cell = currentRow.getCell(crRecipientName.getCol());
                cellValue = evaluator.evaluate(cell);
                String recipientName = getValueString(cellValue);
                orderRequest.setRecipientName(recipientName);

                if(StringUtils.isEmpty(recipientName)){
                    continue;
                }

                orderRequestList.add(orderRequest);
            }

            workbook.close();
            return orderRequestList;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<OrderRequest> excelToMapSuccess(InputStream is, String transportationService, String sheetName) throws IOException {
        try {
            Workbook workbook = new XSSFWorkbook(is);

//            Sheet sheet = workbook.getSheet(sheetName);
            Sheet sheet = workbook.getSheetAt(0);
            if("kerry".equalsIgnoreCase(transportationService)){
                sheet = workbook.getSheetAt(0);
            }else if("flash".equalsIgnoreCase(transportationService)){
                sheet = workbook.getSheetAt(1);
            }
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Iterator<Row> rows = null;
            try {
                rows = sheet.iterator();
            }catch (Exception e){
                throw new InvalidParameterException("sheet name : "+sheetName+" ไม่มีในไฟล์");
            }

            List<OrderRequest> orderRequestList = new ArrayList<>();

            boolean isHeader = true;
            int countHeader = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                if("kerry".equalsIgnoreCase(transportationService)) {
                    if(countHeader < 5){
                        countHeader++;
                        continue;
                    }
                }else{
                    // skip header
                    if (isHeader) {
                        isHeader = false;
                        continue;
                    }
                }

                OrderRequest orderRequest = new OrderRequest();

                CellReference cr = new CellReference("C"); // รหัสพัสดุ
                if("kerry".equalsIgnoreCase(transportationService)){
                    cr = new CellReference("A"); // รหัสพัสดุ
                }else if("flash".equalsIgnoreCase(transportationService)){
                    cr = new CellReference("C"); // รหัสพัสดุ
                }
                Cell cell = currentRow.getCell(cr.getCol());
                CellValue cellValue = evaluator.evaluate(cell);
                String parcelCode = getValueString(cellValue);
                orderRequest.setParcelCode(parcelCode);

                if(StringUtils.isEmpty(parcelCode)){
                    continue;
                }

                cr = new CellReference("D"); // ชื่อผู้รับ
                if("kerry".equalsIgnoreCase(transportationService)){
                    cr = new CellReference("B"); // รหัสพัสดุ
                }else if("flash".equalsIgnoreCase(transportationService)){
                    cr = new CellReference("D"); // รหัสพัสดุ
                }
                cell = currentRow.getCell(cr.getCol());
                cellValue = evaluator.evaluate(cell);
                String recipientName = getValueString(cellValue);
                orderRequest.setRecipientName(recipientName);

                orderRequestList.add(orderRequest);
            }

            workbook.close();
            return orderRequestList;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String getValueString(CellValue cell){
        String result = "";
        if(cell == null){
            return result;
        }
        CellType type = cell.getCellType();
        switch (type) {
            case STRING:
                result = cell.getStringValue();
                break;
            case NUMERIC:
                result = DoubleUtil.toString1DecimalFormat(cell.getNumberValue());
                break;
            case BOOLEAN:
                result = cell.getBooleanValue()+"";
                break;
            case BLANK:
            case ERROR:
            case FORMULA:
            case _NONE:
                break;
        }
        return result;
    }

    public static BigDecimal getValueBigDecimal(CellValue cell){
        BigDecimal result = BigDecimal.ZERO;
        if(cell == null){
            return result;
        }
        CellType type = cell.getCellType();
        switch (type) {
            case STRING:
                try {
                    result = new BigDecimal(cell.getStringValue());
                    result = result.setScale(2, BigDecimal.ROUND_HALF_UP);
                }catch (Exception e){
                    result = new BigDecimal("0.00");
                }
                break;
            case NUMERIC:
                result = BigDecimal.valueOf(cell.getNumberValue());
                result = result.setScale(2, BigDecimal.ROUND_HALF_UP);
                break;
            case BOOLEAN:
            case BLANK:
            case ERROR:
            case FORMULA:
            case _NONE:
                break;
        }
        return result;
    }

    public static long getValueLong(CellValue cell){
        long result = 0L;
        if(cell == null){
            return result;
        }
        CellType type = cell.getCellType();
        switch (type) {
            case STRING:
                result = Long.parseLong(cell.getStringValue());
                break;
            case NUMERIC:
                result = (long) cell.getNumberValue();
                break;
            case BOOLEAN:
            case BLANK:
            case ERROR:
            case FORMULA:
            case _NONE:
                break;
        }
        return result;
    }
}