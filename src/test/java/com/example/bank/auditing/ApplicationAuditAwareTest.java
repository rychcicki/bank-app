package com.example.bank.auditing;

import com.example.bank.client.model.Address;
import com.example.bank.client.model.Client;
import com.example.bank.client.model.Role;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationAuditAwareTest {
    @Mock
    Authentication authentication;

    @InjectMocks
    ApplicationAuditAware applicationAuditAware;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private static Client buildClient() {
        Client client = new Client("Zdzislaw", "Krecina", LocalDate.of(1954, 4, 28),
                "zdzislaw.krecina@gmail.com", "secretpassword",
                new Address("Tatrzanska", "7B", "12-456", "Zywiec"));
        client.setId(123L);
        return client;
    }

    private void setContext(Authentication authentication) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    @DisplayName("getCurrentAuditorTest")
    @Test
    void shouldReturnIdOfAuthenticatedClient() {
        final Client client = buildClient();
        setContext(authentication);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(client);

        Optional<Long> currentAuditor = applicationAuditAware.getCurrentAuditor();

        assertTrue(currentAuditor.isPresent());
        assertEquals(client.getId(), currentAuditor.get());
        verify(authentication, times(1)).isAuthenticated();
        verify(authentication, times(1)).getPrincipal();
    }

    @DisplayName("getCurrentAuditorNotAuthenticatedTest")
    @Test
    void shouldReturnEmptyWhenNotAuthenticated() {
        setContext(authentication);

        when(authentication.isAuthenticated()).thenReturn(false);

        Optional<Long> currentAuditor = applicationAuditAware.getCurrentAuditor();

        assertFalse(currentAuditor.isPresent());
        verify(authentication, never()).getPrincipal();
    }

    @DisplayName("getCurrentAuditorAnonymousAuthenticationTest")
    @Test
    void shouldReturnEmptyForAnonymousAuthentication() {
        final Client client = buildClient();
        final Authentication anonymousAuth = new AnonymousAuthenticationToken("anyKey", client,
                Collections.singletonList((Role.ADMIN)));
        setContext(anonymousAuth);

        Optional<Long> currentAuditorAnonymous = applicationAuditAware.getCurrentAuditor();

        assertFalse(currentAuditorAnonymous.isPresent());
    }

    @DisplayName("getCurrentAuditorAuthenticationNullTest")
    @Test
    void shouldReturnEmptyWhenAuthenticationIsNull() {
        setContext(null);

        Optional<Long> currentAuditorEmpty = applicationAuditAware.getCurrentAuditor();

        assertFalse(currentAuditorEmpty.isPresent());
    }

    @DisplayName("getCurrentAuditorNoAuthenticationSetTest")
    @Test
    void shouldReturnEmptyWhenNoAuthenticationSet() {
        SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());

        Optional<Long> currentAuditor = applicationAuditAware.getCurrentAuditor();

        assertFalse(currentAuditor.isPresent());
    }

    @DisplayName("getCurrentAuditorPrincipalNotClientTest")
    @Test
    void shouldReturnEmptyWhenPrincipalIsNotClient() {
        setContext(authentication);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("not-a-client");

        Optional<Long> currentAuditorEmpty = applicationAuditAware.getCurrentAuditor();

        assertFalse(currentAuditorEmpty.isPresent());
    }
}
