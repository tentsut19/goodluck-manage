package th.co.infinitait.goodluck.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationLineResponse {
    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;
}
