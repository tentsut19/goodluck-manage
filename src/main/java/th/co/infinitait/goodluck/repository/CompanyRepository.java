package th.co.infinitait.goodluck.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import th.co.infinitait.goodluck.entity.CompanyEntity;
import th.co.infinitait.goodluck.entity.OrderEntity;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {


}
