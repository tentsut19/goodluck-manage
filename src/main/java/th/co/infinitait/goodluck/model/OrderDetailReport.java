package th.co.infinitait.goodluck.model;

import lombok.Data;

@Data
public class OrderDetailReport {
    private String no; // ลำกับ
    private String description; // รายละเอียด
    private String quantity; // จำนวน
    private String unitPrice; // ราคาต่อหน่วย
    private String amount; // จำนวนเงิน
}
