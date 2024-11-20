package com.example.bank.client;

import com.example.bank.client.model.Client;

import java.util.List;

public interface ClientService {
    ClientDTO createClient(ClientRequest clientRequest);

    Client findClient(Long id);

    List<ClientDTO> findClients();

    ClientDTO updateClient(Long id, ClientRequest clientRequest);

    void deleteClient(Long id);
}
