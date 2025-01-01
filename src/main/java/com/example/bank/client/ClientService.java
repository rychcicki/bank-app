package com.example.bank.client;

import com.example.bank.client.model.Client;

import java.util.Set;

public interface ClientService {
    ClientDTO createClient(ClientRequest clientRequest);

    Client findClient(Long id);

    Set<ClientDTO> findClients();

    ClientDTO updateClient(Long id, ClientRequest clientRequest);

    void deleteClient(Long id);
}
