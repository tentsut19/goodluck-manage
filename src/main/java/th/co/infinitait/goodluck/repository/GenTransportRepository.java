package th.co.infinitait.goodluck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import th.co.infinitait.goodluck.entity.GenTransportEntity;
import th.co.infinitait.goodluck.entity.OrderEntity;
import th.co.infinitait.goodluck.entity.UpdateOrderEntity;

import java.util.List;
import java.util.Optional;

public interface GenTransportRepository extends JpaRepository<GenTransportEntity, String> {

}
