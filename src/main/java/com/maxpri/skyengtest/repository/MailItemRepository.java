package com.maxpri.skyengtest.repository;

import com.maxpri.skyengtest.model.entity.MailItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author max_pri
 */
@Repository
public interface MailItemRepository extends JpaRepository<MailItem, Long> {
    boolean existsById(Long id);
}
