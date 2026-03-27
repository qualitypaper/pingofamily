import { createSlice } from "@reduxjs/toolkit";
import { Language } from "../language/languageTypes";
import { UserVocabulary } from "../training/trainingTypes";
import { VocabularyGroup, VocabularyType, WordListWord, } from "./vocabularyTypes";

export const ADD_VOCABULARY_BUTTON: VocabularyType = {
	id: 5,
	learningLanguage: null,
	nativeLanguage: null,
	numberOfWords: -1,
	createdDate: "long",
	loading: false,
	vocabularyGroupList: [],
};

const addNewWord = (
	currentWords: WordListWord[],
	addWordType: WordListWord,
): WordListWord[] => {
	return [getDefaultWordListWord(addWordType), ...currentWords];
};

const addAlreadyCreatedWord = (
	currentWords: WordListWord[],
	wordList: WordListWord,
	vocabularyId: number,
	vocabularyGroupId: number,
): WordListWord[] => {
	return [
		...currentWords,
		{ ...wordList, addWordType: "INPUT", vocabularyId, vocabularyGroupId, loading: true },
	];
};

const getDefaultWordListWord = ({
	tempWordId,
	wordFrom,
	wordTo,
	vocabularyGroupId,
	vocabularyId,
}: WordListWord): WordListWord => {
	return {
		tempWordId,
		wordFrom: wordFrom,
		wordTo: wordTo,
		wordTranslationId: 0,
		vocabularyGroupId,
		vocabularyId,
		wordFromId: 0,
		wordToId: 0,
		userVocabularyId: 0,
		soundUrl: "",
		partOfSpeech: "OTHER",
		loading: true,
		createdAt: Date.now(),
	};
};

const removeWords = (
	currentWords: WordListWord[],
	wordToRemove: WordListWord,
) => {
	return currentWords.filter(
		(word) => word.userVocabularyId !== wordToRemove.userVocabularyId,
	);
};

const addVocabulary = (
	currentVocabularies: Array<VocabularyType>,
	vocabulary: VocabularyType,
) => {
	const last = currentVocabularies.pop()!;
	currentVocabularies.push(vocabulary);
	currentVocabularies.push(last);
	return currentVocabularies;
};

const filterVocabularies = (
	vocabularies: VocabularyType[],
): VocabularyType[] => {
	vocabularies
		.sort(
			(a, b) => a.vocabularyGroupList?.length - b.vocabularyGroupList?.length,
		)
		.reverse();

	if (vocabularies.includes(ADD_VOCABULARY_BUTTON)) return vocabularies;

	vocabularies.push(ADD_VOCABULARY_BUTTON);
	return vocabularies;
};

const replaceTempIdWord = (
	userVocabulary: WordListWord,
	allWords: WordListWord[],
): WordListWord[] => {
	const newArr: WordListWord[] = [];

	for (let word of allWords) {
		if (
			(word.tempWordId === userVocabulary.tempWordId ||
				(word.wordFrom === userVocabulary.wordFrom &&
					word.wordTo === userVocabulary.wordTo)) &&
			word.loading
		)
			continue;
		else newArr.push(word);
	}
	newArr.push(userVocabulary);

	return newArr;
};

const removeVocabularyFromState = (
	vocabularyId: number,
	vocabularies: VocabularyType[],
): VocabularyType[] => {
	return vocabularies.filter((vocabulary) => vocabulary.id !== vocabularyId);
};

export type VocabularyState = {
	readonly currentlyInspectedWord: UserVocabulary | null;
	readonly currentlyInspectedWordGender: string;
	readonly currentVocabularyWords: WordListWord[];
	readonly allWords: WordListWord[];
	readonly selected: VocabularyType;
	readonly vocabularies: VocabularyType[];
	readonly suggestedVocabularyGroups: VocabularyGroup[];
	readonly vocabularyGroup: VocabularyGroup;
	readonly isFetchedWordList: boolean;
};

export const VOCABULARY_INITIAL_STATE: VocabularyState = {
	currentlyInspectedWord: {
		createdAt: Date.now(),
	},
	currentlyInspectedWordGender: "",
	currentVocabularyWords: [],
	allWords: [],
	selected: {
		id: 0,
		createdDate: "",
		learningLanguage: null,
		nativeLanguage: null,
		numberOfWords: 0,
		loading: false,
		vocabularyGroupList: [],
	},
	vocabularies: [],
	suggestedVocabularyGroups: [],
	vocabularyGroup: {
		groupId: 0,
	},
	isFetchedWordList: false,
};

