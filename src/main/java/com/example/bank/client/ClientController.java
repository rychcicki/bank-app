package com.example.bank.client;

import com.example.bank.client.jpa.Client;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/client")
@RequiredArgsConstructor
class ClientController {
    private final ClientService clientService;

    @GetMapping("/find/{id}")
    ResponseEntity<Client> getClient(@PathVariable @Min(1) Integer id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @PutMapping("/update/{id}")
    ResponseEntity<Client> updateClientById(@PathVariable @Min(1) Integer id, @RequestBody ClientRequest clientRequest) {
        return ResponseEntity.ok(clientService.updateClientById(id, clientRequest));
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<Void> deleteClient(@PathVariable Integer id) {
        clientService.deleteClient(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request, Principal connectedUser) {
        clientService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }
}
