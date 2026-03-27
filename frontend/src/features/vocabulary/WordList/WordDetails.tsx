import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { ButtonBack } from "../../../components/Button/ButtonBack";
import TitleCard from "../../../components/Cards/TitleCard";
import { LoadingPage } from "../../../components/Loading/Loading";
import Conjugation from "../../../components/WordDetails/Conjugation";
import Examples from "../../../components/WordDetails/Examples";
import HeaderWordDetails from "../../../components/WordDetails/HeaderWordDetails";
import PosDesc from "../../../components/WordDetails/PosDesc";
import Synonyms from "../../../components/WordDetails/Synonyms";
import { WordDetailsTitle } from "../../../components/WordDetails/WordDetailsTitle";
import { Difficulty } from "../../../custom";
import { showNotification } from "../../../store/headerSlice";
import {
  selectCurrentlyInspectedWord,
  selectCurrentlyInspectedWordGender,
  selectVocabularyGroup,
} from "../../../store/vocabulary/vocabularySelector";
import {
  changeWordParameters,
  setCurrentlyInspectedWord,
  setIsFetchedWordList,
  setWordLoading,
} from "../../../store/vocabulary/vocabularySlice";
import { TranslationJson } from "../../../store/vocabulary/vocabularyTypes";
import {
  changeTranslation,
  fetchWord,
  getPossibleTranslations,
  regenerateExamples,
} from "../../../utils/vocabularyUtils";
import { VOCABULARY_GROUP_PERMISSION } from "../../../voc_constants";
import RegenerateExamplesPopup from "../Popups/RegenerateExamplesPopup";
import TranslationsListPopup from "../Popups/TranslationListPopup";

