package th.co.infinitait.goodluck.constant;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@AllArgsConstructor
@Getter
public enum WithDraw {
    I("I","เบิกเพื่อติดตั้ง"),
    R("R","เบิกเพื่อซ่อม"),
    T("T","เบิกเพื่อทดสอบ"),
    S("S","เบิกเพื่อขาย"),
    B("B","เบิกเพื่อยืม"),
    SP("SP","เบิกเพื่อสำรอง");

    private final String code;
    private final String description;
}
