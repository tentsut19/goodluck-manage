package th.co.infinitait.goodluck.model.request;

import lombok.*;

import java.util.List;

@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReportRequest {
    private List<Long> orderIdList;
}