const WordDetails = () => {
  const dispatch = useDispatch();
  const { wordId } = useParams();
  const navigate = useNavigate();
  const [selectedItem, setSelectedItem] = useState("Dictionary");
  const [changeTranslationVisible, setChangeTranslationVisible] = useState<boolean>(false);
  const [regenerateExamplesVisible, setRegenerateExamplesVisible] = useState<boolean>(false);
  const [translations, setTranslations] = useState<TranslationJson[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [possibleTranslationsLoading, setPossibleTranslationsLoading] = useState<boolean>(true);

  const { t, i18n } = useTranslation();

  const word = useSelector(selectCurrentlyInspectedWord);
  const vocabularyGroup = useSelector(selectVocabularyGroup);
  const currentlyInspectedWordGender = useSelector(selectCurrentlyInspectedWordGender);

  useEffect(() => {
    async function fetch() {
      try {
        const res = await fetchWord(+(wordId ?? "0"));
        if (!res || !res?.data) {
          return;
        }
        dispatch(setCurrentlyInspectedWord(res.data));
      } catch (e) {
        navigate(-1);
        dispatch(
          showNotification({
            message: "Unexpected on our side. Please try again later",
            status: "error",
          }),
        );
      } finally {
        setLoading(false);
      }
    }

    fetch().then();
  }, [dispatch, navigate, t, wordId]);

  const redirectUrl = `/${i18n.language}/vocabularies`;
  if (!word?.wordTranslation) {
    dispatch(setCurrentlyInspectedWord(null));
    return <LoadingPage />;
  }
  if (!vocabularyGroup.type) {
    return null;
  }
  const permissions = VOCABULARY_GROUP_PERMISSION.get(vocabularyGroup?.type);
  if (!permissions) {
    return null;
  }
  const { wordTranslation } = word;
  const { wordFrom, wordTo } = wordTranslation;
  const { wordDictionary } = wordFrom;

  async function getPossTranslations() {
    setLoading(true);
    setChangeTranslationVisible(true);
    const possibleTranslations = await getPossibleTranslations({
      word: wordFrom.word,
      sourceLanguage: wordFrom.language,
      targetLanguage: wordTo.language,
      wordLimit: 7,
      isAutoComplete: false,
    });
    setTranslations(
      possibleTranslations?.possibleTranslations?.filter(
        (x) => x.translation !== (word?.wordTranslation?.wordTo.word ?? ""),
      ) || [],
    );
    setPossibleTranslationsLoading(false);
    setLoading(false);
  }

  async function onChangeTranslation(option: TranslationJson) {
    setChangeTranslationVisible(false);
    await changeTranslation(
      typeof word?.userVocabularyId === "number" ? word.userVocabularyId : -1,
      option.translation,
      option.pos,
      vocabularyGroup.groupId,
    );
    sessionStorage.setItem("isFetched", "true");
    dispatch(setWordLoading({ userVocabularyId: word?.userVocabularyId }));
    dispatch(setIsFetchedWordList(true))
    dispatch(
      changeWordParameters({
        previousUserVocabularyId: word?.userVocabularyId,
        newUserVocabulary: {
          addWordType: "PARAMETER_CHANGE",
          userVocabularyId: word?.userVocabularyId,
          wordFrom: word?.wordTranslation?.wordFrom.word,
          wordTo: option.translation,
          partOfSpeech: option.pos,
          loading: true,
          soundUrl: word?.wordTranslation?.wordFrom.soundUrl,
          wordFromId: word?.wordTranslation?.wordFrom.id,
          vocabularyGroupId: word?.vocabularyGroupId,
          previousUserVocabularyId: word?.userVocabularyId,
        },
      }),
    );
    navigate(`${redirectUrl}/${vocabularyGroup.groupId}`);
  }

  async function onRegenerateExamples(difficulty: Difficulty) {
    setRegenerateExamplesVisible(false);
    await regenerateExamples(
      typeof word?.userVocabularyId === "number" ? word?.userVocabularyId : -1,
      vocabularyGroup.groupId,
      difficulty,
    );
    dispatch(setWordLoading({ userVocabularyId: word?.userVocabularyId }));
    dispatch(setIsFetchedWordList(true))
    sessionStorage.setItem("isFetched", "true");
    navigate(`${redirectUrl}/${vocabularyGroup.groupId}`, { state: { isFetched: true } });
  }

  return loading || !word ? (
    <LoadingPage />
  ) : (
    <div className="flex mb-10">
      <div className="flex flex-col w-full h-full ">
        <ButtonBack onClick={() => dispatch(setCurrentlyInspectedWord(null))} />
        <TitleCard
          className=""
          titleClassName="text-lg"
          title={
            <WordDetailsTitle
              pos={wordFrom.partOfSpeech}
              gender={currentlyInspectedWordGender}
              soundUrl={wordFrom.soundUrl}
              word={wordFrom.word || ""}
              translation={wordTo.word || ""}
              permissions={permissions}
              setChangeTranslationVisible={() => getPossTranslations()}
              setRegenerateExamplesVisible={() => setRegenerateExamplesVisible(true)}
            />
          }
        >
          <div className="my-3 bg-[#F4F4F4] border-2">
            <HeaderWordDetails selectedItem={selectedItem} setSelectedItem={setSelectedItem} />
          </div>

          <>
            {selectedItem === "Dictionary" && word.wordExampleTranslation && (
              <>
                <Examples
                  imageUrl={wordFrom.imageUrl}
                  wordExample={word.wordExampleTranslation}
                  key={word.userVocabularyId}
                />
                <div className="w-full h-1 bg-gray-200 mb-4 mt-4"></div>
                {wordDictionary.description &&
                  wordDictionary.description !== "" &&
                  wordDictionary.descriptionTranslation &&
                  wordDictionary.descriptionTranslation !== "" && (
                    <>
                      <PosDesc
                        desc={wordDictionary.description}
                        descTranslation={wordDictionary.descriptionTranslation}
                      />
                      <div className="w-full h-1 bg-gray-200 mb-4 mt-4"></div>
                    </>
                  )}
                {wordDictionary.synonyms &&
                  wordDictionary.synonyms.length > 0 &&
                  wordDictionary.synonyms[0] !== null && (
                    <>
                      <Synonyms synonyms={wordDictionary.synonyms} />
                      <div className="w-full h-1 bg-gray-200 mb-4 mt-4"></div>
                    </>
                  )}
                <Conjugation conjugation={wordDictionary.conjugation} pos={wordFrom.partOfSpeech} />
              </>
            )}
            {selectedItem === "Examples" && word.wordExampleTranslation && (
              <Examples
                imageUrl={wordFrom.imageUrl}
                wordExample={word.wordExampleTranslation}
                key={word.userVocabularyId}
              />
            )}
            {selectedItem === "Description" && (
              <PosDesc
                desc={wordDictionary.description}
                descTranslation={wordDictionary.descriptionTranslation}
              />
            )}
            {selectedItem === "Thesaurus" && <Synonyms synonyms={wordDictionary.synonyms} />}
            {selectedItem === "Conjugation" && (
              <Conjugation conjugation={wordDictionary.conjugation} pos={wordFrom.partOfSpeech} />
            )}
          </>
        </TitleCard>
      </div>
      {changeTranslationVisible && (
        <TranslationsListPopup
          translations={translations}
          onClose={() => setChangeTranslationVisible(false)}
          inspectedWord={wordFrom.word}
          onChoose={onChangeTranslation}
          loading={possibleTranslationsLoading}
        />
      )}
      {regenerateExamplesVisible && (
        <RegenerateExamplesPopup
          onClose={() => setRegenerateExamplesVisible(false)}
          regenerateExamples={onRegenerateExamples}
        />
      )}
    </div>
  );
};
export default WordDetails;
