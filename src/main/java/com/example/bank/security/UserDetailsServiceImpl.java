package com.example.bank.security;

import com.example.bank.client.ClientRepository;
import com.example.bank.client.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return clientRepository
                .findByStatusAndEmail(Status.ACTIVE, username)
                .orElseThrow(() -> new UsernameNotFoundException("Client not found"));
    }
}
