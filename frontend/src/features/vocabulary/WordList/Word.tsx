import React, { FC } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { ReactComponent as DeleteIcon } from "../../../assets/icons/delete.svg";
import { showNotification } from "../../../store/headerSlice";
import { setCurrentlyInspectedWord } from "../../../store/vocabulary/vocabularySlice";
import { VocabularyGroupActions, WordListWord } from "../../../store/vocabulary/vocabularyTypes";
import { ItemPairContainer } from "../SpanContainer";

export type WordProps = {
  word: WordListWord;
  removeWord: (word: WordListWord) => void;
  permissions: VocabularyGroupActions[];
  authenticated?: boolean;
};

const Word: FC<WordProps> = ({
  word,
  removeWord,
  permissions,
  authenticated = false,
}: WordProps) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { t } = useTranslation();

  const deleteWordHandler = (event: React.MouseEvent<HTMLElement>) => {
    event.stopPropagation();
    removeWord(word);
  };

  function clickHandler() {
    try {
      if (!word.loading) {
        dispatch(setCurrentlyInspectedWord(word));
        navigate(`${word.userVocabularyId}`);
        window.scrollTo(0, 0);
      } else {
        dispatch(
          showNotification({
            message: t("WordLoaded"),
            status: "info",
          }),
        );
      }
    } catch (error) {
      console.error("Navigation Error:", error);
    }
  }

  return (
    <section
      className="flex items-center p-2 justify-between cursor-pointer hover:bg-base-200 border-b-2 border-base-300 mb-2 rounded-lg w-full transition duration-3000"
      onClick={clickHandler}
    >
      <div className="flex flex-col md:flex-row flex-wrap">
        <div className="flex items-center">
          <div className={`p-2  text-gray-900 flex gap-2 items-center`}>
            <ItemPairContainer
              item1={word?.wordFrom ?? ""}
              item2={word?.wordTo ?? ""}
              additionalStyles1="font-bold sm:text-md"
              additionalStyles2="font-md color-base-300 text-gray-500"
              soundUrl={word.soundUrl ?? ""}
              loading={word.loading}
            />
          </div>
        </div>

        <div className="w-full flex flex-col lg:flex-row justify-between items-center"></div>
      </div>
      {permissions.includes("DELETE_WORD") && !word.loading && authenticated && (
        <div className="flex flex-wrap m-2 text-gray-900 dark:text-gray-300">
          <button
            className="hover:cursor-pointer sm:text-sm text-gray-900  dark:text-gray-300 font-semibold hover:text-red-500 hover:scale-110 transition-all duration-300"
            onClick={deleteWordHandler}
          >
            <DeleteIcon />
          </button>
        </div>
      )}
    </section>
  );
};
export default Word;
