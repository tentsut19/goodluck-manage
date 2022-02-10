package th.co.infinitait.goodluck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import th.co.infinitait.goodluck.entity.OrderEntity;
import th.co.infinitait.goodluck.entity.UpdateOrderEntity;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface UpdateOrderRepository extends JpaRepository<UpdateOrderEntity, String> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM tb_update_order WHERE state = :state ", nativeQuery = true)
    int deleteByState(@Param("state") String state);

    List<UpdateOrderEntity> findByUuid(String uuid);

}
