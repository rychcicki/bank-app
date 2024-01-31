package com.example.bank.registration;

import com.example.bank.registration.jpa.Client;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
class ClientController {
    private final ClientService clientService;

//    @PostMapping("/register")
//    ResponseEntity<Client> registerClient(@RequestBody ClientRequest clientRequest) {
//        return ResponseEntity.ok(clientService.registerClient(clientRequest));
//    }

    @GetMapping("/find/{id}")
//    @PreAuthorize("admin:read")
    ResponseEntity<Client> getClient(@PathVariable @Min(1L) Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @PutMapping("/update")
    ResponseEntity<Client> updateClient(@RequestBody ClientRequest clientRequest) {
        return ResponseEntity.ok(clientService.updateClient(clientRequest));
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
