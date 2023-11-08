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
/** Pytania na spotkanie:
 * 1. Czy jest sens 'nullable = false' przy polach @NotBlank ??
 * 2. Czy nie powinienem zmienić oznaczeń na @NotNull zamiast 'nullable = false' ??
 *
 * Dowiedziałem się o różnicy między 'nullable = false' i @NotNull - lepiej stosować @NotNull, gdyż:
 * - walidacja będzie mieć miejsce, zanim Hibernate wyśle do db zapytanie SQL;
 * - lepiej bazować na walidacji Beana niż na procesie sprawdzania poprawności przez db.
 *  * @NotNull -> Char, Collection, Map, Array - not null but it can be empty
 *  * @NotEmpty -> -||- - not null and greater than 0
 *  * @NotBlank -> String - not null and TRIMMED LENGTH is greater than 0
 * */
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
