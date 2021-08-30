package th.co.infinitait.goodluck.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor
@Getter
public enum RequisitionDocumentFlow {
    D("D","ใบขอเบิก","แบบร่าง"),
    C("C","ใบขอเบิก","ยกเลิก"),
    WA("WA","ใบขอเบิก","รออนุมัติ"),
    RJ("RJ","ใบขอเบิก","ไม่อนุมัติ"),
    WC("WC","ใบขอเบิก","รอตรวจสอบ"),
    WP("WP","ใบขอเบิก","รอสินค้า"),
    F("F","ใบเบิก","เสร็จสิ้น");

    private final String code;
    private final String state;
    private final String status;
}
