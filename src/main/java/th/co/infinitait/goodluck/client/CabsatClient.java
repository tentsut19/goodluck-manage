package th.co.infinitait.goodluck.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import th.co.infinitait.goodluck.model.request.OrderRequest;

import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
public class CabsatClient {

    @Autowired
    private RestTemplate restTemplate;

//    final String HOST_NAME = "http://ecommerce-uat.ap-southeast-1.elasticbeanstalk.com";
    final String HOST_NAME = "http://localhost:8091";
    final String CONVERT_PDF_TO_EXCEL = "/api/v1/convert/pdf-to-excel";

    // http://localhost:8091/api/v1/convert/pdf-to-excel

    public List<OrderRequest> converterPdfToExcel(MultipartFile file) {
        String endpoint = HOST_NAME + CONVERT_PDF_TO_EXCEL;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body
                = new LinkedMultiValueMap<>();
        body.add("files", file);

        HttpEntity<MultiValueMap<String, Object>> requestEntity
                = new HttpEntity<>(body, headers);

        ResponseEntity<List<OrderRequest>> result = restTemplate.exchange(
                endpoint, HttpMethod.POST, requestEntity,
                new ParameterizedTypeReference<List<OrderRequest>>() {
                });

        return result.getBody();
    }

    public List<OrderRequest> converterPdfToExcelV1(MultipartFile file) {
        String endpoint = HOST_NAME + CONVERT_PDF_TO_EXCEL;
        LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", file);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
        ResponseEntity<List<OrderRequest>> result = restTemplate.exchange(
                endpoint, HttpMethod.POST, requestEntity,
                new ParameterizedTypeReference<List<OrderRequest>>() {
                });

        return result.getBody();
    }

}
