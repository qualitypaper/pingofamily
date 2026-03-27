package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.tokens.auth.AuthenticationToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AuthenticationTokenRepository extends JpaRepository<AuthenticationToken, Integer> {

  Optional<AuthenticationToken> findTopByToken(String authenticationToken);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Transactional
  AuthenticationToken save(AuthenticationToken authenticationToken);
}
