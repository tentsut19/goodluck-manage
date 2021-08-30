package th.co.infinitait.goodluck.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import th.co.infinitait.goodluck.component.CabsatPayload;
import th.co.infinitait.goodluck.entity.*;
import th.co.infinitait.goodluck.model.request.OrderRequest;
import th.co.infinitait.goodluck.model.response.OrderResponse;
import th.co.infinitait.goodluck.repository.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExcelService {

    private final ExcelHelperService excelHelperService;
    private final OrderRepository orderRepository;
    private final CabsatPayload cabsatPayload;

    public List<OrderResponse> uploadFileUpdateParcelCode(MultipartFile file, String sheetName) throws Exception {
//        String formattedDateRun = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        List<OrderRequest> orderRequestList = excelHelperService.excelToMap(file.getInputStream(), sheetName);
        log.info("orderRequestList : {}", orderRequestList.size());
        return updateParcelCode(orderRequestList);
    }

    public List<OrderResponse> updateParcelCode(List<OrderRequest> orderRequestList){
        List<OrderResponse> orderResponseList = new ArrayList<>();
        log.info("cabsatPayload : {}",cabsatPayload.getUserId());
        if(StringUtils.isEmpty(cabsatPayload.getUserId())){
            throw new RuntimeException("cabsatPayload null");
        }
        if(!CollectionUtils.isEmpty(orderRequestList)){
            for(OrderRequest orderRequest : orderRequestList){
                try {
                    Optional<OrderEntity> optional = orderRepository.findByRecipientName(orderRequest.getRecipientName());
                    if(optional.isPresent()){
                        OrderEntity orderEntity = optional.get();
                        if(orderEntity.getStatus().equalsIgnoreCase("Draft")){
                            orderEntity.setParcelCode(orderRequest.getParcelCode());
                            orderEntity.setStatus("Shipping");
                            orderEntity.setUpdatedBy(cabsatPayload.getUserId());
                            orderEntity.setUpdatedAt(new Date());
                            orderRepository.save(orderEntity);

                            OrderResponse orderResponse = new OrderResponse();
                            orderResponse.setStatus("success");
                            orderResponse.setParcelCode(orderRequest.getParcelCode());
                            orderResponse.setRecipientName(orderRequest.getRecipientName());
                            orderResponseList.add(orderResponse);
                        }else{
                            OrderResponse orderResponse = new OrderResponse();
                            orderResponse.setStatus("fail");
                            orderResponse.setErrorMessage("ชื่อผู้รับนี้กำลังจัดส่ง");
                            orderResponse.setParcelCode(orderRequest.getParcelCode());
                            orderResponse.setRecipientName(orderRequest.getRecipientName());
                            orderResponseList.add(orderResponse);
                        }
                    }else{
                        OrderResponse orderResponse = new OrderResponse();
                        orderResponse.setStatus("fail");
                        orderResponse.setErrorMessage("ไม่มีชื่อผู้รับในระบบ");
                        orderResponse.setParcelCode(orderRequest.getParcelCode());
                        orderResponse.setRecipientName(orderRequest.getRecipientName());
                        orderResponseList.add(orderResponse);
                    }
                }catch (Exception e){
                    OrderResponse orderResponse = new OrderResponse();
                    orderResponse.setStatus("fail");
                    orderResponse.setErrorMessage(e.getMessage());
                    orderResponse.setParcelCode(orderRequest.getParcelCode());
                    orderResponse.setRecipientName(orderRequest.getRecipientName());
                    orderResponseList.add(orderResponse);
                }
            }
        }
        log.info("fail size : {}", orderResponseList.size());
        return orderResponseList;
    }

    public List<OrderResponse> uploadFileUpdateSuccess(MultipartFile file, String sheetName) throws IOException {
        List<OrderRequest> orderRequestList = excelHelperService.excelToMap(file.getInputStream(), sheetName);
        log.info("orderRequestList : {}", orderRequestList.size());
        return updateSuccess(orderRequestList);
    }

    public List<OrderResponse> updateSuccess(List<OrderRequest> orderRequestList){
        List<OrderResponse> orderResponseList = new ArrayList<>();
        log.info("cabsatPayload : {}",cabsatPayload.getUserId());
        if(StringUtils.isEmpty(cabsatPayload.getUserId())){
            throw new RuntimeException("cabsatPayload null");
        }
        if(!CollectionUtils.isEmpty(orderRequestList)){
            for(OrderRequest orderRequest : orderRequestList){
                try {
                    Optional<OrderEntity> optional = orderRepository.findByParcelCode(orderRequest.getParcelCode());
                    if(optional.isPresent()){
                        OrderEntity orderEntity = optional.get();
                        orderEntity.setStatus("Success");
                        orderEntity.setUpdatedBy(cabsatPayload.getUserId());
                        orderEntity.setUpdatedAt(new Date());
                        orderRepository.save(orderEntity);

                        OrderResponse orderResponse = new OrderResponse();
                        orderResponse.setStatus("success");
                        orderResponse.setParcelCode(orderRequest.getParcelCode());
                        orderResponse.setRecipientName(orderRequest.getRecipientName());
                        orderResponseList.add(orderResponse);
                    }else{
                        OrderResponse orderResponse = new OrderResponse();
                        orderResponse.setStatus("fail");
                        orderResponse.setErrorMessage("ไม่มีรหัสพัสดุนี้ในระบบ");
                        orderResponse.setParcelCode(orderRequest.getParcelCode());
                        orderResponse.setRecipientName(orderRequest.getRecipientName());
                        orderResponseList.add(orderResponse);
                    }
                }catch (Exception e){
                    OrderResponse orderResponse = new OrderResponse();
                    orderResponse.setStatus("fail");
                    orderResponse.setErrorMessage(e.getMessage());
                    orderResponse.setParcelCode(orderRequest.getParcelCode());
                    orderResponse.setRecipientName(orderRequest.getRecipientName());
                    orderResponseList.add(orderResponse);
                }
            }
        }
        log.info("fail size : {}", orderResponseList.size());
        return orderResponseList;
    }

}
