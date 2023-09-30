package com.example.bank.userRegister;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Entity(name = "user")
@Table(name = "\"USER\"")   //słowo kluczowe w postgresie??!!, inaczej nie zadziała
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class User {
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(
            name = "first_name",
            nullable = false
    )
    private String firstName;
    @Column(
            name = "last_name",
            nullable = false
    )
    private String lastName;
    @Transient
    /** Czy jest sens wstawiania tego rodzaju pól??*/
    private String fullName = firstName + " " + lastName;
    @Column(
            name = "birth_date",
            nullable = false
    )
    private LocalDate birthDate;
    @Column(
            nullable = false
    )
    private String email;
    @Embedded
    private Address address;
}
