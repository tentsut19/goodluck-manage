package th.co.infinitait.goodluck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import th.co.infinitait.goodluck.entity.OrderProductEntity;
import th.co.infinitait.goodluck.entity.SettingProductEntity;

import java.util.List;

public interface SettingProductRepository extends JpaRepository<SettingProductEntity, Long> {

    @Query(value = "SELECT * FROM tb_setting_product " +
            "WHERE name = :name ", nativeQuery = true)
    List<SettingProductEntity> findByName(@Param("name")String name);

}
