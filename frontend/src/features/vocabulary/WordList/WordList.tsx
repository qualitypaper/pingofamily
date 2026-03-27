import { useCallback, useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Navigate, useLocation, useNavigate, useParams } from "react-router-dom";
import { selectIsAuthenticated } from "store/user/userSelector";
import TitleCard from "../../../components/Cards/TitleCard";
import { LoadingPage } from "../../../components/Loading/Loading";
import i18n from "../../../i18nf/i18n";
import {
  selectCurrentlySelected,
  selectCurrentWords,
  selectIsFetchedWordList,
  selectVocabularyGroup
} from "../../../store/vocabulary/vocabularySelector";
import {
  removeWord,
  setCurrentlySelectedVocabulary,
  setCurrentWords,
  setIsFetchedWordList,
  setVocabularyGroup
} from "../../../store/vocabulary/vocabularySlice";
import {
  VocabularyGroup,
  VocabularyType,
  WordListWord,
} from "../../../store/vocabulary/vocabularyTypes";
import {
  deleteWordFromVocabulary,
  fetchWordsByVocabularyGroupId,
} from "../../../utils/vocabularyUtils";
import { VOCABULARY_GROUP_PERMISSION } from "../../../voc_constants";
import Word from "./Word";
import WordListAddWordInput from "./WordListAddWordInput";
import WordListHeader from "./WordListHeader";

export type AutoCompleteProps<T> = {
  readonly options?: T[];
  readonly onPick: (value: T) => void;
  readonly isResult?: boolean;
  readonly optionsLoading?: boolean;
};

