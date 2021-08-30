package th.co.infinitait.goodluck.component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@Scope(
        value = "request",
        proxyMode = ScopedProxyMode.TARGET_CLASS
)
public class CabsatPayload {

    private String userId;
    private String companyId;
    private String accessToken;
    private String language;

    public HttpHeaders generateHeader() {
        HttpHeaders requestHeader = new HttpHeaders();
        requestHeader.set("user_id", this.userId);
        requestHeader.set("access_token", this.accessToken);
        requestHeader.set("company_id", this.companyId);
        requestHeader.set("language", this.language);
        requestHeader.setContentType(MediaType.APPLICATION_JSON);
        return requestHeader;
    }
}
