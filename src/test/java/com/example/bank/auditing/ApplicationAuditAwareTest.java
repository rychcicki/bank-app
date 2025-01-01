package com.example.bank.auditing;

import com.example.bank.client.model.Address;
import com.example.bank.client.model.Client;
import com.example.bank.client.model.Role;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class ApplicationAuditAwareTest {
    @Mock
    Authentication authentication;

    @DisplayName("getCurrentAuditorTest")
    @Test
    void shouldReturnIdOfAuthenticatedClient() {
        Client client = new Client("Zdzislaw", "Krecina", LocalDate.of(1954, 4, 28),
                "zdzislaw.krecina@gmail.com", "secretpassword",
                new Address("Tatrzanska", "7B", "12-456", "Zywiec"));
        client.setId(123L);

        when(authentication.getPrincipal()).thenReturn(client);
        when(authentication.isAuthenticated()).thenReturn(true);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        ApplicationAuditAware applicationAuditAware = new ApplicationAuditAware();
        Optional<Long> currentAuditor = applicationAuditAware.getCurrentAuditor();

        assertTrue(currentAuditor.isPresent());
        assertEquals(client.getId(), currentAuditor.get());
        SecurityContextHolder.clearContext();
    }

    @DisplayName("getCurrentAuditorNotAuthenticatedTest")
    @Test
    void shouldReturnEmptyWhenClientIsNotAuthenticatedOrAnonymous() {
        Client client = new Client("Zdzislaw", "Krecina", LocalDate.of(1954, 4, 28),
                "zdzislaw.krecina@gmail.com", "secretpassword",
                new Address("Tatrzanska", "7B", "12-456", "Zywiec"));
        client.setId(123L);

        ApplicationAuditAware applicationAuditAware = new ApplicationAuditAware();

        when(authentication.isAuthenticated()).thenReturn(false);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        Optional<Long> currentAuditor = applicationAuditAware.getCurrentAuditor();
        assertEquals(Optional.empty(), currentAuditor);
        SecurityContextHolder.clearContext();

        SecurityContext contextNull = SecurityContextHolder.createEmptyContext();
        contextNull.setAuthentication(null);
        SecurityContextHolder.setContext(contextNull);

        Optional<Long> currentAuditorEmpty = applicationAuditAware.getCurrentAuditor();
        assertEquals(Optional.empty(), currentAuditorEmpty);
        SecurityContextHolder.clearContext();

        Authentication anonymous = new AnonymousAuthenticationToken("anyKey", client, List.of(Role.ADMIN));
        SecurityContext contextAnonymous = SecurityContextHolder.createEmptyContext();
        contextAnonymous.setAuthentication(anonymous);
        SecurityContextHolder.setContext(contextAnonymous);

        Optional<Long> currentAuditorAnonymous = applicationAuditAware.getCurrentAuditor();
        assertEquals(Optional.empty(), currentAuditorAnonymous);
        SecurityContextHolder.clearContext();
    }
}
