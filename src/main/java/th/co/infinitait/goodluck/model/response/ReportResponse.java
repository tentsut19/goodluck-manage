package th.co.infinitait.goodluck.model.response;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReportResponse {
    private String status;
    private String exportPath;
    private byte[] desc;
}
