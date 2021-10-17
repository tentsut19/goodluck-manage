package th.co.infinitait.goodluck.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import th.co.infinitait.goodluck.entity.OrderEntity;
import th.co.infinitait.goodluck.entity.UpdateOrderEntity;
import th.co.infinitait.goodluck.model.request.OrderRequest;
import th.co.infinitait.goodluck.model.response.OrderResponse;
import th.co.infinitait.goodluck.repository.OrderRepository;
import th.co.infinitait.goodluck.repository.UpdateOrderRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateOrderService {

    private final UpdateOrderRepository updateOrderRepository;

    public List<OrderResponse> getUpdateOrder(String state) throws Exception {
        List<OrderResponse> updateOrderEntityList = new ArrayList<>();
        List<UpdateOrderEntity> updateOrderEntities = updateOrderRepository.findByState(state);
        if(!CollectionUtils.isEmpty(updateOrderEntities)){
            for(UpdateOrderEntity updateOrderEntity:updateOrderEntities){
                OrderResponse orderResponse = new OrderResponse();
                orderResponse.setId(updateOrderEntity.getId());
                orderResponse.setStatus(updateOrderEntity.getStatus());
                orderResponse.setState(updateOrderEntity.getState());
                orderResponse.setErrorMessage(updateOrderEntity.getErrorMessage());
                orderResponse.setParcelCode(updateOrderEntity.getParcelCode());
                orderResponse.setRecipientName(updateOrderEntity.getRecipientName());
                orderResponse.setTotalAmount(updateOrderEntity.getTotalAmount());
                updateOrderEntityList.add(orderResponse);
            }
        }
        return updateOrderEntityList;
    }

    public void deleteUpdateOrder(String state) throws Exception {
        updateOrderRepository.deleteByState(state);
    }

}
