import {VocabularyGroupActions, VocabularyGroupType} from "./store/vocabulary/vocabularyTypes";

export const VOCABULARY_GROUP_PERMISSION: Map<VocabularyGroupType, VocabularyGroupActions[]> = new Map([
	["PREDEFINED", []],
	["USER_DEFINED", ["DELETE_GROUP", "DELETE_WORD", "ADD_WORD", "TRAIN_GROUP", "EDIT_GROUP_NAME", "UPDATE_WORD"]],
	["DEFINED_BY_USER_FROM_PREDEFINED", ["DELETE_GROUP", "DELETE_WORD", "ADD_WORD", "TRAIN_GROUP", "EDIT_GROUP_NAME", "UPDATE_WORD"]]
]);
