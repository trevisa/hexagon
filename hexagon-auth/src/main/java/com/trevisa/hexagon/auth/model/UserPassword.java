package com.trevisa.hexagon.auth.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Data
@Entity
public class UserPassword {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    private String password;

    @CreationTimestamp
    private Instant createdAt;

    private Instant deletedAt;
}
