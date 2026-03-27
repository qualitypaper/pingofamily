import { ChangeEvent, useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch } from "react-redux";
import { Link } from "react-router-dom";
import { POSSIBLE_TRANSLATIONS_WORD_LIMIT } from "../../../constant";
import i18n from "../../../i18nf/i18n";
import { ReactComponent as PlusCircleIcon } from "assets/icons/plus-circle.svg";
import { showNotification } from "../../../store/headerSlice";
import { Language } from "../../../store/language/languageTypes";
import {
  TranslationJson,
  VocabularyType,
  WordListWord,
} from "../../../store/vocabulary/vocabularyTypes";
import {
  distinctTranslations,
  getPossibleTranslations,
  handleTranslationPickUtil,
  markWithLanguage,
  PossibleTranslationResponse,
  removeUnknownTranslation as removeUnknownTranslations,
} from "../../../utils/vocabularyUtils";
import WordListInput from "./WordListInput";
import { API } from "../../../app/init";
import { Button } from "@chakra-ui/react";
import { usePrerenderSize } from "../../../hooks/usePrerenderSize";

export type WordListAddWordInputProps = {
  vocabulary: VocabularyType;
  currentVocabularyWords: WordListWord[];
  vocabularyGroupId?: string;
};

export class AutoCompleteOption {
  elem: string = "";
  language: Language = "ENGLISH";

  public constructor(option: string, language: Language) {
    this.elem = option;
    this.language = language;
  }
}

export type TranslationOption = {
  elem: TranslationJson;
  language: Language;
};

