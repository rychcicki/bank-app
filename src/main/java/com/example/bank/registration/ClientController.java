package com.example.bank.registration;

import com.example.bank.registration.jpa.Client;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
class ClientController {
    private final ClientService clientService;

    @PostMapping("/clients")
    ResponseEntity<Client> registerClient(@RequestBody ClientRequest clientRequest) {
        return ResponseEntity.ok(clientService.registerClient(clientRequest));
    }

    @GetMapping("/clients/{id}")
    ResponseEntity<Client> getClient(@PathVariable @Min(1L) Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @PutMapping("/clients")
    ResponseEntity<Client> updateClient(@RequestBody ClientRequest clientRequest) {
        return ResponseEntity.ok(clientService.updateClient(clientRequest));
    }

    @DeleteMapping("/clients/{id}")
    ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
