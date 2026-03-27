import { ReactNode, useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  selectCurrentlyTrainedWords,
  selectFinalSequenceIndex,
  selectFinalTrainingSequence,
  selectTrainingVocabularyGroupId,
} from "store/training/trainingSelector";
import {
  clearTrainingCache,
  DEFAULT_TRAINING_TYPE_SEQUENCE,
  DEVELOPMENT_TRAINING_TYPE_SEQUENCE,
  incrementTotalExercisesCount,
  setCurrentlyTrainedWords,
  setFinalTrainingSequence,
  setTrainingVocabularyGroupId
} from "store/training/trainingSlice";

import { useTranslation } from "react-i18next";
import { useNavigate, useSearchParams } from "react-router-dom";
import { showNotification } from "store/headerSlice";
import { Language } from "store/language/languageTypes";
import { sortByCategory } from "store/training/trainingFunctions";
import { TrainingExampleWithIndex, TrainingI } from "store/training/trainingTypes";
import { selectVocabularyGroup } from "store/vocabulary/vocabularySelector";
import AUDIO from "utils/audioUtils";
import { articles, fetchTraining, removeSpecialCharacters } from "utils/trainingUtils";
import i18n from "../../i18nf/i18n";
import { LoadingPage } from "../Loading/Loading";
import TrainingComplete from "./TrainingComplete";
import SentenceAudio from "./trainingTypes/hard/SentenceAudio";
import SentenceType from "./trainingTypes/hard/SentenceType";
import AudioTraining from "./trainingTypes/standard/AudioTraining";
import CompleteEmptySpacesTraining from "./trainingTypes/standard/CompleteEmptySpacesTraining";
import DeclensionTrainer from "./trainingTypes/standard/DeclensionTrainer";
import PhraseConstruction from "./trainingTypes/standard/PhraseConstruction";
import TranslationTraining from "./trainingTypes/standard/TranslationTraining";

export interface TrainingTypesProps {
  elem: TrainingExampleWithIndex;
  targetWord: TrainingI;
  onHint?: () => string;
  showAnswer?: () => void;
  checkCorrect?: (value: string) => boolean;
}

