package com.example.bank.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN')")
class ClientController {
    private final ClientServiceImpl clientService;

    @PostMapping
    ClientDTO createClient(@RequestBody @Valid ClientRequest clientRequest) {
        return clientService.createClient(clientRequest);
    }

    @GetMapping("{id}")
    ClientDTO getClient(@PathVariable @Min(1) Long id) {
        return clientService.findClientAsDto(id);
    }

    @GetMapping
    Set<ClientDTO> getClients() {
        return clientService.findClients();
    }

    @PutMapping("{id}")
    ClientDTO updateClient(
            @PathVariable @Min(1) Long id, @RequestBody @Valid ClientUpdateRequest clientRequest) {
        return clientService.updateClient(id, clientRequest);
    }

    @DeleteMapping("{id}")
    void softDeleteClient(@PathVariable @Min(1) Long id) {
        clientService.softDeleteClient(id);
    }
}
