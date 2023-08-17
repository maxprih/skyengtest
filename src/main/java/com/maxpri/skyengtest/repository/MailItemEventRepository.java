package com.maxpri.skyengtest.repository;

import com.maxpri.skyengtest.model.entity.MailItemEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author max_pri
 */
@Repository
public interface MailItemEventRepository extends JpaRepository<MailItemEvent, Long> {
    List<MailItemEvent> findByMailItemIdOrderByEventDate(Long mailItemId);
}
