import { LANGUAGES } from "constant";
import { API } from "../app/init";
import { store } from "../app/store";
import { Difficulty } from "../custom";
import { showNotification } from "../store/headerSlice";
import { Language } from "../store/language/languageTypes";
import {
  PartOfSpeech,
  WordExampleTranslation,
  WordTranslation,
} from "../store/training/trainingTypes";
import { addAlreadyCreated, addWord } from "../store/vocabulary/vocabularySlice";
import { TranslationJson, VocabularyType, WordListWord } from "../store/vocabulary/vocabularyTypes";
import { getRandomUUID, getShortLanguageName } from "./globalUtils";

export type PossibleTranslationType = {
  word: string;
  targetLanguage: string;
  sourceLanguage: string;
  wordLimit: number;
  isAutoComplete: boolean;
};

export type PossibleTranslationResponse = {
  possibleTranslations?: TranslationJson[];
  lemma?: LemmaResponse;
};

export type LemmaResponse = {
  lemma: string;
  pos: PartOfSpeech;
};

export type AddWord = {
  tempId: string;
  word: string;
  vocabularyId: number;
  vocabularyGroupId: number;
  wordTranslation: {
    translation: string;
    pos: PartOfSpeech;
  };
};

export type ResponseStatus = "ALREADY_CREATED" | "NEW" | "FAIL" | "EXISTS";

export type UserVocabularyResponseType = {
  tempWordId: string;
  status: ResponseStatus;
  userVocabularyId?: number;
  wordTranslation?: WordTranslation;
  wordExampleTranslation?: WordExampleTranslation;
  userId?: number;
  error?: string;
  vocabularyGroupIdList?: number[];
};

const token = store.getState().user.tokenPair.accessToken;

export const distinctTranslations = (arr: TranslationJson[]) => {
  const visited: TranslationJson[] = [];

  for (let e of arr) {
    const find = visited.find((v) => v.translation === e.translation);
    if (!find) {
      visited.push(e);
    }
  }

  return visited;
};

export const distinct = (arr: string[]): string[] => {
  const map = new Map<string, string>();

  for (let s of arr) {
    if (!map.has(s.toLowerCase())) {
      map.set(s.toLowerCase(), s);
    }
  }
  return Array.from(map.values()).sort((a, b) => a.localeCompare(b));
};

export const fetchWord = async (wordId: number) => {
  return await API.get(`/user-vocabulary/get-word/${wordId}`);
};

export const fetchWordsByVocabulary = async (vocabularyId: number) => {
  return await API.get(`/user-vocabulary/get-words/${vocabularyId}`);
};

export const fetchWordsByVocabularyNGroup = async (
  vocabularyId: number,
  vocabularyGroupId: number,
) => {
  if (!vocabularyGroupId || !token) return [];

  return await API.get(
    `/user-vocabulary/get-words/${vocabularyId}?vocabularyGroupId=${vocabularyGroupId}`,
  );
};

export const fetchWordsByVocabularyGroupId = async (vocabularyGroupId: number) => {
  return await API.get(`/user-vocabulary/get-words?vocabularyGroupId=${vocabularyGroupId}`);
};

export const fetchVocabularies = async () => {
  return await API.get(`/vocabulary/get-all-vocabularies`);
};

export const fetchSuggestedVocabularyGroups = async (
  learningLanguage: string,
  nativeLanguage: string,
) => {
  return await API.get(
    `/vocabulary-group/get-suggested?learningLanguage=${learningLanguage.toUpperCase()}&nativeLanguage=${nativeLanguage}`,
  );
};


export const createVocabularyRequest = async (vocabulary: {
  learningLanguage: string;
  nativeLanguage: string;
}) => {
  return await API.post(`/vocabulary/create`, vocabulary);
};

export const createVocabularyGroup = async (vocabularyGroup: {
  vocabularyId: number;
  name: string;
}) => {
  return await API.post(`/vocabulary-group/create`, vocabularyGroup);
};

export const deleteVocabularyGroup = async (vocabularyId: number) => {
  return await API.delete(`/vocabulary-group/delete/${vocabularyId}`);
};

export const getPossibleTranslations = async (
  wordInformation: PossibleTranslationType,
): Promise<PossibleTranslationResponse> => {
  return (await API.post(`/translation/get-possible-translations`, wordInformation)).data;
};

export const addWordToVocabulary = async (
  addWord: AddWord,
): Promise<UserVocabularyResponseType> => {
  return (await API.post(`/user-vocabulary/add-word`, addWord)).data;
};

export const deleteWordFromVocabulary = async (
  wordId?: number | string,
  vocabularyGroupId?: number,
) => {
  return await API.delete(
    `/user-vocabulary/delete-word/${wordId ?? "-1"}?vocabularyGroupId=${vocabularyGroupId}`,
  );
};

export const getAutocomplete = async (
  str: string,
  sourceLanguage: Language,
  targetLanguage: Language,
) => {
  return await API.get(
    `/translation/autocomplete?str=${str}&sourceLanguage=${sourceLanguage}&targetLanguage=${targetLanguage}`,
  );
};

