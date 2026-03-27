package com.qualitypaper.fluentfusion.service.admin;

import com.qualitypaper.fluentfusion.controller.dto.response.auth.UserAdminResponse;
import com.qualitypaper.fluentfusion.mappers.user.UserMapper;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.Vocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.word.Word;
import com.qualitypaper.fluentfusion.repository.*;
import com.qualitypaper.fluentfusion.service.db.DbService;
import com.qualitypaper.fluentfusion.service.db.types.Compare;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabulary.VocabularyDbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.HttpMediaTypeNotAcceptableException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {
  private final RefreshTokenRepository refreshTokenRepository;

  private final DbService dbService;
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final VocabularyDbService vocabularyDbService;
  private final ConfirmationTokenRepository confirmationTokenRepository;
  private final ForgotPasswordRepository forgotPasswordRepository;
  private final AuthenticationTokenRepository authenticationTokenRepository;

  public List<Map<String, Object>> query(String query) throws HttpMediaTypeNotAcceptableException {

    return dbService.query(query);
  }

  public String execute(String command) throws IOException {
    boolean isWindows = System.getProperty("os.name")
            .toLowerCase().startsWith("windows");

    ProcessBuilder builder = new ProcessBuilder();
    if (isWindows) {
      builder.command("cmd.exe", "/c", command);
    } else {
      builder.command("sh", "-c", command);
    }

    builder.redirectErrorStream(true);
    Process p = builder.start();
    BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
    String line = r.readLine();
    StringBuilder res = new StringBuilder();

    while (line != null) {
      res.append(line).append('\n');
      line = r.readLine();
    }

    return res.toString();
  }


  public List<Map<String, Object>> getWord(Long wordId) throws HttpMediaTypeNotAcceptableException {
    log.info("Getting word with id: {}", wordId);
    List<Map<String, Object>> select = dbService.select(Word.class, List.of("*"), List.of(Compare.eq("id", wordId)));
    log.info(select.toString());
    return select;
  }

  @Transactional
  public List<UserAdminResponse> allUsers() {

    return userRepository.findAll().stream().map(e -> {
      Hibernate.initialize(e.getUserVocabularies());
      return userMapper.from(e);
    }).toList();
  }

  @Async
  @Transactional
  public void deleteUser(long id) {
    Optional<User> userOptional = userRepository.findById(id);
    if (userOptional.isEmpty()) return;

    User user = userOptional.get();

    for (Vocabulary voc : new ArrayList<>(user.getUserVocabularies())) {
      vocabularyDbService.deleteVocabulary(voc.getId());
    }
    log.info("Deleted user vocabularies");

    refreshTokenRepository.deleteAll(user.getRefreshTokens());
    confirmationTokenRepository.deleteAllByUser(user);
    forgotPasswordRepository.deleteAllByUser(user);
    log.info("Deleted all tokens related to user");
    userRepository.delete(user);
    log.info("Deleted user: {}", id);
  }
}
