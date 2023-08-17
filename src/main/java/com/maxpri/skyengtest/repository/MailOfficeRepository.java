package com.maxpri.skyengtest.repository;

import com.maxpri.skyengtest.model.entity.MailOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author max_pri
 */
@Repository
public interface MailOfficeRepository extends JpaRepository<MailOffice, Long> {
    boolean existsByIndex(int index);
}
