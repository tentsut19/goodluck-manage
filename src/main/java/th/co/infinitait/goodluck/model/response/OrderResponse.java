package th.co.infinitait.goodluck.model.response;

import lombok.*;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderResponse {
    private Long id;
    private String uuid;
    private String status;
    private String state;
    private Integer current;
    private Integer total;
    private String errorMessage;
    private String parcelCode;
    private String recipientName;
    private BigDecimal totalAmount;
    private Date deletedAt;
    private Date createdAt;
    private String createdBy;
    private Date updatedAt;
    private String updatedBy;
}
