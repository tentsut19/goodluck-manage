package th.co.infinitait.goodluck.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor
@Getter
public enum WorkSheetStatus {
    W("W","รอมอบหมายงาน"),
    P("P","อยู่ระหว่างดำเนินงาน"),
    O("O","งานคงค้าง"),
    F("F","เสร็จสมบูรณ์"),
    C("C","งานยกเลิก");

    private final String code;
    private final String name;
}
