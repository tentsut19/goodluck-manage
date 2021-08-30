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
@Table(name = "TB_ORDER")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "parcel_code")
    private String parcelCode;

    @Column(name = "status")
    private String status;

    @Column(name = "payment_channel")
    private String paymentChannel;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "transportation_service")
    private String transportationService;

    @Column(name = "invoice_code")
    private String invoiceCode;

    @Column(name = "total_amount")
    private BigDecimal totalAmount;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "date_of_appointment")
    private Date dateOfAppointment;

    @Column(name = "order_date")
    private Date orderDate;

    @Column(name = "product_draft_name")
    private String productDraftName;

    @Column(name = "line_user_id")
    private String lineUserId;

    @Column(name = "DELETED_AT")
    private Date deletedAt;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "UPDATED_AT")
    private Date updatedAt;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @ManyToOne
    @JoinColumn(name="company_id")
    private CompanyEntity company;

    @ManyToOne
    @JoinColumn(name="customer_id")
    private CustomerEntity customer;
}
