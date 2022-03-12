package th.co.infinitait.goodluck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import th.co.infinitait.goodluck.entity.OrderEntity;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    @Query(value = "SELECT * FROM tb_order " +
            "WHERE recipient_name = :recipientName AND status != 'Cancel' AND deleted_at is null ", nativeQuery = true)
    List<OrderEntity> findByRecipientName(@Param("recipientName")String recipientName);

    @Query(value = "SELECT * FROM tb_order " +
            "WHERE REPLACE(REPLACE(REPLACE(recipient_name, ' ', ''), '\u200B', ''), \"?\", '') " +
            "= REPLACE(REPLACE(REPLACE(:recipientName, ' ', ''), '\u200B', ''), \"?\", '') " +
            "AND status != 'Cancel' AND deleted_at is null ", nativeQuery = true)
    List<OrderEntity> findByRecipientNameIgnoreSpace(@Param("recipientName")String recipientName);

    @Query(value = "SELECT * FROM tb_order " +
            "WHERE parcel_code = :parcelCode AND status != 'Cancel' AND deleted_at is null ", nativeQuery = true)
    Optional<OrderEntity> findByParcelCode(@Param("parcelCode")String parcelCode);

}
