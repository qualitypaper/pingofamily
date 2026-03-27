import { ReactComponent as Language } from "assets/icons/academic.svg";
import { ReactComponent as HintIcon } from "assets/icons/hint.svg";
import TitleCard from "components/Cards/TitleCard";
import LinearProgress from "components/Loading/LinearProgress";
import SpecialCharactersBlock from "features/vocabulary/SpecialCharactersBlock";
import { memo, ReactNode, useEffect, useRef, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  selectFinalSequenceIndex,
  selectFinalTrainingSequence,
} from "store/training/trainingSelector";
import { clearTrainingCache } from "store/training/trainingSlice";
import SkipButton from "../SkipButton";
import { CloseTrainingButton } from "./CloseTrainingButton";

type TrainingBlockProps = {
  title?: string;
  children: string | string[] | ReactNode | ReactNode[];
  showHint?: () => void;
  isHint?: boolean;
  isSkipped: boolean;
  isCorrect: boolean;
  handleSkipClick: () => void;
  addChar: (char: string) => void;
};

const TrainingBlock = memo(
  ({
    title,
    children,
    showHint,
    isSkipped = false,
    isHint = false,
    isCorrect = false,
    handleSkipClick,
    addChar,
  }: TrainingBlockProps) => {
    const dispatch = useDispatch();
    const finalTrainingSequence = useSelector(selectFinalTrainingSequence);
    const trainingSequenceIndex = useSelector(selectFinalSequenceIndex);
    const [progress, setProgress] = useState(((trainingSequenceIndex + 1) * 100) / finalTrainingSequence.length);
    const [hintIcon, setHintIcon] = useState(false);

    useEffect(() => {
      setProgress((trainingSequenceIndex + 1) * 100 / finalTrainingSequence.length)
    }, [finalTrainingSequence.length, trainingSequenceIndex])

    const ref = useRef<HTMLDivElement>(null);

    const onBackClick = () => {
      dispatch(clearTrainingCache());
    };

    const handleCharacterClick = (char: string) => {
      addChar(char);
      setHintIcon(false);
    };

    const onHintClick = () => {
      if (!isHint) {
        showHint && showHint();
      }
    };

    const handleMouseLeave = (event: any, setClickIcon: any) => {
      if (ref.current && ref.current.contains(event.target)) {
        setClickIcon(false);
      }
    };

    useEffect(() => {
      const handleClickOutside = (event: any) => {
        if (ref.current) {
          if (!ref.current.contains(event.target)) {
            setHintIcon(false);
          }
          if (ref.current.contains(event.target)) {
            setHintIcon(true);
          }
        }
      };

      document.addEventListener("mousedown", handleClickOutside, true);

      return () => {
        document.removeEventListener("mousedown", handleClickOutside, true);
      };
    }, []);

    return (
      <div className="overflow-hidden w-full flex flex-col h-auto md:h-full">
        <div className="flex flex-col justify-between items-center">
          <div className="relative flex flex-row items-сenter justify-between w-full mx-auto my-4 max-w-[1084px] max-lg:mt-4">
            <CloseTrainingButton onBackClick={onBackClick} className="lg:px-4 xl:px-8" />
            <LinearProgress value={progress} color="blue" />

            <div className="flex relative">
              <>
                <div className="w-6 h-6 sm:w-7 sm:h-7">
                  <Language
                    onMouseEnter={() => setHintIcon(true)}
                    onMouseLeave={() => handleMouseLeave(ref, setHintIcon)}
                    className="!w-6 !h-6 sm:!w-7 sm:!h-7 max-lg:mt-1
                                    cursor-pointer absolute xl:relative sm:top-[10px] top-[0px]
                                    right-[2rem] xl:right-[0.5rem]"
                  />
                </div>

                {hintIcon && (
                  <div ref={ref} className="animation transition-all duration-300 ease-out z-50 ">
                    <SpecialCharactersBlock onCharacterClick={handleCharacterClick} />
                  </div>
                )}
              </>

              <button
                style={{
                  pointerEvents: isCorrect || isSkipped || isHint ? "none" : "all",
                }}
                className="relative flex items-start justify-center z-10"
                onClick={onHintClick}
              >
                {showHint && (
                  <HintIcon
                    className="w-6 h-6 max-lg:mt-1
                                    cursor-pointer absolute xl:relative sm:top-[10px] top-[0px] right-[5px]
                                    hover:fill-yellow-300 duration-300 ease-out transition sm:w-7 sm:h-7"
                  />
                )}
              </button>
            </div>
          </div>
          <TitleCard title={title}>
            <div className="flex max-w-[1084px] min-w-full px-4 w-full min-h-full justify-start md:mb-10 md:justify-center items-center flex-wrap flex-col gap-0 lg:gap-4">
              {children}
            </div>
          </TitleCard>
          <SkipButton handleSkipClick={handleSkipClick} isCorrect={isCorrect ?? false} />
        </div>
      </div>
    );
  },
);

export default TrainingBlock;
