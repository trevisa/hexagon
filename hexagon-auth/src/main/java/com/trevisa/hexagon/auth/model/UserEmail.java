package com.trevisa.hexagon.auth.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
public class UserEmail {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    private String email;

    @CreationTimestamp
    private Instant createdAt;

    private Instant deletedAt;
}
