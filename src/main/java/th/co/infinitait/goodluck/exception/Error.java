package th.co.infinitait.goodluck.exception;

import lombok.Data;
import java.util.List;

@Data
public class Error  {
    private List<ErrorDetail> errors;
}
