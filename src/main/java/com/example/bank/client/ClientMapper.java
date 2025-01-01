package com.example.bank.client;

import com.example.bank.client.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    @Mapping(target = "createdOn", source = "createdOn", dateFormat = "dd-MMM-yyyy HH:mm:ss")
    ClientDTO clientToDto(Client client);

    @Mapping(target = "password", ignore = true)
    void updateClient(@MappingTarget Client clientToUpdate, ClientRequest clientRequest);

    Client clientRequestToClient(ClientRequest clientRequest);
}
