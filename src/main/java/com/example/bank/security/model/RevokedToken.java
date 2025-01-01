package com.example.bank.security.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class RevokedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(unique = true)
    public String token;
}
