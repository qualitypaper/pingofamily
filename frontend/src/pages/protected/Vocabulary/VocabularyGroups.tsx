import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch, useSelector } from "react-redux";
import { selectCurrentlySelected } from "store/vocabulary/vocabularySelector";
import VocabularyGroupCard from "../../../components/Cards/VocabularyGroupCard";
import AddButton from "../../../components/VocabularyGroupList/AddButton";
import ConfirmationPopup from "../../../features/vocabulary/Popups/ConfirmationPopup";
import {
  deleteVocabularyGroupSlice,
  setSuggestedVocabularyGroups,
} from "../../../store/vocabulary/vocabularySlice";
import { VocabularyGroup } from "../../../store/vocabulary/vocabularyTypes";
import {
  deleteVocabularyGroup,
  fetchSuggestedVocabularyGroups,
} from "../../../utils/vocabularyUtils";

function VocabularyGroups() {
  const vocabulary = useSelector(selectCurrentlySelected);
  const [isFormVisible, setFormVisible] = useState(false);
  const dispatch = useDispatch();
  const [groupOnDelete, setGroupOnDelete] = useState<VocabularyGroup | null>();
  const { t } = useTranslation();

  const deleteVGroup = async () => {
    setFormVisible(false);
    if (groupOnDelete) {
      await deleteVocabularyGroup(groupOnDelete.groupId);
      dispatch(deleteVocabularyGroupSlice(groupOnDelete));
      if (!vocabulary.learningLanguage || !vocabulary.nativeLanguage) {
        return;
      }

      // @ts-ignore
      if (groupOnDelete.initiallyPredefined) {
        const res = await fetchSuggestedVocabularyGroups(
          vocabulary.learningLanguage,
          vocabulary.nativeLanguage,
        );
        dispatch(setSuggestedVocabularyGroups(res.data));
      }
    }
  };

  const showForm = (vocabularyGroup: VocabularyGroup) => {
    setFormVisible(true);
    setGroupOnDelete(vocabularyGroup);
  };

  const onClose = () => {
    setFormVisible(false);
    setGroupOnDelete(null);
  };

  return (
    <>
      <h1 className="animation text-2xl font-bold mb-6 mt-3">{t("VocabularyGroupListTitle")}</h1>
      <div className="flex">
        <div className="vocabulary-group">
          {vocabulary.vocabularyGroupList?.map((vocabularyGroup, _) => (
            <VocabularyGroupCard
              key={vocabularyGroup.groupId}
              showForm={showForm}
              vocabularyGroup={vocabularyGroup}
            />
          ))}
          <AddButton />
        </div>
        {isFormVisible && (
          <ConfirmationPopup
            onClose={onClose}
            onOk={deleteVGroup}
            message={t("DeleteVocabularyGroupText")}
          />
        )}
      </div>
    </>
  );
}

export default VocabularyGroups;
