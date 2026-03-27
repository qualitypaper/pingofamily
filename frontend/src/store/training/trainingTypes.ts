export type PartOfSpeech = "NOUN" | "VERB" | "ADJECTIVE" | "OTHER" | "PROPN";

export type TrainingType =
	| "PHRASE_CONSTRUCTION"
	| "PHRASE_CONSTRUCTION_REVERSED"
	| "COMPLETE_EMPTY_SPACES"
	| "AUDIO"
	| "SENTENCE_AUDIO"
	| "SENTENCE_TYPE"
	| "TRANSLATION"
	| "DECLENSION_OF_VERB";

export type WordStatus = "LEARNED" | "REPEAT" | "NEW" | "IN_PROCESS";

export type WordType =
	| "WORD"
	| "SENTENCE"
	| "SPREAD_VERB"
	| "PHRASE"
	| "GERMAN_PHRASAL_VERB"
	| "PHRASAL_VERB";

export interface Declension {
	normative: string;
	genitive: string;
	dative: string;
	accusative: string;
}

export interface UserVocabulary {
	tempWordId?: string;
	userVocabularyId?: number;
	wordTranslation?: WordTranslation;
	wordExampleTranslation?: WordExampleTranslation;
	loading?: boolean;
	status?: WordStatus;
	vocabularyGroupId?: number;
	// used exclusively for new words that haven't been loaded yet
	createdAt: number;
}

export interface WordExampleTranslation {
	id?: number | string;
	example?: string;
	exampleTranslation?: string;
	soundUrl?: string;
	wordId?: number | string;
}

export interface WordDictionary {
	description: string;
	descriptionTranslation: string;
	synonyms: string[];
	conjugation: IConjugation;
}

export interface IConjugation {
	id: number | string;
	infinitive: string;
	conjugation: IMappings;
}

export interface IMappings {
	gender?: string;
	mappings: {};
}

export interface IWord {
	id: number | string;
	word: string;
	language: string;
	partOfSpeech: PartOfSpeech;
	imageUrl: string;
	soundUrl: string;
	wordDictionary: WordDictionary;
	wordType: WordType;
	declension: Declension;
}

export interface WordTranslation {
	id?: number | string;
	wordFrom: IWord;
	wordTo: IWord;
}

export interface TrainingExample {
	id: number;
	sentence: string;
	sentenceTranslation: string;
	formattedString: string;
	wordsTranslation: {};
	soundUrl: string;
	trainingType: TrainingType;
	identifiedWord: string;
	declension: Declension;
}

export interface TrainingI {
	trainingId: number;
	wordTranslation: WordTranslation;
	trainingExampleList: TrainingExample[];
}

export class TrainingExampleWithIndex {
	constructor(
		public index: number,
		public trainingExample: TrainingExample,
		public trainingId: number,
		public hint: boolean = false,
		public skipped: boolean = false,
		public timestamp: Date = new Date()
	) {
	};

	// isEqual(other: TrainingExampleWithIndex) {
	//   return this.trainingExample.sentence === other.trainingExample.sentence &&
	//     this.trainingExample.identifiedWord === other.trainingExample.identifiedWord &&
	//     this.trainingExample.trainingType === other.trainingExample.trainingType;
	// }
}

export type TrainingWord = {
	learningSessionId: number;
	trainingExamples: TrainingI[];
}

export type TrainingState = {
	readonly trainingVocabularyGroupId: number;
	readonly currentlyTrainedWords: TrainingWord
	readonly finalTrainingSequence: TrainingExampleWithIndex[];
	readonly mistakesCount: number;
	readonly hintsCount: number;
	readonly finalSequenceIndex: number;
	readonly totalExercisesCount: number;
};
