package th.co.infinitait.goodluck.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EquipmentProductItemStatus {
    STATUS_INACTIVE("เบิกตัดสค๊อคทันที่ (ชำรุด)"),
    STATUS_ACTIVE("ปกติ"),
    STATUS_HOLD("(จอง) ถ้าใบงานเสร็จจะขาขขาด"),
    STATUS_LEND("ยืม"),
    STATUS_RESERVE("สำรองช่าง"),
    STATUS_SELL("ขายขาด"),
    STATUS_REPAIR("ซ่อม");

    private final String description;

}
