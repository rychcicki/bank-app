package com.example.bank.client;

import com.example.bank.client.model.Client;
import com.example.bank.client.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByStatusAndEmail(Status status, String email);

    Optional<Client> findByStatusAndId(Status status, Long id);

    List<Client> findAllByStatus(Status status);
}
