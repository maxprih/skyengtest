package com.maxpri.skyengtest.model.dto.request;

import com.maxpri.skyengtest.model.entity.EType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author max_pri
 */
@Data
@AllArgsConstructor
public class MailItemRequest {
    @NotNull
    private EType type;

    @NotNull
    private Integer startIndex;

    @NotNull
    private Integer receiverIndex;

    @NotEmpty
    private String receiverAddress;

    @NotEmpty
    private String receiverName;
}
