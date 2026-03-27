import { Loading } from "components/Loading/Loading";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch } from "react-redux";
import { Link } from "react-router-dom";
import { ReactComponent as DeleteIcon } from "../../assets/icons/delete.svg";
import { ViewListButton } from "../../pages/protected/Vocabulary/VocabularyGroupList";
import { resetWords, setVocabularyGroup } from "../../store/vocabulary/vocabularySlice";
import { VocabularyGroup } from "../../store/vocabulary/vocabularyTypes";
import { VOCABULARY_GROUP_PERMISSION } from "../../voc_constants";
import StartQuizButton from "../VocabularyGroupList/StartQuizButton";
import { VocabularyGroupContent } from "../VocabularyGroupList/VocabularyGroupContent";
import { useMediaQuery } from "@react-hook/media-query";

export type VocabularyGroupCardProps = {
  vocabularyGroup: VocabularyGroup;
  showForm?: (vocabularyGroup: VocabularyGroup) => void;
};

const VocabularyGroupCard = ({ vocabularyGroup, showForm }: VocabularyGroupCardProps) => {
  const { t } = useTranslation();
  const dispatch = useDispatch();
  const [isHovered, setHovered] = useState<boolean>(false);
  const permissions = VOCABULARY_GROUP_PERMISSION.get(vocabularyGroup?.type ?? "PREDEFINED");
  const isMobile = useMediaQuery("(max-width: 782px)")

  const onCardClick = () => {
    dispatch(resetWords())
    dispatch(setVocabularyGroup(vocabularyGroup));
  };

  return (
    <section
      className={`rounded-lg md:hover:z-30 py-[0.7rem] shadow w-full bg-blue-100 hover:bg-white md:hover:shadow-xl`}
      key={vocabularyGroup.groupId}
      onMouseOver={() => {
        if (!vocabularyGroup.loading) setHovered(true);
        else setHovered(false);
      }}
      onMouseOut={() => setHovered(false)}
    >
      {!isMobile && !vocabularyGroup.loading && isHovered && permissions?.includes("DELETE_GROUP") && (
        <div
          onClick={() => showForm && showForm(vocabularyGroup)}
          className="flex flex-col items-start justify-between relative"
        >
          <div className="absolute top-0 right-0 p-1 hover:cursor-pointer hover:scale-110 hover:text-red-500 ease-out duration-300">
            <div className="cursor-pointer w-6 h-6 flex items-center justify-start rounded-full">
              <DeleteIcon />
            </div>
          </div>
        </div>
      )}
      <Link to={`${vocabularyGroup.groupId}`} onClick={onCardClick}>
        <VocabularyGroupContent vocabularyGroup={vocabularyGroup} />
      </Link>

      {vocabularyGroup.loading ? (
        <Loading />
      ) : !permissions?.includes("TRAIN_GROUP") ? (
        <div
          className={`vocabulary-group-block flex mt-0 max-h-0 opacity-0 overflow-hidden p-0 invisible justify-center items-start gap-4 `}
        >
          <ViewListButton
            onClick={onCardClick}
            title={t("ViewList")}
            vocabularyGroup={vocabularyGroup}
          />
        </div>
      ) : (
        <div
          className={`vocabulary-group-block flex mt-0 max-h-0 opacity-0 overflow-hidden p-0 invisible justify-evenly items-start gap-4`}
        >
          <StartQuizButton
            className="w-full"
            title={t("StartQuiz")}
            vocabularyGroupId={vocabularyGroup.groupId}
          />
          <ViewListButton
            onClick={onCardClick}
            title={t("ViewList")}
            vocabularyGroup={vocabularyGroup}
            classname="font-bold whitespace-nowrap"
          />
        </div>
      )}
    </section>
  );
};

export default VocabularyGroupCard;