function WordList() {
  const CHUNK_SIZE = 100;
  const THRESHOLD_INDEX = 65;

  const dispatch = useDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const { vocabularyGroupId } = useParams();

  const vocabulary = useSelector(selectCurrentlySelected);
  const currentWords = useSelector(selectCurrentWords);
  const vocabularyGroup = useSelector(selectVocabularyGroup);
  const isAuthenticated = useSelector(selectIsAuthenticated);
  const isFetched = useSelector(selectIsFetchedWordList);

  const [loading, setLoading] = useState(false);
  const [checkCounter, setCheckCounter] = useState(0);
  const [visibleCount, setVisibleCount] = useState(CHUNK_SIZE);

  const scrollRef = useRef<HTMLDivElement>(null);
  const observer = useRef<IntersectionObserver | null>(null);

  const lastElementRef = useCallback((node: HTMLDivElement | null) => {
    if (observer.current) observer.current.disconnect();
    observer.current = new IntersectionObserver((entries) => {
      if (entries[0].isIntersecting && visibleCount < currentWords.length) {
        setVisibleCount((prev) => prev + CHUNK_SIZE);
      }
    });
    if (node) observer.current.observe(node);
  }, [visibleCount, currentWords.length]);

  useEffect(() => {
    const temp = sessionStorage.getItem("isFetched");
    if (!temp) {
      dispatch(setIsFetchedWordList(false));
      return;
    }

    sessionStorage.removeItem("isFetched");

    if (temp === "true") {
      dispatch(setIsFetchedWordList(true));
    } else {
      dispatch(setIsFetchedWordList(false));
    }
  }, [dispatch, location.pathname])

  useEffect(() => {
    if (vocabulary || isAuthenticated) return;

    const temp: VocabularyType = {
      id: 0,
      numberOfWords: 0,
      createdDate: Date.now().toString(),
      learningLanguage: vocabularyGroup.learningLanguage ?? "ENGLISH",
      nativeLanguage: vocabularyGroup.nativeLanguage ?? "ENGLISH",
      vocabularyGroupList: [],
      loading: false,
    };

    dispatch(setCurrentlySelectedVocabulary(temp));
  }, [dispatch, isAuthenticated, vocabulary, vocabularyGroup.learningLanguage, vocabularyGroup.nativeLanguage]);

  const fetchWordsByVocabularyGroup = useCallback(async () => {
    setLoading(true);
    try {
      const res = await fetchWordsByVocabularyGroupId(+(vocabularyGroupId ?? "0"));
      if (!res || !res.data) {
        console.warn("Word list fetch haven't returned anything");
        return;
      }

      const response = res.data;

      const { words, vocabularyGroup: fetchedVocabularyGroup } = response;

      if (!fetchedVocabularyGroup) {
        throw new Error("Fetched vocabulary group is undefined");
      }

      if (fetchedVocabularyGroup.groupId !== vocabularyGroupId) {
        dispatch(setVocabularyGroup(fetchedVocabularyGroup));
      }

      dispatch(setCurrentWords(words.map((w: WordListWord) => ({ ...w, vocabularyGroupId }))));
      dispatch(setIsFetchedWordList(true));
    } catch (e) {
      console.error(e);
      navigate(`/${i18n.language}/vocabularies`);
    } finally {
      setLoading(false);
    }
  }, [dispatch, navigate, vocabularyGroupId]);

  // fetch for user_re/defined groups
  useEffect(() => {
    if (vocabularyGroup.type === "PREDEFINED") return;
    else if (!vocabularyGroupId) {
      console.log("Navigating back because vocabularyGroupId is undefined");
      navigate(-1);
      return;
    }

    const vocGroup =
      vocabularyGroup && +(vocabularyGroupId || "0") === vocabularyGroup.groupId
        ? vocabularyGroup
        : vocabulary.vocabularyGroupList.find(
          (e: VocabularyGroup) => +e.groupId === +(vocabularyGroupId ?? "0"),
        );


    if (!isFetched || (!vocGroup && currentWords.length === 0)) {
      fetchWordsByVocabularyGroup().then();
    }
  }, [
    currentWords,
    dispatch,
    fetchWordsByVocabularyGroup,
    isFetched,
    navigate,
    vocabulary.vocabularyGroupList,
    vocabularyGroup,
    vocabularyGroupId,
  ]);

  // fetch for predefined vocabulary groups
  useEffect(() => {
    if (vocabularyGroup.type !== "PREDEFINED") return;
    else if (!isFetched && vocabularyGroupId) fetchWordsByVocabularyGroup().then();
  }, [isFetched, vocabularyGroupId, fetchWordsByVocabularyGroup, vocabularyGroup.type]);

  useEffect(() => {
    const newFiltered = currentWords.filter((word: WordListWord) => {
      if (!word.loading) return true;
      else if (word.createdAt && word.createdAt + 1000 * 60 * 1.1 < Date.now()) {
        return false;
      }

      return word.vocabularyGroupId === +(vocabularyGroupId ?? "0");
    });
    if (currentWords.length !== newFiltered.length) dispatch(setCurrentWords(newFiltered));

    setTimeout(() => setCheckCounter(checkCounter > 100 ? 0 : checkCounter + 1), 30 * 1000);
  }, [checkCounter, dispatch, currentWords, vocabularyGroupId]);

  if (!vocabularyGroup.type) {
    console.log("Type error navigate", vocabularyGroup?.type);
    return <Navigate to={`/${i18n.language}/vocabularies`} />;
  }

  const permissions = VOCABULARY_GROUP_PERMISSION.get(vocabularyGroup?.type);

  if (!vocabularyGroupId || Number.isNaN(+vocabularyGroupId) || !permissions) {
    console.log("vocabularyGroupId and permissions navigate back", vocabularyGroupId, permissions);

    return <Navigate to={`/${i18n.language}/vocabularies`} />;
  }

  const removeWordFromVocabulary = async (word: WordListWord) => {
    setLoading(true);
    await deleteWordFromVocabulary(word.userVocabularyId, +(vocabularyGroupId ?? "0"));
    dispatch(removeWord(word));
    setLoading(false);
  };

  return loading ? (
    <LoadingPage />
  ) : (
    <div className="max-w-5xl flex" ref={scrollRef}>
      <TitleCard>
        <WordListHeader
          authenticated={isAuthenticated}
          vocabularyGroup={vocabularyGroup}
          wordListLength={currentWords.length}
          vocabulary={vocabulary}
          setLoading={setLoading}
        />

        {permissions.includes("ADD_WORD") && isAuthenticated && (
          <WordListAddWordInput
            currentVocabularyWords={currentWords}
            vocabulary={vocabulary}
            vocabularyGroupId={vocabularyGroupId}
          />
        )}

        <div className="flex flex-wrap flex-1 justify-start mb-16">
          {currentWords.slice(0, visibleCount).map((word: WordListWord, index: number) => {
            if (index === visibleCount - CHUNK_SIZE + THRESHOLD_INDEX) {
              return (
                <div
                  className="w-full"
                  key={word.userVocabularyId + (word.wordFrom ?? "") + word.wordTo}
                  ref={lastElementRef}
                >
                  <Word
                    authenticated={isAuthenticated}
                    word={word}
                    removeWord={removeWordFromVocabulary}
                    permissions={permissions}
                  />
                </div>
              );
            }
            return (
              <Word
                authenticated={isAuthenticated}
                key={word.userVocabularyId + (word.wordFrom ?? "") + word.wordTo}
                word={word}
                removeWord={removeWordFromVocabulary}
                permissions={permissions}
              />
            );
          })}
        </div>
      </TitleCard>
    </div>
  );
}

export default WordList;
