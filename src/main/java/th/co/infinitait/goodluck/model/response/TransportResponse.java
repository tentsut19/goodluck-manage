package th.co.infinitait.goodluck.model.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransportResponse {
    @Builder.Default
    private Integer total = 0;
    private Integer no;
    private String consigneeName;
    private String address1;
    private String address2;
    private String postalCode;
    private String phoneNumber;
    private String email;
    private String product;
    private BigDecimal cod;
    private float weightKg;
    private float lengthCm;
    private float widthCm;
    private float heightCm;
    @Builder.Default
    private String productType = "Standard";
}
