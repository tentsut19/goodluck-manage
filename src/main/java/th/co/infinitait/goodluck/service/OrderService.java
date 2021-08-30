package th.co.infinitait.goodluck.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import th.co.infinitait.goodluck.entity.OrderEntity;
import th.co.infinitait.goodluck.exception.NotFoundException;
import th.co.infinitait.goodluck.model.request.OrderRequest;
import th.co.infinitait.goodluck.model.response.OrderResponse;
import th.co.infinitait.goodluck.repository.OrderRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository cardRegisterRepository;

    public OrderResponse createActivity(OrderRequest request, String organizationUser) {
        Long activityId = create(request,organizationUser);
        return getActivityById(activityId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Long create(OrderRequest request, String organizationUser) {

        OrderEntity orderEntity = OrderEntity.builder()
                .code(request.getCode())
                .createdAt(new Date())
                .createdBy(organizationUser)
                .build();
        OrderEntity entity = cardRegisterRepository.save(orderEntity);
        return entity.getId();
    }

    public List<OrderResponse> getAll(){
        List<OrderResponse> orderResponseList = new ArrayList<>();
        List<OrderEntity> orderEntityList = cardRegisterRepository.findAll();
        if(!CollectionUtils.isEmpty(orderEntityList)){
            orderEntityList.forEach(orderEntity -> {
                orderResponseList.add(toActivityResponse(orderEntity));
            });
        }
        return orderResponseList;
    }

    public List<OrderResponse> getByDepartmentId(Long departmentId){
        List<OrderResponse> orderResponseList = new ArrayList<>();
//        List<CardRegisterEntity> cardRegisterEntityList = cardRegisterRepository.findByDepartmentId(departmentId);
//        if(!CollectionUtils.isEmpty(cardRegisterEntityList)){
//            cardRegisterEntityList.forEach(cardRegisterEntity -> {
//                cardRegisterResponseList.add(toActivityResponse(cardRegisterEntity));
//            });
//        }
        return orderResponseList;
    }

    public OrderResponse getActivityById(Long cardRegisterId) {
        Optional<OrderEntity> optionalActivityEntity = cardRegisterRepository.findById(cardRegisterId);
        if(!optionalActivityEntity.isPresent()){
            throw new NotFoundException(String.format("CardRegister NotFound By cardRegisterId : %1s",cardRegisterId));
        }
        return optionalActivityEntity.map((Function<OrderEntity, OrderResponse>) this::toActivityResponse).orElse(null);
    }

    public OrderResponse toActivityResponse(OrderEntity entity){

        return OrderResponse.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
