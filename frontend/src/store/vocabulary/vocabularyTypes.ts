import { Language } from "../language/languageTypes";
import { PartOfSpeech } from "../training/trainingTypes";

export type VocabularyGroupActions =
	| "DELETE_GROUP"
	| "TRAIN_GROUP"
	| "EDIT_GROUP_NAME"
	| "ADD_WORD"
	| "DELETE_WORD"
	| "UPDATE_WORD";

export type VocabularyGroupType = "PREDEFINED" | "USER_DEFINED" | "DEFINED_BY_USER_FROM_PREDEFINED";
export type AddWordType = "INPUT" | "PARAMETER_CHANGE";

export type WordListWord = {
	tempWordId?: string;
	userVocabularyId?: number;
	vocabularyId?: number;
	vocabularyGroupId?: number;
	wordTranslationId?: number;
	wordFromId?: number;
	wordToId?: number;
	wordFrom: string;
	wordTo: string;
	partOfSpeech: PartOfSpeech;
	soundUrl?: string;
	loading?: boolean;
	createdAt?: number;
	addWordType?: AddWordType;
};

export type VocabularyGroup = {
	groupId: number;
	name?: string;
	wordsNumber?: number;
	imageUrl?: string;
	vocabulary?: VocabularyType;
	loading?: boolean;
	learningLanguage?: Language;
	nativeLanguage?: Language;
	type?: VocabularyGroupType;
};

export type VocabularyType = {
	id: number;
	learningLanguage: Language | null;
	nativeLanguage: Language | null;
	numberOfWords: number | string;
	createdDate: string;
	loading: boolean;
	vocabularyGroupList: VocabularyGroup[];
};

export class TranslationJson {
	translation: string;
	pos: PartOfSpeech;
	language: Language;

	public constructor(
		translation: string,
		pos: PartOfSpeech,
		language: Language = "ENGLISH",
	) {
		this.language = language;
		this.pos = pos;
		this.translation = translation;
	}
}