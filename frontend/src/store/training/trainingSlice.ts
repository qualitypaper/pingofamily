import { createSlice } from "@reduxjs/toolkit";
import { setHintStatus, setSkippedStatus } from "./trainingFunctions";
import { TrainingState, TrainingType } from "./trainingTypes";

export const DEFAULT_TRAINING_TYPE_SEQUENCE: TrainingType[] = [
  "TRANSLATION", "AUDIO", "COMPLETE_EMPTY_SPACES",
  "PHRASE_CONSTRUCTION", "PHRASE_CONSTRUCTION_REVERSED",
  "SENTENCE_TYPE", "SENTENCE_AUDIO"
]

export const DEVELOPMENT_TRAINING_TYPE_SEQUENCE: TrainingType[] = [
  "COMPLETE_EMPTY_SPACES", "TRANSLATION", "AUDIO",
  "PHRASE_CONSTRUCTION", "PHRASE_CONSTRUCTION_REVERSED",
  "SENTENCE_TYPE", "SENTENCE_AUDIO"
]

export const createTrainingInitialState = (): TrainingState => ({
  trainingVocabularyGroupId: 0,
  currentlyTrainedWords: {
    learningSessionId: 0,
    trainingExamples: [],
  },
  finalTrainingSequence: [],
  mistakesCount: 0,
  hintsCount: 0,
  finalSequenceIndex: 0,
  totalExercisesCount: 0,
});

export const TrainingInitialState: TrainingState = createTrainingInitialState();

export const userSlice = createSlice({
  name: "training",
  initialState: TrainingInitialState,
  reducers: {
    setCurrentlyTrainedWords(state, action) {
      state.currentlyTrainedWords = action.payload;
    },
    setTrainingVocabularyGroupId(state, action) {
      state.trainingVocabularyGroupId = action.payload;
    },
    setFinalTrainingSequence(state, action) {
      state.finalTrainingSequence = action.payload
    },
    incrementFinalSequenceIndex(state) {
      state.finalSequenceIndex += 1;
    },
    incrementHintsNumber(state, action) {
      state.hintsCount += 1;
      state.finalTrainingSequence = setHintStatus(
        state.finalTrainingSequence,
        action.payload.index,
        action.payload.trainingType,
      );
    },
    incrementMistakeNumber(state, action) {
      state.mistakesCount += 1;
      state.finalTrainingSequence = setSkippedStatus(
        state.finalTrainingSequence,
        action.payload.index,
        action.payload.trainingType,
      );
    },
    incrementTotalExercisesCount(state) {
      state.totalExercisesCount += 1;
    },
    addMistakeToTrainingSequence(state, action) {
      state.finalTrainingSequence = [...state.finalTrainingSequence, action.payload];
    },
    clearTrainingCache() {
      return createTrainingInitialState();
    },
    setFinalTrainingIndex(state, action) {
      state.finalSequenceIndex = action.payload;
    },
  },
});

export const trainingReducer = userSlice.reducer;

export const {
  setTrainingVocabularyGroupId,
  setCurrentlyTrainedWords,
  setFinalTrainingSequence,
  addMistakeToTrainingSequence,
  incrementFinalSequenceIndex,
  incrementHintsNumber,
  incrementMistakeNumber,
  incrementTotalExercisesCount,
  clearTrainingCache,
} = userSlice.actions;