export const deleteVocabulary = async (vocabularyId: number) => {
  return await API.delete(`/vocabulary/delete-vocabulary/${vocabularyId}`);
};

export const updateLastPickedVocabulary = async (vocabularyId: number) => {
  return await API.get(`/user/update-last-picked-vocabulary/${vocabularyId}`);
};

export const createFromPredefinedVocabularyGroup = async (
  vocabularyId: number,
  vocabularyGroupId: number,
) => {
  return await API.post(`/vocabulary-group/create-from-predefined`, {
    vocabularyId,
    vocabularyGroupId,
  });
};

export const changeTranslation = async (
  userVocabularyId: number,
  translation: string,
  pos: PartOfSpeech,
  vocabularyGroupId: number,
) => {
  return await API.post("/user-vocabulary/change-translation", {
    id: userVocabularyId,
    translation,
    partOfSpeech: pos.toUpperCase(),
    vocabularyGroupId,
  });
};

export const regenerateExamples = async (
  userVocabularyId: number,
  vocabularyGroupId: number,
  difficulty: Difficulty,
) => {
  return await API.post(`/user-vocabulary/regenerate-examples`, {
    userVocabularyId,
    vocabularyGroupId,
    difficulty,
  });
};

export const handleTranslationPickUtil = async (
  addWordField: string,
  vocabulary: VocabularyType,
  translationJson: TranslationJson,
  vocabularyGroupId: string,
) => {
  if (!addWordField) return;

  if (translationJson.pos === "PROPN") {
    translationJson.pos = "NOUN";
  }

  const tempId = getRandomUUID();
  const vGid = +(vocabularyGroupId ?? "-1");

  const addWordData: AddWord = {
    tempId,
    word: addWordField,
    vocabularyId: vocabulary.id,
    vocabularyGroupId: vGid,
    wordTranslation: translationJson,
  };

  const res = await addWordToVocabulary(addWordData);

  switch (res.status) {
    case "EXISTS": {
      store.dispatch(
        showNotification({
          message: "Word already exists in vocabulary",
          status: 0,
        }),
      );
      break;
    }
    case "FAIL": {
      store.dispatch(
        showNotification({
          message: "Failed to add word",
          status: 1,
        }),
      );
      break;
    }
    case "ALREADY_CREATED": {
      store.dispatch(
        addAlreadyCreated({
          wordListWord: res,
          vocabularyId: vocabulary.id,
          vocabularyGroupId: vGid,
        }),
      );
      break;
    }
    case "NEW": {
      const data: WordListWord = {
        tempWordId: tempId,
        wordFrom: addWordField,
        wordTo: translationJson.translation,
        vocabularyId: vocabulary.id,
        vocabularyGroupId: vGid,
        loading: true,
        partOfSpeech: translationJson.pos,
        addWordType: "INPUT"
      };

      store.dispatch(addWord(data));
    }
  }
};

export const fetchUserVocabularies = async () => {
  return (await API.get("/vocabulary/get-all-vocabularies"))?.data;
};

export function mapDefaultVocabularyName(lang: string): string {
  switch (lang) {
    case "en":
      return "vocabulary";
    case "de":
      return "Wortschatz";
    case "ru":
      return "словарь";
    case "es":
      return "vocabulario";
    default:
      return "VOCABULARY";
  }
}

export function markWithLanguage(data: any[], language: Language) {
  return data.map((e) => {
    if (typeof e === "object") {
      if ((e as []).length === undefined) {
        return {
          language,
          elem: { ...(e as object) },
        };
      } else {
        return {
          language,
          elem: e,
        };
      }
    } else {
      return {
        language,
        elem: e,
      };
    }
  });
}

export function removeUnknownTranslation(
  data: TranslationJson[],
  word: string,
  language?: Language,
) {
  return data.filter(
    (e) => !(e.translation === word && (language ? e.language === language : false)),
  );
}

export function mapVocabularyGroupName(vocabularyGroupName: string, currLang: Language): string {
  return vocabularyGroupName.includes("VOCABULARY")
    ? vocabularyGroupName.substring(0, 1).toUpperCase() +
    vocabularyGroupName.substring(1, vocabularyGroupName.indexOf("VOCABULARY")) +
    mapDefaultVocabularyName(getShortLanguageName(currLang))
    : vocabularyGroupName
}

export async function getMultiLingualSuggestedGroups(nativeLanguage: Language = "ENGLISH") {
  const remainingLanguages = LANGUAGES.filter((e) => e !== nativeLanguage);
  let finalList: any[] = [];

  for (let lang of remainingLanguages) {
    let suggestedGroups;
    try {
      const res = await fetchSuggestedVocabularyGroups(lang, nativeLanguage);
      if (!res || !res.data) suggestedGroups = [];
      else suggestedGroups = res.data;
    } catch (e) {
      suggestedGroups = [];
    }

    const mapped = suggestedGroups?.map((x: object) => ({
      ...x,
      learningLanguage: lang,
      nativeLanguage,
    }));

    finalList = [...finalList, ...mapped];
  }

  return finalList;
}