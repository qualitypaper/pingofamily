import { useTranslation } from "react-i18next";
import { PartOfSpeech } from "../../store/training/trainingTypes";
import { VocabularyGroupActions } from "../../store/vocabulary/vocabularyTypes";
import { MouseEventHandler } from "react";
import PlaySoundButton from "../Button/VolumeUpButton";

type WordDetailsTitleProps = {
  word: string;
  gender: string;
  pos: PartOfSpeech;
  translation: string;
  permissions: VocabularyGroupActions[];
  soundUrl: string;
  setChangeTranslationVisible: () => void;
  setRegenerateExamplesVisible: () => void;
};

export const WordDetailsTitle = ({
  word,
  gender,
  pos,
  translation,
  soundUrl,
  setChangeTranslationVisible,
  setRegenerateExamplesVisible,
  permissions,
}: WordDetailsTitleProps) => {
  const { t } = useTranslation();

  return (
    <div className="flex justify-between flex-col sm:items-stretch mb-4 mt-4 sm:flex-row gap-6 lg:gap-0">
      <div className="flex flex-col mt-0 items-start">
        <div className=" mb-2  whitespace-nowrap lg:text-2xl text-xl">
          <div className="flex items-center gap-2">
            <span className="text-color-big-text font-bold text-3xl">
              <PlaySoundButton soundUrl={soundUrl} />
              {pos !== "NOUN" || word.split(" ")[0]?.startsWith(gender) ? "" : gender} {word}
            </span>
          </div>
          <div className="flex items-center gap-3">
            <p>{translation}</p>
          </div>
        </div>
      </div>
      {permissions.includes("UPDATE_WORD") && (
        <div className="flex  items-stretch gap-3 flex-col sm:flex-row lg:items-center">
          <div className="flex w-[100%] items-center gap-[0.3rem] max-sm:flex-col">
            <ButtonWordDetails
              onClick={setChangeTranslationVisible}
              TextButton={t("ChangeTranslation")}
              className="flex w-full column gap-3 sm:gap-1 lg:flex-row flex-col"
            />

            <ButtonWordDetails
              onClick={setRegenerateExamplesVisible}
              TextButton={t("RegenerateExamples")}
              className="flex w-full column gap-3 sm:gap-1 lg:flex-row flex-col whitespace-nowrap"
            />
          </div>
        </div>
      )}
    </div>
  );
};

interface ButtonWordDetailsProps {
  TextButton: string;
  className: string;
  onClick: MouseEventHandler<HTMLButtonElement>;
}

export const ButtonWordDetails = ({ TextButton, className, onClick }: ButtonWordDetailsProps) => {
  return (
    <nav className={className}>
      <button
        onClick={onClick}
        className="btn-all hover:bg-button-color hover:bg-button-color-light text-white font-bold  py-2 px-8 rounded text-sm transition duration-300 ease-in-out"
      >
        <span className="font-bold text-color-light text-[0.7rem] md:text-[0.7rem]  lg:whitespace-nowrap lg:text-[0.8rem]">
          {TextButton}
        </span>
      </button>
    </nav>
  );
};
