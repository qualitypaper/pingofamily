package com.qualitypaper.fluentfusion.service.vocabulary.vocabularyGroup;

import com.qualitypaper.fluentfusion.annotations.profiling.Profiling;
import com.qualitypaper.fluentfusion.exception.notfound.VocabularyGroupNotFoundException;
import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.Difficulty;
import com.qualitypaper.fluentfusion.model.vocabulary.Vocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroup;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroupType;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.LearningSession;
import com.qualitypaper.fluentfusion.repository.LearningSessionRepository;
import com.qualitypaper.fluentfusion.repository.VocabularyGroupRepository;
import com.qualitypaper.fluentfusion.service.pts.s3.S3Service;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.UserVocabularyDbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class VocabularyGroupDbService {
  private final LearningSessionRepository learningSessionRepository;

  private final VocabularyGroupRepository vocabularyGroupRepository;
  private final UserVocabularyDbService userVocabularyDbService;
  private final S3Service s3Service;

  @Value("${microservice.main.vocabularyGroupImagesFolder}")
  private String vocabularyGroupImagesFolder;

  private final Random random = new Random();

  @Transactional
  public VocabularyGroup create(String name, Vocabulary vocabulary, VocabularyGroupType type, Difficulty difficulty) {

    VocabularyGroup vocabularyGroup = VocabularyGroup.builder()
            .name(name)
            .vocabulary(vocabulary)
            .type(type)
            .difficulty(difficulty)
            .createdAt(LocalDateTime.now())
            .imageUrl(pickRandomImageForVocabularyGroup())
            .build();

    vocabularyGroupRepository.save(vocabularyGroup);

    return vocabularyGroup;
  }

  public String pickRandomImageForVocabularyGroup() {
    String[] files = s3Service.getFolder("images/" + vocabularyGroupImagesFolder + "/");

    int randomInt = random.nextInt(0, files.length);

    String filename = files[randomInt];
    log.info("Picking random image for vocabulary group: {}", filename);

    return filename;
  }


  @Transactional
  @Profiling
  protected void prepareForDeletion(long id, User user) {
    VocabularyGroup vocabularyGroup = vocabularyGroupRepository.findById(id)
            .orElseThrow(VocabularyGroupNotFoundException::new);
//
//    if (!vocabularyGroup.getVocabulary().getCreatedBy().getId().equals(user.getId()))
//      throw new VocabularyOwnerException("Wrong owner tried to delete vocabulary group, userId: " + user.getId());

    if (vocabularyGroup.getDeleted()) {
      return;
    }

    List<Long> list = userVocabularyDbService.findAllForDeletion(id);

    if (list.isEmpty()) {
      log.info("Vocabulary group is empty, no words to delete");
    } else {
      userVocabularyDbService.deleteWithoutStatistics(list);
    }
    log.info("Successfully deleted words from vocabulary group: {}", id);

    for (LearningSession learningSession : vocabularyGroup.getLearningSessions()) {
      learningSession.setVocabulary(vocabularyGroup.getVocabulary());
      learningSession.setVocabularyGroup(null);
    }
    learningSessionRepository.saveAll(vocabularyGroup.getLearningSessions());

    vocabularyGroup.setLearningSessions(null);
    vocabularyGroup.setName(vocabularyGroup.getName() + " - " + System.currentTimeMillis() + "-deleted");
    vocabularyGroup.setDeleted(true);
    vocabularyGroupRepository.save(vocabularyGroup);
    log.info("Vocabulary group {} prepared for deletion", vocabularyGroup.getId());
  }


  @Transactional
  @Profiling
  protected void deleteById(long id) {
    vocabularyGroupRepository.deleteById(id);
  }
}
