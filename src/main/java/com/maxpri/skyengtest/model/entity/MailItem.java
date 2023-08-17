package com.maxpri.skyengtest.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author max_pri
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table
public class MailItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private EType type;

    @Column(nullable = false)
    private EStatus status;

    @Column(nullable = false)
    private int startIndex;

    @Column(nullable = false)
    private int currentIndex;

    @Column()
    private Integer movingToIndex;

    @Column(nullable = false)
    private int receiverIndex;

    @Column(nullable = false)
    private String receiverAddress;

    @Column(nullable = false)
    private String receiverName;
}
