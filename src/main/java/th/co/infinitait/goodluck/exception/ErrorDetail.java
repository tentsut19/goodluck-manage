package th.co.infinitait.goodluck.exception;

import lombok.Data;

@Data
public class ErrorDetail  {
  private String code;
  private String message;
  private String severityLevel;
}
