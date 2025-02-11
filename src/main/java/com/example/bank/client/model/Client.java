package com.example.bank.client.model;

import com.example.bank.account.model.Account;
import com.example.bank.auditing.AuditorEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;


@Entity
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = "account")
@JsonIgnoreProperties(value = "account")
public class Client extends AuditorEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "firstname is mandatory")
    private String firstname;

    @NotBlank(message = "lastname is mandatory")
    private String lastname;

    @NotNull
    private LocalDate birthDate;

    @Email(regexp = "^[^@]+@[^@]+\\.[^@]+$", message = "invalid email address")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "client", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Account> account;

    public Client(String firstname, String lastname, LocalDate birthDate, String email, String password,
                  Address address) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.birthDate = birthDate;
        this.email = email;
        this.password = password;
        this.role = Role.USER;
        this.status = Status.ACTIVE;
        this.address = address;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role == null ? null : List.of(role);
    }

    @Override
    public String getUsername() {
        return email;
    }
}
