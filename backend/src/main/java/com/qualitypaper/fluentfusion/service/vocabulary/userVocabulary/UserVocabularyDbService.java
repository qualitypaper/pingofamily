package com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary;

import com.qualitypaper.fluentfusion.annotations.profiling.Profiling;
import com.qualitypaper.fluentfusion.buffers.Weights;
import com.qualitypaper.fluentfusion.model.vocabulary.*;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.LearningSession;
import com.qualitypaper.fluentfusion.repository.TrainingRepository;
import com.qualitypaper.fluentfusion.repository.UserVocabularyStatisticsRepository;
import com.qualitypaper.fluentfusion.repository.WordStatisticsRepository;
import com.qualitypaper.fluentfusion.repository.userVocabulary.UserVocabularyRepository;
import com.qualitypaper.fluentfusion.service.email.FormResendService;
import com.qualitypaper.fluentfusion.service.recall.RecallProbability;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserVocabularyDbService {

    private final UserVocabularyRepository userVocabularyRepository;
    private final FormResendService formResendService;
    private final UserVocabularyStatisticsRepository userVocabularyStatisticsRepository;
    private final WordStatisticsRepository wordStatisticsRepository;
    private final TrainingRepository trainingRepository;

    @Transactional(readOnly = true)
    public Stream<UserVocabulary> findAllByVocabulary(Vocabulary vocabulary) {
        return userVocabularyRepository.findAllByVocabulary(vocabulary);
    }

    @Transactional(readOnly = true)
    public List<UserVocabulary> findNewWordsForTraining(VocabularyGroup vocabularyGroup, int newWordsLimit) {
        return userVocabularyRepository.findAllByVocabularyGroupAndIsNew(
                vocabularyGroup,
                true,
                newWordsLimit == -1 ? Pageable.unpaged() : Pageable.ofSize(newWordsLimit).first());
    }

    @Transactional
    public void delete(UserVocabulary userVocabulary) {
        log.info("Deleting user vocabulary recursively with id: {}", userVocabulary.getId());
        userVocabulary = userVocabularyRepository.saveAndFlush(userVocabulary);

        try {
            List<UserVocabularyStatistics> trainingStatistics = userVocabulary.getTrainingStatistics();

            if (trainingStatistics != null && !trainingStatistics.isEmpty()) {
                for (UserVocabularyStatistics trainingStatistic : trainingStatistics) {
                    trainingStatistic.setUserVocabulary(null);
                    trainingStatistic.setVocabulary(userVocabulary.getVocabulary());
                    trainingStatistic.setDeletedAt(LocalDateTime.now());
                }

                userVocabularyStatisticsRepository.saveAll(trainingStatistics);
                userVocabulary.setTrainingStatistics(null);
            }

            if (userVocabulary.getWordStatistics() != null) {
                wordStatisticsRepository.save(userVocabulary.getWordStatistics());
                wordStatisticsRepository.deleteById(userVocabulary.getWordStatistics().getId());
                userVocabulary.setWordStatistics(null);
            }

            if (userVocabulary.getLearningSessions() != null && !userVocabulary.getLearningSessions().isEmpty()) {
                for (LearningSession learningSession : userVocabulary.getLearningSessions()) {
                    learningSession.getLearningWords().remove(userVocabulary);
                }

                userVocabulary.getLearningSessions().clear();
            }

            userVocabularyRepository.save(userVocabulary);
        } catch (Exception e) {
            formResendService.sendErrorMessage(e.getMessage());
            log.error(e.getMessage(), e);
            throw e;
        }
        if (userVocabulary.getNextTraining() != null) {
            try {
                trainingRepository.delete(userVocabulary.getNextTraining());
            } catch (Exception e) {
                log.error("Error while deleting next training for user vocabulary with id: {}", userVocabulary.getId());
            }
            userVocabulary.setNextTraining(null);
        }
        userVocabularyRepository.delete(userVocabulary);
        log.info("Successfully deleted user vocabulary with id: {}", userVocabulary.getId());
    }

    @Profiling
    public void insertWordsFrom(long predefinedVocabularyGroupId, long vocabularyId, long vocabularyGroupId) {
        userVocabularyRepository.insertPredefined(predefinedVocabularyGroupId, vocabularyId, vocabularyGroupId);
    }

    @Profiling
    public List<UserVocabulary> findTrainedWords(List<Long> learningWordsIds) {
        return userVocabularyRepository.findTrainedWords(learningWordsIds);
    }

    public List<Long> findAllForDeletion(long vocabularyGroupId) {
        return userVocabularyRepository.findAllForDeletion(vocabularyGroupId);
    }

    @Transactional
    public void deleteWithoutStatistics(List<Long> ids) {
        log.info("Deleting user vocabulary without statistics for ids: {}", ids);
        userVocabularyStatisticsRepository.deleteWithoutStatistics(ids);
        // userVocabularyRepository.deleteAllById(ids);
    }

    @Transactional
    public UserVocabulary saveAndFlush(UserVocabulary userVocabulary) {
        return userVocabularyRepository.saveAndFlush(userVocabulary);
    }

    @Transactional
    public List<UserVocabulary> saveAll(List<UserVocabulary> userVocabularyList) {
        return userVocabularyRepository.saveAll(userVocabularyList);
    }

    @Transactional
    public UserVocabulary save(UserVocabulary userVocabulary) {
        return userVocabularyRepository.save(userVocabulary);
    }

    @Transactional(readOnly = true)
    public List<UserVocabulary> findPrioritizedWordsForTraining(VocabularyGroup vocabularyGroup, int limit) {
        return userVocabularyRepository.findAllByVocabularyGroupAndPriority(
                        vocabularyGroup.getId(),
                        limit == -1 ? Pageable.unpaged() : Pageable.ofSize(limit).first())
                .getContent();
    }

    @Transactional(readOnly = true)
    public List<RecallProbability> getRecallProbabilities(VocabularyGroup vocabularyGroup) {
        return userVocabularyRepository.getRecallProbabilities(vocabularyGroup.getId());
    }

    @Transactional(readOnly = true)
    public List<UserVocabulary> findWordsForCustomizedTraining(VocabularyGroup vocabularyGroup, List<Long> userVocabularyIds) {
        return userVocabularyRepository.findAllByVocabularyGroupAndIds(vocabularyGroup, userVocabularyIds);
    }

    @Transactional
    protected Pageable setWeights(Weights weights, Pageable pageable) {
        Page<WordStatistics> page = wordStatisticsRepository
                .findAll(Example.of(WordStatistics.builder().weights(null).build()), pageable);
        if (!page.hasContent())
            return null;

        for (WordStatistics wordStatistics : page.getContent()) {
            if (wordStatistics == null)
                continue;

            wordStatistics.setWeights(new ArrayList<>());

            if (wordStatistics.getWeights().isEmpty()) {
                wordStatistics.getWeights()
                        .addAll(weights
                                .getAllFields()
                                .values()
                                .stream()
                                .mapToDouble(e -> (Double) e).boxed()
                                .toList());
                wordStatisticsRepository.save(wordStatistics);
            }
        }
        return page.nextPageable();
    }

    @Transactional
    public long countNewWords(VocabularyGroup vocabularyGroup) {
        return userVocabularyRepository.countAllByVocabularyGroupAndIsNew(vocabularyGroup, true);
    }
}
