package com.maxpri.skyengtest.model.dto.response;

import com.maxpri.skyengtest.model.entity.EStatus;
import com.maxpri.skyengtest.model.entity.EType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author max_pri
 */
@Data
@AllArgsConstructor
public class MailItemInfoResponse {
    private EType type;
    private EStatus status;
    private int startIndex;
    private int receiverIndex;
    private String receiverAddress;
    private String receiverName;
    private List<MailItemHistoryEvent> history;
}
