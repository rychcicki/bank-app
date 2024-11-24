package com.example.bank.client;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
class ClientController {
    private final ClientServiceImpl clientService;

    @PostMapping
    ClientDTO createClient(@RequestBody ClientRequest clientRequest) {
        return clientService.createClient(clientRequest);
    }

    @GetMapping("{id}")
    ClientDTO getClient(@PathVariable @Min(1) Long id) {
        return clientService.findClientAsDtoById(id);
    }

    @GetMapping
    Set<ClientDTO> getClients() {
        return clientService.findClients();
    }

    @PutMapping("{id}")
    ClientDTO updateClient(@PathVariable @Min(1) Long id, @RequestBody ClientRequest clientRequest) {
        return clientService.updateClient(id, clientRequest);
    }

    @DeleteMapping("{id}")
    void deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
    }
}
