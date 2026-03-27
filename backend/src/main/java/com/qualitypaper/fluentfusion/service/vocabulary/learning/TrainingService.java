package com.qualitypaper.fluentfusion.service.vocabulary.learning;

import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabulary;
import com.qualitypaper.fluentfusion.model.vocabulary.UserVocabularyStatistics;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.Conjugation;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.Training;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.TrainingType;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.training.trainingExample.TrainingExample;
import com.qualitypaper.fluentfusion.model.vocabulary.word.WordTranslation;
import com.qualitypaper.fluentfusion.repository.TrainingRepository;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.trainingTypeScore.ScoreAlgorithm;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.algorithms.trainingTypeScore.TrainingResult;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample.TrainingExampleRegenerationType;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample.TrainingExampleService;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingScore.TrainingScore;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingScore.TrainingScoreFactory;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.types.TrainingMistake;
import com.qualitypaper.fluentfusion.service.vocabulary.learning.types.TrainingMistakeWithScore;
import com.qualitypaper.fluentfusion.service.vocabulary.structs.TranslationDirection;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.UserVocabularyDbService;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.statistics.UserVocabularyStatisticsService;
import com.qualitypaper.fluentfusion.service.vocabulary.wordExample.WordExampleService;
import com.qualitypaper.fluentfusion.util.types.Pair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.hibernate.LazyInitializationException;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static com.qualitypaper.fluentfusion.service.vocabulary.learning.trainingExample.TrainingExampleService.getTranslationDirection;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainingService {

    private static final List<TrainingType> FIRST_TRAINING_TYPES = List.of(
            TrainingType.COMPLETE_EMPTY_SPACES,
            TrainingType.AUDIO,
            TrainingType.TRANSLATION
    );

    private final TrainingRepository trainingRepository;
    private final TrainingExampleService trainingExampleService;
    private final UserVocabularyDbService userVocabularyDbService;
    private final UserVocabularyStatisticsService userVocabularyStatisticsService;
    private final WordExampleService wordExampleService;
    private final TrainingScoreFactory trainingScoreFactory;

    public Training createEmpty() {
        Training training = Training.builder()
                .createdAt(System.currentTimeMillis())
                .trainingExamples(new ArrayList<>())
                .build();

        return trainingRepository.save(training);
    }

    public void delete(Training training) {
        trainingRepository.delete(training);
    }

    public Training save(Training training) {
        return trainingRepository.save(training);
    }

    @Transactional
    public void generateFirstTraining(UserVocabulary userVocabulary) {
        Training training = createEmpty();
        List<TrainingExample> trainingExamples = new ArrayList<>();
        Deque<TrainingType> stack = new ArrayDeque<>(FIRST_TRAINING_TYPES);

        // not in another method because of the transactional annotation
        while (!stack.isEmpty()) {
            TrainingType trainingType = stack.pop();
            TranslationDirection translationDirection = getTranslationDirection(trainingType);
            boolean isReversed = TrainingExampleService.isReversedTraining(trainingType);
            TrainingExample trainingExample;

            if (trainingType.equals(TrainingType.AUDIO) || trainingType.equals(TrainingType.TRANSLATION)) {
                String word = isReversed
                        ? userVocabulary.getWordTranslation().getWordTo().getWord()
                        : userVocabulary.getWordTranslation().getWordFrom().getWord();
                String translation = isReversed
                        ? userVocabulary.getWordTranslation().getWordFrom().getWord()
                        : userVocabulary.getWordTranslation().getWordTo().getWord();
                String soundUrl = isReversed
                        ? userVocabulary.getWordTranslation().getWordTo().getSoundUrl()
                        : userVocabulary.getWordTranslation().getWordFrom().getSoundUrl();

                trainingExample = trainingExampleService.createTrainingExampleAndSave(userVocabulary.getWordTranslation(), training, word, translation,
                        soundUrl, translationDirection, trainingType, Optional.empty());
            } else {
                String sentence = userVocabulary.getWordExampleTranslation().getWordExampleFrom().getExample();
                String translation = userVocabulary.getWordExampleTranslation().getWordExampleTo().getExample();
                String soundUrl = isReversed
                        ? userVocabulary.getWordExampleTranslation().getWordExampleTo().getSoundUrl()
                        : userVocabulary.getWordExampleTranslation().getWordExampleFrom().getSoundUrl();

                trainingExample = trainingExampleService.createTrainingExampleAndSave(
                        userVocabulary.getWordTranslation(),
                        training,
                        isReversed ? translation : sentence,
                        isReversed ? sentence : translation,
                        soundUrl,
                        translationDirection,
                        trainingType,
                        Optional.empty()
                );
            }
            trainingExamples.add(trainingExample);
        }

        training.setTrainingExamples(trainingExamples);
        trainingRepository.save(training);

        userVocabulary.setNextTraining(training);
        userVocabularyDbService.save(userVocabulary);

        log.info("Generated first training for word: {}", userVocabulary.getWordTranslation().getWordFrom().getWord());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void generateNextTraining(TrainingMistakeWithScore trainingMistakeWithScore) {
        if (trainingMistakeWithScore == null) return;

        UserVocabulary userVocabulary = trainingMistakeWithScore.userVocabulary();
        Training newNextTraining = createEmpty();

        updateProblematicTrainingExamples(trainingMistakeWithScore, userVocabulary);
        userVocabulary.setLoading(true);
        userVocabularyDbService.save(userVocabulary);

        List<TrainingType> list = getNewTrainingTypes(trainingMistakeWithScore);
        List<TrainingExample> trainingExamplesList = new ArrayList<>();

        for (TrainingType trainingType : list) {
            TrainingExample trainingExample = generateExamplesForTraining(newNextTraining, trainingType, userVocabulary.getWordTranslation());
            trainingExamplesList.add(trainingExample);
        }

        newNextTraining.setTrainingExamples(trainingExamplesList);
        trainingRepository.save(newNextTraining);

        resetTrainingGenerationParams(trainingMistakeWithScore, newNextTraining);
        log.info("Successfully generated new training for user vocabulary with id: {}", userVocabulary.getId());
    }

    @NotNull
    private List<TrainingType> getNewTrainingTypes(TrainingMistakeWithScore trainingMistakeWithScore) {
        Map<TrainingType, Double> weights = getWeights(trainingMistakeWithScore, TrainingType.values());
        log.info("Generated weights: {}", weights);

        return weights.entrySet()
                .stream()
                // -----------------------------------------------
                // user request: "remove hard training types"
                .filter(e -> !e.getKey().equals(TrainingType.SENTENCE_TYPE) && !e.getKey().equals(TrainingType.SENTENCE_AUDIO))
                // -----------------------------------------------
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(LearningService.TRAINING_EXAMPLES_COUNT)
                .map(Map.Entry::getKey)
                .toList();
    }

    protected void updateProblematicTrainingExamples(TrainingMistakeWithScore trainingMistakeWithScore, UserVocabulary userVocabulary) {
        for (TrainingMistake currentTrainingMistake : trainingMistakeWithScore.trainingMistakes()) {
//      TrainingType currTrainingType = trainingExample.getTrainingExampleData().getTrainingType();

            TrainingExample trainingExample = currentTrainingMistake.trainingExampleData().trainingExample();
            TrainingExampleRegenerationType type = getTrainingExampleAction(currentTrainingMistake.trainingExampleScore());

            if (Objects.requireNonNull(type).equals(TrainingExampleRegenerationType.COPY)) {
                Hibernate.initialize(trainingExample);

//        toUseAlgorithmTrainingTypes.remove(currTrainingType.tryToDefault());
//        toUseAlgorithmTrainingTypes.remove(currTrainingType.tryToReversed());
//        trainingExamplesList.add(trainingExampleService.from(trainingExample, newNextTraining));

                if (userVocabulary.getProblematicTrainingExamples() == null) {
                    userVocabulary.setProblematicTrainingExamples(new HashSet<>());
                }
                if (userVocabulary.getProblematicTrainingExamples().stream().noneMatch(e -> Objects.equals(e.getId(), trainingExample.getId()))) {
                    userVocabulary.getProblematicTrainingExamples().add(trainingExample);
                }
            }
        }

    }

    private TrainingExampleRegenerationType getTrainingExampleAction(TrainingScore trainingScore) {
        if (trainingScore.isMistake()) {
            return TrainingExampleRegenerationType.COPY;
        } else {
            return TrainingExampleRegenerationType.NEW;
        }
    }

    protected void resetTrainingGenerationParams(TrainingMistakeWithScore trainingMistakeWithScore, Training nextTraining) {
        userVocabularyStatisticsService.createUserVocabularyStatistics(trainingMistakeWithScore);

        UserVocabulary userVocabulary = trainingMistakeWithScore.userVocabulary();

        userVocabulary.setLoading(false);
        userVocabulary.setNextTraining(nextTraining);
        userVocabularyDbService.save(userVocabulary);
    }

    @Transactional
    protected TrainingExample generateExamplesForTraining(Training nextTraining,
                                                          TrainingType newTrainingType,
                                                          WordTranslation wordTranslation) {
        log.debug("Generating new example with type: {}", newTrainingType);

        if (TrainingExampleService.isSentenceRequired(newTrainingType)) {

            boolean isReversed = TrainingExampleService.isReversedTraining(newTrainingType);
            Pair<TrainingExampleService.Example, TrainingExampleService.Example> examplePair = trainingExampleService.generateTrainingExample(wordTranslation, newTrainingType);

            wordExampleService.createAndSaveWordExample(examplePair.first().example(), examplePair.first().soundUrl(),
                    isReversed ? wordTranslation.getWordTo() : wordTranslation.getWordFrom());
            wordExampleService.createAndSaveWordExample(examplePair.second().example(), examplePair.second().soundUrl(),
                    isReversed ? wordTranslation.getWordFrom() : wordTranslation.getWordTo());

            Conjugation conjugation;
            try {
                conjugation = Objects.requireNonNull(wordTranslation.getWordFrom().getWordDictionary()).getConjugation();
            } catch (LazyInitializationException | NullPointerException e) {
                conjugation = null;
            }

            return trainingExampleService.createTrainingExampleAndSave(
                    wordTranslation,
                    nextTraining,
                    examplePair.first().example(),
                    examplePair.second().example(),
                    examplePair.first().soundUrl(),
                    TrainingExampleService.getTranslationDirection(newTrainingType),
                    newTrainingType,
                    Optional.ofNullable(conjugation)
            );
        } else {
            String wordFrom = wordTranslation.getWordFrom().getWord();
            String wordTo = wordTranslation.getWordTo().getWord();
            String soundUrlFrom = wordTranslation.getWordFrom().getSoundUrl();

            return trainingExampleService.createTrainingExampleAndSave(
                    wordTranslation,
                    nextTraining,
                    wordFrom,
                    wordTo,
                    soundUrlFrom,
                    TrainingExampleService.getTranslationDirection(newTrainingType),
                    newTrainingType,
                    Optional.empty()
            );
        }

    }

    private Map<TrainingType, Double> getWeights(TrainingMistakeWithScore trainingMistakeWithScore, TrainingType[] trainingTypes) {
        if (trainingTypes == null || trainingTypes.length == 0) {
            return Collections.emptyMap();
        }

        UserVocabulary userVocabulary = trainingMistakeWithScore.userVocabulary();
        Map<TrainingType, Double> weights = new EnumMap<>(TrainingType.class);
        List<UserVocabularyStatistics> lastMonthTrainingData = userVocabularyStatisticsService.findWordTrainingsAfter(
                userVocabulary,
                LocalDateTime.now().minusMonths(1)
        );
        List<TrainingResult> wordHistory = new ArrayList<>(getTrainingResults(lastMonthTrainingData));

        // adding the current training mistakes to the history
        LocalDateTime lastTrainingTime;
        if (wordHistory.isEmpty()) {
            lastTrainingTime = userVocabulary.getCreatedAt();
        } else {
            lastTrainingTime = wordHistory.getLast().trainingTime();
        }
        List<TrainingResult> lastTrainingResults = TrainingResult.from(
                trainingMistakeWithScore,
                Duration.between(LocalDateTime.now(), lastTrainingTime),
                lastTrainingTime,
                e -> trainingScoreFactory.getTrainingScore(e.trainingExampleData().mistakeData())
        );
        wordHistory.addAll(lastTrainingResults);

        for (TrainingType trainingType : trainingTypes) {
            ScoreAlgorithm scoreAlgorithm = new ScoreAlgorithm(wordHistory, trainingType, lastMonthTrainingData.size() + 1);

            weights.put(trainingType, scoreAlgorithm.calculateScore());
        }
        return weights;
    }


    private List<TrainingResult> getTrainingResults(List<UserVocabularyStatistics> recentTrainings) {
        if (recentTrainings == null || recentTrainings.isEmpty()) {
            return Collections.emptyList();
        }

        List<TrainingResult> trainingResults = new ArrayList<>();

        for (UserVocabularyStatistics statistics : recentTrainings) {
            if (statistics.getTraining().getTrainingExamples().isEmpty()) {
                continue;
            }

            List<TrainingResult> results = TrainingResult.from(
                    statistics,
                    trainingExample -> trainingScoreFactory.getTrainingScoreAndConvert(trainingExample.getTrainingExampleStatistics())
            );

            trainingResults.addAll(results);
        }


        return trainingResults;
    }

    @Transactional
    public Training copy(Training nextTraining) {
        if (nextTraining.getCopied()) {
            return nextTraining;
        }
        Training copy = Training.builder()
                .trainingExamples(new ArrayList<>())
                .completedAt(null)
                .copied(true)
                .createdAt(System.currentTimeMillis())
                .build();

        trainingRepository.save(copy);
        copy.setTrainingExamples(
                nextTraining.getTrainingExamples()
                        .stream()
                        .filter(e -> e != null && e.getTrainingExampleData() != null)
                        .map(e -> trainingExampleService.copy(e, copy))
                        .toList()
        );
        trainingRepository.save(copy);
        return copy;
    }
}
