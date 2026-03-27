import React, { ChangeEvent, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import { ButtonBack } from "../../components/Button/ButtonBack";
import { showNotification } from "../../store/headerSlice";
import { selectCurrentWords } from "../../store/vocabulary/vocabularySelector";
import {
  TranslationJson,
  VocabularyType,
  WordListWord,
} from "../../store/vocabulary/vocabularyTypes";
import { removeSpecialCharacters } from "../../utils/trainingUtils";
import {
  distinct,
  distinctTranslations,
  getPossibleTranslations,
  handleTranslationPickUtil,
} from "../../utils/vocabularyUtils";
import TranslationsListPopup from "./Popups/TranslationListPopup";
import { useTranslation } from "react-i18next";
import { LoadingWithBackground } from "../../components/Loading/Loading";

export type ImportWordsProps = {
  selected: VocabularyType;
};

export const ImportWords = ({ selected }: ImportWordsProps) => {
  const dispatch = useDispatch();
  const { vocabularyGroupId } = useParams();
  const [textField, setTextField] = useState<string>("");
  const [alreadyCreatedList, setAlreadyCreatedList] = useState<WordListWord[]>([]);
  const [newWords, setNewWords] = useState<string[]>([]);
  const currentWords = useSelector(selectCurrentWords);
  const [inspectedWord, setInspectedWord] = useState<string>("");
  const [translations, setTranslations] = useState<TranslationJson[]>([]);
  const [isOpenedPopup, setIsOpenedPopups] = useState<boolean>(false);
  const { t } = useTranslation();
  const [loading, setLoading] = useState<boolean>(false);
  const onChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
    setTextField(e.target.value);
  };

  const importWords = () => {
    if (!textField) {
      dispatch(
        showNotification({
          status: 0,
          message: t("AddWordFieldEmpty"),
        }),
      );
      return;
    }

    const split = removeSpecialCharacters(textField).split("\n");
    const alreadyCreatedList: WordListWord[] = [];
    const newWordsList: string[] = [];

    for (const s of split) {
      if (!s) continue;
      const words = s.trimStart().trimEnd().split(" ");

      for (const word of words) {
        const alreadyCreated = currentWords.find((e) => e.wordFrom === word);

        if (alreadyCreated) {
          alreadyCreatedList.push(alreadyCreated);
          continue;
        }
        newWordsList.push(word);
      }
    }

    setAlreadyCreatedList(alreadyCreatedList);
    setNewWords(distinct(newWordsList));
  };

  const getPossTranslations = async (word: string) => {
    setLoading(true);
    setInspectedWord(word);
    const possibleTranslationsBody = {
      word: word,
      wordLimit: 5,
      sourceLanguage: selected.learningLanguage ?? "ENGLISH",
      targetLanguage: selected.nativeLanguage ?? "ENGLISH",
      isAutoComplete: false,
    };
    const possibleTranslations = await getPossibleTranslations(possibleTranslationsBody);
    const result = distinctTranslations(possibleTranslations?.possibleTranslations ?? []);
    console.log("🚀 ~ getPossTranslations ~ result:", result);
    setTranslations(result);
    setIsOpenedPopups(true);
    setLoading(false);
  };

  const addNewWord = async (translationJson: TranslationJson) => {
    await handleTranslationPickUtil(
      inspectedWord,
      selected,
      translationJson,
      vocabularyGroupId ?? "-1",
    );
    setNewWords((prev) => prev.filter((word) => word !== inspectedWord));
    closePopup();
    setInspectedWord("");

    dispatch(
      showNotification({
        status: 1,
        message: t("WordAdded"),
      }),
    );
  };

  const closePopup = () => {
    setInspectedWord("");
    setIsOpenedPopups(false);
  };

  const isEmptyAlreadyCreatedList = alreadyCreatedList.length === 0;
  const isEmptyNewWordsList = newWords.length === 0;

  return (
    <div className="flex justify-start flex-col w-full">
      <ButtonBack className="pt-0 lg:pt-6" />
      <div className="flex flex-col items-center justify-start">
        <h1 className="text-3xl md:text-4xl font-bold mb-2 mt-5">{t("importWords")}</h1>
        <p className="mb-2 text-color-text">{t("importWordsDescription")}</p>
        <textarea
          placeholder={t("YourText")}
          onChange={onChange}
          maxLength={5000}
          className="p-3 bg-white rounded-xl w-full md:w-4/5 outline-none  h-32 border border-gray-300 hover:border-gray-400 focus:border-gray-400 "
        />
        {/*<Textarea aria-label="minimum height"  placeholder="Minimum 3 rows" />*/}
        <button
          onClick={importWords}
          className="btn-all btn-md hover:bg-blue-400 text-white font-bold py-2 px-4 rounded mt-5 w-full md:w-1/4 transition-all duration-300"
        >
          {t("import")}
        </button>
      </div>
      <div className="">
        {!isEmptyAlreadyCreatedList && (
          <h1 className="my-5 text-2xl font-semibold">{t("AlreadyCreatedList")}</h1>
        )}
        <div className="inline-flex gap-3">
          {!isEmptyAlreadyCreatedList &&
            alreadyCreatedList.map((e) => (
              <span
                key={e.userVocabularyId}
                className="rounded-md flex flex-wrap justify-between flex-col cursor-pointer duration-100 ease-in-out"
              >
                <div className="dropdown">
                  <span
                    className="flex items-center p-2 justify-between border bg-blue-50 border-gray-300 rounded-lg cursor-pointer w-full
                            ease-out border-b-blue-400
                            hover:border-b-2 transition-all"
                  >
                    {e.wordFrom}
                  </span>
                  <ul className="dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box w-52">
                    <li>{e.wordTo}</li>
                  </ul>
                </div>
              </span>
            ))}
        </div>
        {!isEmptyNewWordsList && (
          <h1 className="my-5 text-2xl font-semibold">{t("NewWordsList")}</h1>
        )}
        <div className="inline-flex flex-wrap gap-3 md:max-w-md xl:max-w-xl sm:max-w-sm lg:max-w-lg">
          {!isEmptyNewWordsList ? (
            newWords.map((e) => (
              <span
                key={e}
                className="rounded-md flex flex-wrap justify-between flex-col
                                    cursor-pointer duration-100 ease-in-out"
              >
                <button
                  onClick={() => getPossTranslations(e)}
                  className="flex items-center
                                        p-2 justify-between border bg-blue-50 border-gray-300 rounded-lg cursor-pointer w-full
                                        ease-out hover:border-b-blue-500 hover:border-b-1 transition-all"
                >
                  {e}
                </button>
              </span>
            ))
          ) : (
            <div></div>
          )}
        </div>
      </div>
      {loading ? (
        <LoadingWithBackground />
      ) : (
        isOpenedPopup && (
          <TranslationsListPopup
            onClose={closePopup}
            translations={translations}
            inspectedWord={inspectedWord}
            onChoose={addNewWord}
            loading={loading}
          />
        )
      )}
    </div>
  );
};
