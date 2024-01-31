package com.example.bank.security.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Integer> {
    Optional<Token> findByToken(String token);

    @Query(value = "select t from Token t " +
            " inner join Client c on t.client.id = c.id" +
            " where c.id = :id and (t.expired = false or t.revoked = false)")
    List<Token> findAllValidTokenByClient(Integer id);
}