function addVocabularyGroup(
	vocabularyGroup: VocabularyGroup,
	vocabulary: VocabularyType,
) {
	const temp = { ...vocabulary };
	temp.vocabularyGroupList = [
		...(temp.vocabularyGroupList ?? []),
		vocabularyGroup,
	];
	return temp;
}

function removeVocabularyGroup(
	vocabularyGroup: VocabularyGroup,
	vocabulary: VocabularyType,
) {
	const temp = { ...vocabulary };
	temp.vocabularyGroupList = vocabulary.vocabularyGroupList.filter(
		(group) => +group.groupId !== +vocabularyGroup.groupId,
	);
	return temp;
}

function removeFromSuggestedVocabularyGroups(
	suggestedVocabularyGroups: VocabularyGroup[],
	payload: VocabularyGroup,
) {
	return suggestedVocabularyGroups.filter((e) => e.groupId !== payload.groupId);
}

function setCurrentWordsWithTempId(
	payload: WordListWord[],
	currentVocabularyWords: WordListWord[],
): WordListWord[] {
	if (!currentVocabularyWords) return payload;
	const result = [...payload, ...currentVocabularyWords];

	const visited: Set<number> = new Set();

	console.log(result.length)

	return result
		.filter(e => {

			if (e.loading) {
				const isValid = !!(e.createdAt && e.createdAt + 1000 * 60 * 1.1 > Date.now());
				if (!visited.has(e.userVocabularyId ?? Math.random())) {
					visited.add(e.userVocabularyId ?? Math.random());
					return isValid;
				}

				debugger
				if (e.addWordType === "INPUT") {
					return false;
				} else if (e.addWordType === "PARAMETER_CHANGE") {
					// HARD-CODE
					// after 20 seconds of parameter change show the backend variant
					return !!(e.createdAt && e.createdAt + 1000 * 60 * 0.3 > Date.now());
				}

				return isValid;
			}

			if (visited.has(e.userVocabularyId ?? Math.random())) return false;

			visited.add(e.userVocabularyId ?? Math.random());
			return true;
		})
		.sort((a, b) => b.loading ? 1 : 0 - (a.loading ? 1 : 0));
}

function setCurrentWordsWordWithId(
	previousId: number,
	newUserVocabulary: WordListWord,
	currentVocabularyWords: WordListWord[],
): WordListWord[] {
	return currentVocabularyWords.map(e => {
		if (e.userVocabularyId === previousId) {
			return { ...newUserVocabulary }
		} else {
			return { ...e }
		}
	})
}

function setWordLoadingById(
	currentWords: WordListWord[],
	userVocabularyId: number,
) {
	const arr = currentWords.map((word) =>
		word.userVocabularyId === userVocabularyId
			? {
				...word,
				loading: true,
			}
			: { ...word },
	);

	return arr;
}

const GERMAN_GENDER_ARTICLES = { m: "der", f: "die", n: "das" };
const SPANISH_GENDER_ARTICLES = { m: "el", f: "la", n: "el" };

export function mapWordGender(
	learningLanguage: Language | null,
	str: string,
): string {
	if (learningLanguage === null || str.length === 0) return "";
	const gender: string = str[0];

	if (learningLanguage === "GERMAN") {
		// @ts-ignore
		return GERMAN_GENDER_ARTICLES[gender] ?? "";
	} else if (learningLanguage === "SPANISH") {
		// @ts-ignore
		return SPANISH_GENDER_ARTICLES[gender] ?? "";
	}

	return "";
}

function setSuggestedGroups(payload: VocabularyGroup[], vocabulary: VocabularyType) {
	return payload.filter(vg => {
		return !(!!vocabulary?.vocabularyGroupList?.find(e => e?.name === vg.name && e?.type === "DEFINED_BY_USER_FROM_PREDEFINED"));
	}) ?? []
}

