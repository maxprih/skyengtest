package com.maxpri.skyengtest.repository;

import com.maxpri.skyengtest.model.entity.MailItemMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author max_pri
 */
@Repository
public interface MailItemMovementRepository extends JpaRepository<MailItemMovement, Long> {
    List<MailItemMovement> findByMailItemIdOrderByMovementDate(Long mailItemId);
}
