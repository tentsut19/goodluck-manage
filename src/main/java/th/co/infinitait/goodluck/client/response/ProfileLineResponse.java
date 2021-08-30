package th.co.infinitait.goodluck.client.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileLineResponse {
    @JsonProperty("userId")
    private String userId;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("pictureUrl")
    private String pictureUrl;

    @JsonProperty("statusMessage")
    private String statusMessage;
}
