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
    private final Status status = Status.ACTIVE;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clientRepository
                .findByStatusAndEmail(status, username)
                .orElseThrow(() -> new UsernameNotFoundException("Client not found"));
    }
}
