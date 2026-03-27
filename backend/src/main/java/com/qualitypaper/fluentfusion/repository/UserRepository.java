package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  List<User> findAllByLastActiveAtAfter(LocalDateTime time);


}
