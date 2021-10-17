package th.co.infinitait.goodluck.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
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
    private final UpdateOrderRepository updateOrderRepository;

    @Async
    public void uploadFileUpdateParcelCode(MultipartFile file, String userId) throws Exception {
        List<OrderRequest> orderRequestList = excelHelperService.excelToMap(file.getInputStream(),"order");
        log.info("orderRequestList : {}", orderRequestList.size());
        updateParcelCode(orderRequestList,userId);
    }

    public void updateParcelCode(List<OrderRequest> orderRequestList, String userId){
        List<UpdateOrderEntity> updateOrderEntityList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(orderRequestList)){
            updateOrderRepository.deleteByState("Shipping");
            for(OrderRequest orderRequest : orderRequestList){
                try {
                    Optional<OrderEntity> optional = orderRepository.findByRecipientName(orderRequest.getRecipientName().trim());
                    if(optional.isPresent()){
                        OrderEntity orderEntity = optional.get();
                        if(orderEntity.getStatus().equalsIgnoreCase("Draft") ||
                                orderEntity.getStatus().equalsIgnoreCase("Shipping")){
                            orderEntity.setParcelCode(orderRequest.getParcelCode());
                            orderEntity.setStatus("Shipping");
                            orderEntity.setUpdatedBy(userId);
                            orderEntity.setUpdatedAt(new Date());
                            orderEntity.setDeliveryDate(new Date());
                            orderRepository.save(orderEntity);

                            UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                            updateOrder.setStatus("success");
                            updateOrder.setState("Shipping");
                            updateOrder.setTotalAmount(orderEntity.getTotalAmount());
                            updateOrder.setParcelCode(orderRequest.getParcelCode());
                            updateOrder.setRecipientName(orderRequest.getRecipientName());
                            updateOrder.setCreatedBy(userId);
                            updateOrder.setCreatedAt(new Date());
                            updateOrderEntityList.add(updateOrder);
                        }else{
                            UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                            updateOrder.setStatus("fail");
                            updateOrder.setState("Shipping");
                            updateOrder.setErrorMessage("รายการสินค้าของชื่อผู้รับนี้ไม่อยํ่ในสถานะแบบร่างหรือกำลังจัดส่ง");
                            updateOrder.setParcelCode(orderRequest.getParcelCode());
                            updateOrder.setRecipientName(orderRequest.getRecipientName());
                            updateOrder.setCreatedBy(userId);
                            updateOrder.setCreatedAt(new Date());
                            updateOrderEntityList.add(updateOrder);
                        }
                    }else{
                        UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                        updateOrder.setStatus("fail");
                        updateOrder.setState("Shipping");
                        updateOrder.setErrorMessage("ไม่มีชื่อผู้รับในระบบ");
                        updateOrder.setParcelCode(orderRequest.getParcelCode());
                        updateOrder.setRecipientName(orderRequest.getRecipientName());
                        updateOrder.setCreatedBy(userId);
                        updateOrder.setCreatedAt(new Date());
                        updateOrderEntityList.add(updateOrder);
                    }
                }catch (Exception e){
                    UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                    updateOrder.setStatus("fail");
                    updateOrder.setState("Shipping");
                    updateOrder.setErrorMessage(e.getMessage());
                    updateOrder.setParcelCode(orderRequest.getParcelCode());
                    updateOrder.setRecipientName(orderRequest.getRecipientName());
                    updateOrder.setCreatedBy(userId);
                    updateOrder.setCreatedAt(new Date());
                    updateOrderEntityList.add(updateOrder);
                }
            }
        }
        log.info("fail size : {}", updateOrderEntityList.size());
        updateOrderRepository.saveAll(updateOrderEntityList);
    }

    @Async
    public void uploadFileUpdateSuccess(MultipartFile file, String userId) throws IOException {
        List<OrderRequest> orderRequestList = excelHelperService.excelToMap(file.getInputStream(),"order");
        log.info("orderRequestList : {}", orderRequestList.size());
        updateSuccess(orderRequestList,userId);
    }

    public void updateSuccess(List<OrderRequest> orderRequestList, String userId){
        List<UpdateOrderEntity> updateOrderEntityList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(orderRequestList)){
            updateOrderRepository.deleteByState("Success");
            for(OrderRequest orderRequest : orderRequestList){
                try {
                    Optional<OrderEntity> optional = orderRepository.findByParcelCode(orderRequest.getParcelCode().trim());
                    if(optional.isPresent()){
                        OrderEntity orderEntity = optional.get();
                        orderEntity.setStatus("Success");
                        orderEntity.setUpdatedBy(userId);
                        orderEntity.setUpdatedAt(new Date());
                        orderEntity.setDepositDate(new Date());
                        orderRepository.save(orderEntity);

                        UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                        updateOrder.setStatus("success");
                        updateOrder.setState("Success");
                        updateOrder.setTotalAmount(orderEntity.getTotalAmount());
                        updateOrder.setParcelCode(orderRequest.getParcelCode());
                        updateOrder.setRecipientName(orderRequest.getRecipientName());
                        updateOrder.setCreatedBy(userId);
                        updateOrder.setCreatedAt(new Date());
                        updateOrderEntityList.add(updateOrder);
                    }else{
                        UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                        updateOrder.setStatus("fail");
                        updateOrder.setState("Success");
                        updateOrder.setErrorMessage("ไม่มีรหัสพัสดุนี้ในระบบ");
                        updateOrder.setParcelCode(orderRequest.getParcelCode());
                        updateOrder.setRecipientName(orderRequest.getRecipientName());
                        updateOrder.setCreatedBy(userId);
                        updateOrder.setCreatedAt(new Date());
                        updateOrderEntityList.add(updateOrder);
                    }
                }catch (Exception e){
                    UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                    updateOrder.setStatus("fail");
                    updateOrder.setState("Success");
                    updateOrder.setErrorMessage(e.getMessage());
                    updateOrder.setParcelCode(orderRequest.getParcelCode());
                    updateOrder.setRecipientName(orderRequest.getRecipientName());
                    updateOrder.setCreatedBy(userId);
                    updateOrder.setCreatedAt(new Date());
                    updateOrderEntityList.add(updateOrder);
                }
            }
        }
        log.info("fail size : {}", updateOrderEntityList.size());
        updateOrderRepository.saveAll(updateOrderEntityList);
    }

    @Async
    public void uploadFileUpdateCancel(MultipartFile file, String userId) throws IOException {
        List<OrderRequest> orderRequestList = excelHelperService.excelToMap(file.getInputStream(),"order");
        log.info("orderRequestList : {}", orderRequestList.size());
        updateCancel(orderRequestList,userId);
    }

    public void updateCancel(List<OrderRequest> orderRequestList, String userId){
        List<UpdateOrderEntity> updateOrderEntityList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(orderRequestList)){
            updateOrderRepository.deleteByState("Cancel");
            for(OrderRequest orderRequest : orderRequestList){
                try {
                    Optional<OrderEntity> optional = orderRepository.findByParcelCode(orderRequest.getParcelCode().trim());
                    if(optional.isPresent()){
                        OrderEntity orderEntity = optional.get();
                        orderEntity.setStatus("Cancel");
                        orderEntity.setUpdatedBy(userId);
                        orderEntity.setUpdatedAt(new Date());
                        orderRepository.save(orderEntity);

                        UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                        updateOrder.setStatus("success");
                        updateOrder.setState("Cancel");
                        updateOrder.setTotalAmount(orderEntity.getTotalAmount());
                        updateOrder.setParcelCode(orderRequest.getParcelCode());
                        updateOrder.setRecipientName(orderRequest.getRecipientName());
                        updateOrder.setCreatedBy(userId);
                        updateOrder.setCreatedAt(new Date());
                        updateOrderEntityList.add(updateOrder);
                    }else{
                        UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                        updateOrder.setStatus("fail");
                        updateOrder.setState("Cancel");
                        updateOrder.setErrorMessage("ไม่มีรหัสพัสดุนี้ในระบบ");
                        updateOrder.setParcelCode(orderRequest.getParcelCode());
                        updateOrder.setRecipientName(orderRequest.getRecipientName());
                        updateOrder.setCreatedBy(userId);
                        updateOrder.setCreatedAt(new Date());
                        updateOrderEntityList.add(updateOrder);
                    }
                }catch (Exception e){
                    UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                    updateOrder.setStatus("fail");
                    updateOrder.setState("Cancel");
                    updateOrder.setErrorMessage(e.getMessage());
                    updateOrder.setParcelCode(orderRequest.getParcelCode());
                    updateOrder.setRecipientName(orderRequest.getRecipientName());
                    updateOrder.setCreatedBy(userId);
                    updateOrder.setCreatedAt(new Date());
                    updateOrderEntityList.add(updateOrder);
                }
            }
        }
        log.info("fail size : {}", updateOrderEntityList.size());
        updateOrderRepository.saveAll(updateOrderEntityList);
    }

}
