package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.tokens.confirmation.ConfirmationToken;
import com.qualitypaper.fluentfusion.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken, Integer> {

  Optional<ConfirmationToken> findByConfirmationToken(String confirmationToken);

  Optional<ConfirmationToken> findByUser(User user);

  void deleteAllByUser(User user);
}
