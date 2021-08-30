package th.co.infinitait.goodluck.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AddressType {
    H("ที่อยู่ตามทะเบียนบ้าน"),
    C("ที่อยู่ปัจจุบัน"),
    I("ที่อยู่ติดตั้ง"),
    R("ที่อยู่สำหรับการจัดส่งใบแจ้งหนี้และใบเสร็จรับเงิน"),
    T("ที่อยู่สำหรับการจัดส่งใบกำกับภาษี");

    private final String description;
}
