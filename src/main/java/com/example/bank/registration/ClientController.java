package com.example.bank.registration;

import com.example.bank.exception.ClientNotFoundException;
import com.example.bank.registration.jpa.Client;
import com.example.bank.registration.jpa.ClientRepository;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
/** 7) Dlaczego walidacja działa niezależnie od tego, czy w metodzie POST i PUT mam @Valid, czy też go tam nie ma??
 * Podobno przed parametrem w controllerze powinien być @Valid. Usunąłem go i nadal działa.*/
@Slf4j
class ClientController {
    private final ClientService clientService;
    private final ClientRepository clientRepository;

    @PostMapping("/clients")
    ResponseEntity<Client> registerClient(@RequestBody ClientRequest clientRequest) {
        return ResponseEntity.ok(clientService.registerClient(clientRequest));
    }

    @GetMapping("/clients/{id}")
    ResponseEntity<Client> getClient(@PathVariable @Min(1L) Long id) {
        return ResponseEntity.ok(clientService.getClient(id));
    }

    @PutMapping("/clients")
    ResponseEntity<Client> updateClient(@RequestBody ClientRequest clientRequest) {
        return ResponseEntity.ok(clientService.updateClient(clientRequest));
    }

    @DeleteMapping("/clients/{id}")
    ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Error. There is no client with id #" + id + " in database.");
                    return new ClientNotFoundException("Error. There is no client with id #" + id + " in database.");
                });
        clientService.deleteClient(client.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
