import { createSelector } from "reselect";
import { RootState } from "../../app/store";

const selectTrainingReducer = (state: RootState) => state.training;

export const selectCurrentlyTrainedWords = createSelector(
	[selectTrainingReducer],
	(training) => training.currentlyTrainedWords,
)

export const selectFinalTrainingSequence = createSelector(
	[selectTrainingReducer],
	(training) => training.finalTrainingSequence,
)

export const selectFinalSequenceIndex = createSelector(
	[selectTrainingReducer],
	(training) => training.finalSequenceIndex,
)

export const selectHintsCount = createSelector(
	[selectTrainingReducer],
	(training) => training.hintsCount,
)

export const selectMistakesCount = createSelector(
	[selectTrainingReducer],
	(training) => training.mistakesCount,
)

export const selectTotalExercisesCount = createSelector(
	[selectTrainingReducer],
	(training) => training.totalExercisesCount,
)

export const selectTrainingVocabularyGroupId = createSelector(
	[selectTrainingReducer],
	(training) => training.trainingVocabularyGroupId
)