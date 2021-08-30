package th.co.infinitait.goodluck.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ServiceApplicationStatus {
    D("แบบร่าง"),
    H("รอมอบหมายงาน"),
    A("ใช้งานปกติ"),
    I("ยกเลิกการใช้บริการ"),
    W("ระหว่างการติดตั้ง");

    private final String description;
}