const WordListAddWordInput = ({ vocabulary, vocabularyGroupId }: WordListAddWordInputProps) => {
  const dispatch = useDispatch();

  const [addWordField, setAddWordField] = useState<string>("");
  const [options, setOptions] = useState<TranslationOption[]>([]);
  const [openInput, setOpenInput] = useState<boolean>(false);
  const [isPossibleTranslationsResult, setIsPossibleTranslationsResult] = useState<boolean>(false);
  const [autoCompleteOptions, setAutoCompleteOptions] = useState<AutoCompleteOption[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [optionsLoading, setOptionsLoading] = useState<boolean>(false);

  const dropdownRef = useRef<HTMLDivElement | null>(null);
  const inputDivRef = useRef<HTMLDivElement | null>(null);

  const { t } = useTranslation();

  const isMobile = window.innerWidth <= 768;

  // DEBOUNCE
  useEffect(() => {
    if (!addWordField || isPossibleTranslationsResult) {
      API.finishPendingRequestsLike("/vocabulary/autocomplete");
      return;
    }

    const handleInputChange = async (value: string) => {
      if (!value || !vocabulary.learningLanguage || !vocabulary.nativeLanguage || loading) {
        return;
      }
      setOptionsLoading(true);
      setIsPossibleTranslationsResult(false);

      try {
        API.finishPendingRequestsLike("/vocabulary/autocomplete");

        const res = await API.get(
          `/translation/autocomplete?str=${value}&sourceLanguage=${vocabulary.learningLanguage}&targetLanguage=${vocabulary.nativeLanguage}`,
        );
        const reverseRes = await API.get(
          `/translation/autocomplete?str=${value}&sourceLanguage=${vocabulary.nativeLanguage}&targetLanguage=${vocabulary.learningLanguage}`,
        );

        if (res) {
          const temp = markWithLanguage(res.data, vocabulary.learningLanguage);
          // @ts-ignore
          setAutoCompleteOptions(temp);
        }
        if (reverseRes) {
          const temp = markWithLanguage(reverseRes.data, vocabulary.nativeLanguage);
          // @ts-ignore
          setAutoCompleteOptions((prev) => [...prev, ...temp]);
        }
      } catch (e) {
        console.error(e);
      }
      setOptionsLoading(false);
    };
    let debouncer = setTimeout(() => {
      handleInputChange(addWordField).then((r) => r);
    }, 500);

    return () => {
      clearTimeout(debouncer);
    };
  }, [
    addWordField,
    isPossibleTranslationsResult,
    loading,
    vocabulary.learningLanguage,
    vocabulary.nativeLanguage,
  ]);

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setOpenInput(false);
        setOptions([]);
        setAutoCompleteOptions([]);
        setLoading(false);
        setAddWordField("");
        setIsPossibleTranslationsResult(false);
      }
    }

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const getTranslations = useCallback(
    async (newWord: string, isAutoComplete: boolean = false, isDirect: boolean = true) => {
      if (!newWord) {
        dispatch(showNotification({ message: t("AddWordFieldEmpty"), status: 2 }));
        return;
      }

      setLoading(true);
      API.finishPendingRequestsLike("autocomplete");
      setAutoCompleteOptions([]);

      const temptrDirection = isDirect ? "INITIAL" : "REVERSED";

      const tempTargetLanguage =
        temptrDirection === "INITIAL" ? vocabulary.nativeLanguage : vocabulary.learningLanguage;
      const tempSourceLanguage =
        temptrDirection === "INITIAL" ? vocabulary.learningLanguage : vocabulary.nativeLanguage;

      const possibleTranslations = await getPossibleTranslations({
        word: newWord,
        wordLimit: POSSIBLE_TRANSLATIONS_WORD_LIMIT,
        targetLanguage: tempTargetLanguage as Language,
        sourceLanguage: tempSourceLanguage as Language,
        isAutoComplete,
      });
      const distincted = distinctTranslations(possibleTranslations?.possibleTranslations || []).map(
        (e) => ({
          ...e,
          language: tempTargetLanguage as Language,
        }),
      );
      const filteredDistincted = removeUnknownTranslations(
        distincted,
        newWord,
        tempTargetLanguage as Language,
      );

      let reversePossibleTranslations: PossibleTranslationResponse = {};
      if (!isAutoComplete) {
        reversePossibleTranslations = await getPossibleTranslations({
          word: newWord,
          wordLimit: POSSIBLE_TRANSLATIONS_WORD_LIMIT,
          targetLanguage: tempSourceLanguage as Language,
          sourceLanguage: tempTargetLanguage as Language,
          isAutoComplete,
        });

        if (filteredDistincted.length > 0) {
          reversePossibleTranslations.possibleTranslations =
            reversePossibleTranslations?.possibleTranslations?.filter(
              (e) => newWord !== e.translation,
            );
        }
      }

      const reverseDistinct = distinctTranslations(
        reversePossibleTranslations?.possibleTranslations || [],
      ).map((e) => ({
        ...e,
        language: tempSourceLanguage as Language,
      }));

      const filteredReverseDistinct = removeUnknownTranslations(
        reverseDistinct,
        newWord,
        tempTargetLanguage as Language,
      );

      const options = [
        ...markWithLanguage(filteredDistincted, tempTargetLanguage as Language),
        ...markWithLanguage(filteredReverseDistinct, tempSourceLanguage as Language),
      ];

      setOptions(options);
      setIsPossibleTranslationsResult(true);
      setLoading(false);
    },
    [dispatch, t, vocabulary.learningLanguage, vocabulary.nativeLanguage],
  );

  const addWord = useCallback(async (option: TranslationOption) => {
      clearInput();
      setOptions([]);
      setIsPossibleTranslationsResult(false);
      setAutoCompleteOptions([]);
      setOpenInput(false);
      const translation = option.elem;
      const translationDirection =
        option.language === vocabulary.learningLanguage ? "REVERSED" : "INITIAL";

      let word;
      let wordTranslation;
      if (translationDirection === "INITIAL") {
        word = addWordField;
        wordTranslation = translation;
      } else {
        word = translation.translation;
        wordTranslation = { ...translation, translation: addWordField };
      }

      await handleTranslationPickUtil(word, vocabulary, wordTranslation, vocabularyGroupId ?? "");
    },
    [addWordField, vocabulary, vocabularyGroupId],
  );

  const handleKeyPress = useCallback(
    (event: React.KeyboardEvent<HTMLInputElement>) => {
      if (event.key === "Enter") {
        setOptions([]);
        setAutoCompleteOptions([]);
        setIsPossibleTranslationsResult(false);
        setLoading(true);
        getTranslations(addWordField).then();
      }
    },
    [addWordField, getTranslations],
  );
  const clearInput = () => {
    setAddWordField("");
  };
  const updateAddWordFormValue = (event: ChangeEvent<HTMLInputElement>) => {
    const { value } = event.target;
    setAddWordField(value);
    setIsPossibleTranslationsResult(false);
    setOptions([]);
    setAutoCompleteOptions([]);
  };

  const handleAutoCompletePick = useCallback(
    (option: AutoCompleteOption) => {
      setAddWordField(option.elem);
      setOptions([]);
      setIsPossibleTranslationsResult(false);
      getTranslations(option.elem, true, option.language === vocabulary.learningLanguage).then();
    },
    [getTranslations, vocabulary.learningLanguage],
  );
  const wordListProps = useMemo(
    () => ({
      divRef: inputDivRef,
      autoCompleteOptions,
      addWordField,
      getTranslations,
      options,
      isPossibleTranslationsResult,
      loading,
      optionsLoading,
    }),
    [
      autoCompleteOptions,
      addWordField,
      getTranslations,
      options,
      isPossibleTranslationsResult,
      loading,
      optionsLoading,
    ],
  );

  const { size, portal } = usePrerenderSize(WordListInput, wordListProps);

  if (!vocabularyGroupId) return <div></div>;
  return (
    <div
      ref={dropdownRef}
      className="w-full h-full flex flex-1 my-5 flex-wrap justify-start rounded-xl"
    >
      {portal}
      {openInput ? (
        <WordListInput
          divRef={inputDivRef}
          autoCompleteOptions={autoCompleteOptions}
          handleAutoCompletePick={handleAutoCompletePick}
          handleKeyPress={handleKeyPress}
          addWordField={addWordField}
          updateAddWordFormValue={updateAddWordFormValue}
          getTranslations={getTranslations}
          options={options}
          handleTranslationPick={addWord}
          isPossibleTranslationsResult={isPossibleTranslationsResult}
          loading={loading}
          optionsLoading={optionsLoading}
        />
      ) : (
        <div
          style={{ height: size?.height }}
          className={`w-full flex flex-1 items-center border-2 border-indigo-300 hover:border-primary hover:shadow-indigo-50 flex-wrap justify-center rounded-xl`}
        >
          <Button
            className={`w-full flex gap-2 ${isMobile ? "p-[13.6px]" : "p-[12.7px]"} items-center justify-between`}
            onClick={() => {
              setAutoCompleteOptions([]);
              setOptions([]);
              setOpenInput(true);
            }}
          >
            <div className="flex items-center gap-2">
              <PlusCircleIcon className="h-5 w-5 text-color-text duration-300 ease-in-all  " />
              <span className="text-sm lg:text-lg font-semibold ">{t("AddWord")}</span>
            </div>

            <Link to={`/${i18n.language}/vocabularies/${vocabularyGroupId}/import`}>
              <span className="text-sm lg:text-lg  hover:underline ">{t("ImportWords")}</span>
            </Link>
          </Button>
        </div>
      )}
    </div>
  );
};

export default WordListAddWordInput;
