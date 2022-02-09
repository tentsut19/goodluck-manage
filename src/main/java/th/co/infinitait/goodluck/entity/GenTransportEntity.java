package th.co.infinitait.goodluck.entity;

import lombok.*;
import th.co.infinitait.goodluck.model.response.EState;
import th.co.infinitait.goodluck.model.response.EStatus;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_gen_transport")
public class GenTransportEntity {

    @Id
    private String uuid;

    @Column(name = "file_name")
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EState state;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;
}
