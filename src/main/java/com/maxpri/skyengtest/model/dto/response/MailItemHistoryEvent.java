package com.maxpri.skyengtest.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author max_pri
 */
@Data
@AllArgsConstructor
public class MailItemHistoryEvent {
    private String description;
    private LocalDateTime date;
}
