package th.co.infinitait.goodluck.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TB_UPDATE_ORDER")
public class UpdateOrderEntity {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "status")
    private String status;

    @Column(name = "state")
    private String state;

    @Column(name = "current")
    private Integer current;

    @Column(name = "total")
    private Integer total;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "parcel_code")
    private String parcelCode;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;
}
