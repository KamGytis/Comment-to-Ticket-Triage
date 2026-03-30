package com.pulsedesk.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tickets")

public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String title;
    private String description;
    private String category;
    private String priority;

    @Column (length = 1000)
    private String summary;
    private Long commentId;
    private LocalDateTime createdAt;

}
