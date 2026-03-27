import { TrainingTypesProps } from "components/Training/Training";
import { SOUND_TRAINING } from "constant";
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useDispatch } from "react-redux";
import { Language } from "store/language/languageTypes";
import { addMistakeToTrainingSequence, incrementFinalSequenceIndex, incrementHintsNumber, incrementMistakeNumber } from "store/training/trainingSlice";
import AUDIO from "utils/audioUtils";
import { hintTriggerEvent, isCorrect, revealAnswer } from "utils/trainingUtils";

const useSimpleTraining = ({
  targetWord,
  elem,
  onHint,
  showAnswer,
  checkCorrect,
}: TrainingTypesProps) => {
  const dispatch = useDispatch();
  const [input, setInput] = useState<string>("");
  const [correct, setCorrect] = useState<boolean>(false);
  const [skipped, setSkipped] = useState<boolean>(false);
  const [hint, setHint] = useState<boolean>(false);
  const [image, setImage] = useState<string>(targetWord?.wordTranslation?.wordFrom?.imageUrl);

  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    setImage(targetWord?.wordTranslation?.wordFrom?.imageUrl);
  }, [targetWord?.wordTranslation?.wordFrom?.imageUrl]);

  const resetState = () => {
    setInput("");
    setImage("");
    setHint(false);
    setCorrect(false);
    setSkipped(false);
  };

  const completeTraining = useCallback(async (resolve: VoidFunction = () => { }, reject: ErrorCallback = (_) => { }) => {
    setCorrect(true);
    await AUDIO.play(SOUND_TRAINING);
    await AUDIO.play(
      elem.trainingExample.soundUrl ?? targetWord?.wordTranslation?.wordFrom?.soundUrl,
      resolve,
      reject
    );
  }, [elem.trainingExample.soundUrl, targetWord?.wordTranslation?.wordFrom?.soundUrl]);

  const showCorrectAnswer = useCallback(() => {
    setCorrect(true);
    setSkipped(true);
    if (showAnswer) {
      showAnswer();
    } else {
      revealAnswer(setInput, elem.trainingExample.identifiedWord);
    }
    dispatch(addMistakeToTrainingSequence(elem));
    dispatch(
      incrementMistakeNumber({
        index: elem.index,
        trainingType: elem.trainingExample.trainingType,
      }),
    );
    completeTraining();
  }, [showAnswer, dispatch, elem, completeTraining]);

  const showHint = useCallback(() => {
    setHint(true);
    if (onHint) {
      setInput(onHint());
      return;
    } else {
      hintTriggerEvent(setInput, elem.trainingExample.identifiedWord, inputRef);
    }

    dispatch(
      incrementHintsNumber({
        index: elem.index,
        trainingType: elem.trainingExample.trainingType,
      }),
    );
  }, [
    onHint,
    elem.trainingExample.identifiedWord,
    elem.trainingExample.trainingType,
    elem.index,
    dispatch,
  ]);

  const handleSkipClick = useCallback(() => {
    if (correct) {
      resetState();
      dispatch(incrementFinalSequenceIndex());
    } else {
      showCorrectAnswer();
    }
  }, [correct, dispatch, showCorrectAnswer]);

  useEffect(() => {
    if (checkCorrect) {
      if (checkCorrect(input)) {
        completeTraining(
          () => {
            resetState();
            dispatch(incrementFinalSequenceIndex());
          }
        ).then();
      }
    } else {
      if (
        !correct &&
        isCorrect(
          input || "",
          elem.trainingExample.identifiedWord,
          targetWord?.wordTranslation?.wordFrom?.language as Language,
        )
      ) {
        completeTraining(
          () => {
            resetState();
            dispatch(incrementFinalSequenceIndex());
          }
        ).then();
      }
    }
  }, [checkCorrect, completeTraining, correct, dispatch, elem.trainingExample.identifiedWord, input, targetWord?.wordTranslation?.wordFrom?.language]);

  useEffect(() => {
    resetState();
  }, []);

  return useMemo(
    () => ({
      input,
      setInput,
      image,
      correct,
      hint,
      skipped,
      inputRef,
      showCorrectAnswer,
      handleSkipClick,
      showHint,
    }),
    [
      input,
      setInput,
      image,
      correct,
      hint,
      skipped,
      inputRef,
      showCorrectAnswer,
      handleSkipClick,
      showHint,
    ],
  );
};

export default useSimpleTraining;
