package com.example.bank.userRegister;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@Entity(name = "user")
@Table(name = "API_USER")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
/** Czy poniższa adnotacja jest we właściwej klasie?? I czy w ogóle jest potrzebna?? Bez niej też się waliduje.*/

//czy jest sens nullable = false?? przy polach @NotBlank ??
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
    @NotBlank(message = "firstName is mandatory")
    private String firstName;
    @Column(
            name = "last_name",
            nullable = false
    )
    @NotBlank(message = "lastName is mandatory")
    private String lastName;
    @Column(
            name = "birth_date",
            nullable = false
    )
    private LocalDate birthDate;
    @Column(
            nullable = false
    )
    @Email(message = "invalid email address")
    @NotBlank(message = "email is mandatory")
    private String email;
    @Embedded
    private Address address;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(firstName, user.firstName) && Objects.equals(lastName, user.lastName)
                && Objects.equals(birthDate, user.birthDate) && Objects.equals(email, user.email)
                && Objects.equals(address, user.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, birthDate, email, address);
    }
}
