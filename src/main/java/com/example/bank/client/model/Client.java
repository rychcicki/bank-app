package com.example.bank.client.model;

import com.example.bank.account.model.Account;
import com.example.bank.auditing.AuditorEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"account", "password"})
public class Client extends AuditorEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;

    private String lastname;

    private LocalDate birthDate;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonIgnore
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Client client = (Client) o;
        return Objects.equals(id, client.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
