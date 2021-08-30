package th.co.infinitait.goodluck.component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class CabsatPayloadInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private CabsatPayload cimbPayload;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String accessTokenKey = request.getHeader("access_token");
        String userId = request.getHeader("user_id");
        String companyId = request.getHeader("company_id");
        String language = request.getHeader("language");
        this.cimbPayload.setUserId(userId);
        this.cimbPayload.setCompanyId(companyId);
        this.cimbPayload.setAccessToken(accessTokenKey);
        this.cimbPayload.setLanguage(language);
        MDC.put("UserId", userId);
        return true;
    }
}
