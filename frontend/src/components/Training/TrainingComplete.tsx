import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { ReactComponent as PenguinRed } from "../../assets/penguins/normal_jump_firecracker.svg";
import { selectHintsCount, selectMistakesCount } from "../../store/training/trainingSelector";
import { clearTrainingCache } from "../../store/training/trainingSlice";
import { TrainingExampleWithIndex } from "../../store/training/trainingTypes";
import { selectTokenPair } from "../../store/user/userSelector";
import { completeTraining } from "../../utils/trainingUtils";
import { WrappedTitleCard } from "../Cards/TitleCard";

type InformationalCardsType = {
  value: string;
  progress: number;
};

export const InformationalCards = ({ value, progress }: InformationalCardsType) => {
  return (
    <div className="bg-white border border-gray-300 rounded-xl p-4 my-2  w-80 md:w-86">
      <span className="text-center block mb-2">{value}</span>
      <div className="w-full bg-gray-200 rounded-xl h-4 overflow-hidden">
        <div
          className="bg-blue-400 h-full text-xs leading-none text-center text-white rounded-lg transition-all duration-300"
          style={{ width: `${progress}%` }}
        ></div>
      </div>
    </div>
  );
};

const TrainingComplete = ({
  finalSequence,
  trainingSessionId,
}: {
  finalSequence: TrainingExampleWithIndex[];
  trainingSessionId: number;
}) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const numberOfMistakes = useSelector(selectMistakesCount);
  const numberOfHints = useSelector(selectHintsCount);
  const totalExercisesCount = finalSequence.length;
  const [accuracyCount, setAccuracyCount] = useState<number>();
  const token = useSelector(selectTokenPair);
  const { t, i18n } = useTranslation();

  const clickHandler = (url: string) => {
    dispatch(clearTrainingCache());
    navigate(url);
  };

  const accuracyProgress = accuracyCount ?? 0;

  const continueTraining = async () => {
    dispatch(clearTrainingCache());
    navigate(`/${i18n.language}/training`, {replace: true});
  };

  useEffect(() => {
    let temp: number = Number.parseInt(
      String(numberOfMistakes !== 0 ? 100 - (numberOfMistakes * 100) / totalExercisesCount : 100),
    );
    if (temp <= 20) temp += 10;
    setAccuracyCount(temp);
  }, [accuracyCount, numberOfMistakes, totalExercisesCount]);

  useEffect(() => {
    const complete = async () => {
      return await completeTraining(finalSequence, trainingSessionId);
    };

    complete().then();
  }, [token, dispatch, finalSequence, trainingSessionId]);

  return (
    <div className="flex justify-center items-center">
      <WrappedTitleCard
        alignment="grid place-items-center text-center text-xl"
        title="Training Complete"
      >
        <div className={`flex flex-col items-center`}>
          <div className="flex flex-wrap justify-center flex-col items-center gap-5 m-2 p-4">
            <PenguinRed className="w-[18rem] h-[18rem] md:w-[23rem] md:h-[23rem]" />

            <div className="flex flex-wrap gap-5 w-full justify-center ">
              <div>
                <InformationalCards
                  value={`${t("Accuracy")} ${accuracyCount}%`}
                  progress={accuracyProgress}
                />
                <InformationalCards
                  value={`${t("Mistakes")} ${numberOfMistakes}`}
                  progress={(numberOfMistakes / totalExercisesCount) * 100}
                />
              </div>
              <div>
                <InformationalCards
                  value={`${t("Exercises")} ${totalExercisesCount}`}
                  progress={100}
                />
                <InformationalCards
                  value={`${t("Hints")} ${numberOfHints}`}
                  progress={(numberOfHints / totalExercisesCount) * 100}
                />
              </div>
            </div>
          </div>
          <div className="flex flex-wrap justify-center p-4 gap-4">
            <button
              className="bg-[#0C7DFF] text-white w-[15em] mx-auto rounded-xl p-[0.6rem] font-semibold"
              onClick={() => clickHandler(`/${i18n.language}/vocabularies`)}
            >
              Back to Vocabularies
            </button>
            <button
              className="bg-[#0C7DFF] text-white w-[15em] mx-auto rounded-xl p-[0.6rem] font-semibold"
              onClick={continueTraining}
            >
              Continue training
            </button>
          </div>
        </div>
      </WrappedTitleCard>
    </div>
  );
};

export default TrainingComplete;
