package com.maxpri.skyengtest.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author max_pri
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table
public class MailItemMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "mail_item_id")
    private MailItem mailItem;

    @Column
    private int sourceIndex;

    @Column
    private int destinationIndex;

    @Column
    private LocalDateTime movementDate;
}
