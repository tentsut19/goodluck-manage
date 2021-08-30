package th.co.infinitait.goodluck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import th.co.infinitait.goodluck.entity.OrderEntity;
import th.co.infinitait.goodluck.entity.OrderProductEntity;

import java.util.List;
import java.util.Optional;

public interface OrderProductRepository extends JpaRepository<OrderProductEntity, Long> {

    @Query(value = "SELECT * FROM tb_order_product " +
            "WHERE order_code = :orderCode ", nativeQuery = true)
    List<OrderProductEntity> findByOrderCode(@Param("orderCode")String orderCode);

}