const Training = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const vocabularyGroup = useSelector(selectVocabularyGroup);
  const currentlyTrainedWords = useSelector(selectCurrentlyTrainedWords);
  const trainingVocabularyGroupId = useSelector(selectTrainingVocabularyGroupId);
  const finalSequence = useSelector(selectFinalTrainingSequence);
  const finalSequenceIndex = useSelector(selectFinalSequenceIndex);
  const [loading, setLoading] = useState<boolean>(false);
  const [searchParams] = useSearchParams();
  const { t } = useTranslation();
  const vocabularyGroupId: number = Number.parseInt(searchParams.get("vocabularyGroupId") ?? "0");

  useEffect(() => {
    const helper = async () => {
      dispatch(clearTrainingCache())
      setLoading(true);
      try {
        const response = await fetchTraining({
          vocabularyGroupId: vgId,
        });

        const learningSession = response.data;

        if (!learningSession.trainingExamples || learningSession.trainingExamples.length === 0) {
          navigate(
            `/${i18n.language}/vocabularies/${vocabularyGroupId ?? vocabularyGroup.groupId ?? ""}`,
          );

          dispatch(
            showNotification({
              message: t("VocabularyGroupEmpty"),
              status: "info",
            }),
          );
          return;
        }
        const training: TrainingI[] = learningSession.trainingExamples;

        dispatch(setTrainingVocabularyGroupId(vgId));
        dispatch(setCurrentlyTrainedWords(learningSession));
        const arrays: any[][] = training.map((e, i) => e.trainingExampleList.map(te => [te.id, [e.trainingId, i]])).flat()
        // @ts-ignore
        const trainingIdMap: Map<number, number[]> = new Map(arrays);
        const trainingExamples = training.map(e => e.trainingExampleList).flat();

        const sorted = sortByCategory(trainingExamples, "trainingType", 
          process.env.NODE_ENV === "production" ? DEFAULT_TRAINING_TYPE_SEQUENCE : DEVELOPMENT_TRAINING_TYPE_SEQUENCE
        );

        const indexed = sorted.map(e => {
          const [trainingId, index] = trainingIdMap.get(e.id) ?? [-1, -1];

          return new TrainingExampleWithIndex(index, e, trainingId, false, false, new Date())
        });

        dispatch(setFinalTrainingSequence(indexed));
      } catch (e) {
        navigate(`/${i18n.language}/vocabularies`);
        dispatch(
          showNotification({
            message: t("VocabularyGroupEmpty"),
            status: "info",
          }),
        );
      } finally {
        setLoading(false);
      }

    };

    const vgId = vocabularyGroupId || vocabularyGroup.groupId;

    console.log(vgId)
    if (trainingVocabularyGroupId) {
      if (vgId !== trainingVocabularyGroupId || finalSequence.length === 0) {
        dispatch(clearTrainingCache())
      } else {
        return;
      }
    }

    helper().then();
  }, [dispatch, navigate, t, vocabularyGroup.groupId, vocabularyGroupId]);

  console.log("finalSequence: ", finalSequence);

  useEffect(() => {
    AUDIO.stop();
    dispatch(incrementTotalExercisesCount());
  }, [dispatch, finalSequenceIndex]);

  const startSequence = (): ReactNode | null => {
    if (finalSequence.length === 0) return null;
    else if (finalSequenceIndex >= finalSequence.length || finalSequenceIndex >= 50) {
      return (
        <>
          {
            <TrainingComplete
              trainingSessionId={currentlyTrainedWords.learningSessionId}
              finalSequence={finalSequence}
            />
          }
        </>
      );
    }

    const elem = finalSequence[finalSequenceIndex];
    const targetWord = currentlyTrainedWords.trainingExamples[elem.index];

    switch (elem.trainingExample.trainingType) {
      case "TRANSLATION":
        return <TranslationTraining targetWord={targetWord} elem={elem} />;
      case "PHRASE_CONSTRUCTION":
      case "PHRASE_CONSTRUCTION_REVERSED":
        return <PhraseConstruction elem={elem} targetWord={targetWord} />;
      case "DECLENSION_OF_VERB":
        return <DeclensionTrainer elem={elem} targetWord={targetWord} />;
      case "AUDIO":
        return <AudioTraining targetWord={targetWord} elem={elem} />;
      case "COMPLETE_EMPTY_SPACES":
        return <CompleteEmptySpacesTraining elem={elem} targetWord={targetWord} />;
      case "SENTENCE_AUDIO":
        return <SentenceAudio targetWord={targetWord} elem={elem} />;
      case "SENTENCE_TYPE":
        return <SentenceType elem={elem} targetWord={targetWord} />;
      default:
        return null;
    }
  };

  const elem = startSequence();

  return loading ? <LoadingPage size={36} /> : <div className="bg-white">{elem}</div>;
};

export default Training;

export const constructWordsTranslationSentence = (
  sentence: string,
  wordsTranslation: Map<string, string[]>,
  language?: Language,
  identifiedWord?: string,
) => {
  if (!sentence) return [];
  const arr = [];
  let count = Math.random() * 1000 - 234234;

  for (let word of sentence.split(" ")) {
    if (word === "?") continue;

    let searchedKey: string = "";
    if (word.length >= 2) {
      for (let [key] of wordsTranslation) {
        if (removeSpecialCharacters(key) === removeSpecialCharacters(word)) {
          searchedKey = key;
          break;
        }
      }
    }
    const value = wordsTranslation.get(searchedKey);

    if (language && identifiedWord) {
      const toRemoveParts = articles.get(language) ?? [];
      if (
        identifiedWord.trim().toLowerCase().startsWith(word.trim().toLowerCase()) &&
        toRemoveParts.includes(word)
      ) {
        continue;
      }
    }

    arr.push(
      <span className="relative inline-block group" key={word + count++}>
        <button className="m-1">{word}</button>
        {value && (
          <ul className="absolute top-full left-1/2 transform -translate-x-1/2 mt-2 z-[1] menu p-2 shadow bg-slate-50 rounded-lg w-24 md:w-52 hidden group-hover:block">
            {value.map((e, index) => (
              <li className="text-xs" key={e + index}>
                {e}
              </li>
            ))}
          </ul>
        )}
      </span>,
    );
  }
  return arr;
};