export const vocabularySlice = createSlice({
	name: "vocabulary",
	initialState: VOCABULARY_INITIAL_STATE,
	reducers: {
		setIsFetchedWordList(state, action) {
			state.isFetchedWordList = action.payload;
		},
		addAlreadyCreated(state, action) {
			state.currentVocabularyWords = addAlreadyCreatedWord(
				state.currentVocabularyWords,
				action.payload.wordListWord,
				action.payload.vocabularyId,
				action.payload.vocabularyGroupId,
			);
		},
		addWord(state, action) {
			state.currentVocabularyWords = addNewWord(
				state.currentVocabularyWords,
				action.payload,
			);
		},
		removeWord(state, action) {
			state.currentVocabularyWords = removeWords(
				state.currentVocabularyWords,
				action.payload,
			);
		},
		setCurrentlySelectedVocabulary(state, action) {
			state.selected = VOCABULARY_INITIAL_STATE.selected;
			state.selected = action.payload;
		},
		setCurrentWords(state, action) {
			state.currentVocabularyWords = setCurrentWordsWithTempId(
				action.payload,
				state.currentVocabularyWords,
			);
		},
		resetWords(state) {
			state.currentVocabularyWords = []
		},
		setVocabularies(state, action) {
			state.vocabularies = filterVocabularies(action.payload);
		},
		setCurrentlyInspectedWord(state, action) {
			state.currentlyInspectedWord = action.payload;
		},
		setCurrentlyInspectedWordGender(state, action) {
			state.currentlyInspectedWordGender = mapWordGender(
				state.selected.learningLanguage,
				action.payload,
			);
		},
		createVocabulary(state, action) {
			state.vocabularies = addVocabulary(state.vocabularies, action.payload);
		},
		updateTempIdWord(state, action) {
			state.currentVocabularyWords = replaceTempIdWord(
				action.payload,
				state.currentVocabularyWords,
			);
		},
		removeVocabulary(state, action) {
			state.vocabularies = removeVocabularyFromState(
				action.payload,
				state.vocabularies,
			);
		},
		createVocabularyGroupR(state, action) {
			state.selected = addVocabularyGroup(action.payload, state.selected);
		},
		deleteVocabularyGroupSlice(state, action) {
			const newVoc = removeVocabularyGroup(action.payload, state.selected);
			state.selected = newVoc;
			state.vocabularies = state.vocabularies.map((voc) =>
				voc.id === newVoc.id ? newVoc : voc,
			);
		},
		clearVocabularySlice(state) {
			state.allWords = VOCABULARY_INITIAL_STATE.allWords;
			state.currentVocabularyWords =
				VOCABULARY_INITIAL_STATE.currentVocabularyWords;
			state.currentlyInspectedWord =
				VOCABULARY_INITIAL_STATE.currentlyInspectedWord;
			state.selected = VOCABULARY_INITIAL_STATE.selected;
			state.suggestedVocabularyGroups =
				VOCABULARY_INITIAL_STATE.suggestedVocabularyGroups;
			state.vocabularyGroup = VOCABULARY_INITIAL_STATE.vocabularyGroup;
		},
		setSuggestedVocabularyGroups(state, action) {
			if (action.type !== "REHYDRATION") {
				state.suggestedVocabularyGroups = setSuggestedGroups(action.payload, state.selected);
			}
		},
		removeSuggestedVocabularyGroup(state, action) {
			state.suggestedVocabularyGroups = removeFromSuggestedVocabularyGroups(
				state.suggestedVocabularyGroups,
				action.payload,
			);
		},
		setVocabularyGroup(state, action) {
			state.vocabularyGroup = action.payload;
		},
		changeWordParameters(state, action) {
			state.currentVocabularyWords = setCurrentWordsWordWithId(
				action.payload.previousUserVocabularyId,
				action.payload.newUserVocabulary,
				state.currentVocabularyWords,
			);
		},
		setWordLoading(state, action) {
			state.currentVocabularyWords = setWordLoadingById(
				state.currentVocabularyWords,
				action.payload.userVocabularyId,
			);
		},
	},
});

export const {
	addAlreadyCreated,
	addWord,
	removeWord,
	setCurrentlySelectedVocabulary,
	setCurrentWords,
	setVocabularies,
	createVocabulary,
	setCurrentlyInspectedWord,
	updateTempIdWord,
	removeVocabulary,
	clearVocabularySlice,
	createVocabularyGroupR,
	deleteVocabularyGroupSlice,
	setSuggestedVocabularyGroups,
	removeSuggestedVocabularyGroup,
	setVocabularyGroup,
	changeWordParameters,
	setWordLoading,
	setCurrentlyInspectedWordGender,
	setIsFetchedWordList,
	resetWords
} = vocabularySlice.actions;

export const vocabularyReducer = vocabularySlice.reducer;


