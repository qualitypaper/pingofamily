package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.tokens.forgotPassword.ForgotPassword;
import com.qualitypaper.fluentfusion.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgotPasswordRepository extends JpaRepository<ForgotPassword, Long> {
  ForgotPassword findForgotPasswordByToken(String token);

  void deleteAllByUser(User user);
}
