package com.example.bank.registration.jpa;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity(name = "client")
@Table(name = "client")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Client {
    @Id
    @SequenceGenerator(name = "client_sequence", sequenceName = "client_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_sequence")
    @Column(name = "id", nullable = false)
    @EqualsAndHashCode.Exclude
    private Long id;
    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "firstName is mandatory")
    private String firstName;
    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "lastName is mandatory")
    private String lastName;
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;
    @Column(nullable = false)
    @Email(message = "invalid email address")
    @NotBlank(message = "email is mandatory")
    private String email;
    @Embedded
    private Address address;
}
