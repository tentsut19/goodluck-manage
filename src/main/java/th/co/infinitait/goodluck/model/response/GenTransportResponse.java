package th.co.infinitait.goodluck.model.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GenTransportResponse {
    private String id;
    private String status;
    private String state;
    private String errorMessage;
    private Date createdAt;
    private String createdBy;
    private Date updatedAt;
    private String updatedBy;
}
