package com.qualitypaper.fluentfusion.config.db;

import com.qualitypaper.fluentfusion.controller.dto.request.CreateUserVocabularyRequest;
import com.qualitypaper.fluentfusion.exception.notfound.NotFoundException;
import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.repository.UserRepository;
import com.qualitypaper.fluentfusion.repository.VocabularyRepository;
import com.qualitypaper.fluentfusion.service.email.FormResendService;
import com.qualitypaper.fluentfusion.service.user.UserService;
import com.qualitypaper.fluentfusion.service.vocabulary.vocabulary.VocabularyDbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements ApplicationRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final FormResendService formResendService;
  private final VocabularyRepository vocabularyRepository;
  private final VocabularyDbService vocabularyDbService;
  private final Environment environment;

  @Value("${server.port}")
  private String port;

  @Override
  public void run(ApplicationArguments args) {
    loadAdminUser();
    for (Language learningLanguage : Language.values()) {
      for (Language nativeLanguage : Language.values()) {
        if (nativeLanguage.name().equals(learningLanguage.name())) continue;

        loadVocabularies(learningLanguage, nativeLanguage);
      }
    }

    sendStartUpMessage();
  }


  private void sendStartUpMessage() {
    formResendService.sendInfoMessage("PingoFamily backend has started on port: " + port);
  }

  private void loadAdminUser() {

    // loading WORD_SCRIPT_USER
    var optional = userRepository.findByEmail(UserService.WORD_SCRIPT_USER.getEmail());

    if (optional.isEmpty()) {
      UserService.WORD_SCRIPT_USER.setPassword(passwordEncoder.encode(UserService.WORD_SCRIPT_USER.getPassword()));
      User user = userRepository.save(UserService.WORD_SCRIPT_USER);
      UserService.WORD_SCRIPT_USER.setId(user.getId());
    } else {
      User user = optional.get();
      if (!passwordEncoder.matches(UserService.WORD_SCRIPT_USER.getPassword(), user.getPassword())) {
        user.setPassword(passwordEncoder.encode(UserService.WORD_SCRIPT_USER.getPassword()));
        userRepository.save(user);
      }
      UserService.WORD_SCRIPT_USER.setId(optional.get().getId());
    }
  }

  private void loadVocabularies(Language learningLanguage, Language nativeLanguage) {

    try {
      vocabularyRepository.findByLearningLanguageAndNativeLanguageAndCreatedBy(learningLanguage, nativeLanguage, UserService.WORD_SCRIPT_USER);
    } catch (NotFoundException e) {
      System.out.println("Saving VOCABULARY");
      vocabularyDbService.create(new CreateUserVocabularyRequest(learningLanguage.name(), nativeLanguage.name()), UserService.WORD_SCRIPT_USER, true);
    }
  }
}
