package th.co.infinitait.goodluck.model.response;

import lombok.*;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderResponse {
    private Long id;
    private String status;
    private String errorMessage;
    private String parcelCode;
    private String recipientName;
    private Date deletedAt;
    private Date createdAt;
    private String createdBy;
    private Date updatedAt;
    private String updatedBy;
}
