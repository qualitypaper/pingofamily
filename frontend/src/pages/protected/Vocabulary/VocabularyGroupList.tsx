import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { selectIsAuthenticated } from "store/user/userSelector";
import Button from "../../../components/Button/Button";
import { selectSuggestedVocabularyGroups } from "../../../store/vocabulary/vocabularySelector";
import { VocabularyGroup } from "../../../store/vocabulary/vocabularyTypes";
import SuggestedGroups from "./SuggestedGroups";
import VocabularyGroups from "./VocabularyGroups";

type ViewListButtonProps = {
  title?: string;
  vocabularyGroup?: VocabularyGroup;
  classname?: string;
  onClick?: () => void;
};

export const ViewListButton = ({
  title,
  vocabularyGroup,
  classname,
  onClick,
}: ViewListButtonProps) => {
  const { t } = useTranslation();

  return (
    <Link to={`${vocabularyGroup?.groupId ?? 0}`}>
      <Button classname={classname} onClick={onClick}>
        {title ?? t("ButtonViewList")}
      </Button>
    </Link>
  );
};

function VocabularyGroupList() {
  const suggestedVocabularyGroups = useSelector(selectSuggestedVocabularyGroups);
  const isAuthenticated = useSelector(selectIsAuthenticated);

  return (
    <>
      {isAuthenticated && (
        <VocabularyGroups />
      )}
      <SuggestedGroups
        suggestedVocabularyGroups={suggestedVocabularyGroups}
      />
    </>
  );
}

export default VocabularyGroupList;
