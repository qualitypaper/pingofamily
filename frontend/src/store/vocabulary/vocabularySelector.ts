import { createSelector } from "reselect";
import { RootState } from "../../app/store";

const selectVocabularyReducer = (state: RootState) => state.vocabulary;

export const selectCurrentWords = (state: RootState) => state.vocabulary.currentVocabularyWords

export const selectCurrentlySelected = (state: RootState) => state.vocabulary.selected;

export const selectCurrentVocabularies = (state: RootState) => state.vocabulary.vocabularies;

export const selectCurrentlyInspectedWord = createSelector(
	[selectVocabularyReducer],
	(voc) => voc.currentlyInspectedWord,
)

export const selectCurrentlyInspectedWordGender = createSelector(
	[selectVocabularyReducer],
	(voc) => voc.currentlyInspectedWordGender,
)

export const selectSuggestedVocabularyGroups = (state: RootState) => state.vocabulary.suggestedVocabularyGroups

export const selectVocabularyGroup = createSelector(
	[selectVocabularyReducer],
	(voc) => voc.vocabularyGroup,
)

export const selectIsFetchedWordList = createSelector(
	[selectVocabularyReducer],
	(voc) => voc.isFetchedWordList
)
