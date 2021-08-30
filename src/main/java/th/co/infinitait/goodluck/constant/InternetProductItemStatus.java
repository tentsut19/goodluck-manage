package th.co.infinitait.goodluck.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InternetProductItemStatus {
    USE("ใช้งาน"),
    UN_USE("ไม่ใช้งาน"),
    RESERVATION("จอง");

    private final String description;

}
