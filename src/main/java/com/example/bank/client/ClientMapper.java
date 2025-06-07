package com.example.bank.client;

import com.example.bank.client.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    @Mapping(target = "createdOn", source = "createdOn", dateFormat = "dd-MMM-yyyy HH:mm:ss")
    ClientDTO clientToDto(Client client);

    void updateClient(@MappingTarget Client clientToUpdate, ClientUpdateRequest clientUpdateRequest);

    Client clientRequestToClient(ClientRequest clientRequest);
}
