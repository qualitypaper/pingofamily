import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { Language } from "store/language/languageTypes";
import { API } from "../../../app/init";
import { ReactComponent as PlusIcon } from "../../../assets/icons/plus.svg";
import { ButtonBack } from "../../../components/Button/ButtonBack";
import StartQuizButton from "../../../components/VocabularyGroupList/StartQuizButton";
import { ButtonDeleteGroup } from "../../../components/WordList/ButtonDeleteGroup";
import i18n from "../../../i18nf/i18n";
import { selectAccessToken } from "../../../store/user/userSelector";
import {
  deleteVocabularyGroupSlice,
  removeSuggestedVocabularyGroup,
  resetWords,
  setCurrentlySelectedVocabulary,
  setVocabularyGroup
} from "../../../store/vocabulary/vocabularySlice";
import { VocabularyGroup, VocabularyType } from "../../../store/vocabulary/vocabularyTypes";
import { getFullLanguageNameForRequest } from "../../../utils/globalUtils";
import {
  createFromPredefinedVocabularyGroup,
  deleteVocabularyGroup,
  mapVocabularyGroupName
} from "../../../utils/vocabularyUtils";
import { VOCABULARY_GROUP_PERMISSION } from "../../../voc_constants";

export type WordListTopViewProps = {
  vocabulary: VocabularyType;
  vocabularyGroup?: VocabularyGroup;
  wordListLength: number;
  setLoading: (val: boolean) => void;
  authenticated?: boolean;
};

const WordListHeader = ({
  vocabulary,
  vocabularyGroup,
  wordListLength,
  setLoading,
  authenticated = false,
}: WordListTopViewProps) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const token = useSelector(selectAccessToken);
  const [isFormVisible, setFormVisible] = useState(false);
  const [groupOnDelete, setGroupOnDelete] = useState<VocabularyGroup | null>();
  const { t } = useTranslation();

  if (!vocabularyGroup?.type) return null;
  const permissions = VOCABULARY_GROUP_PERMISSION.get(vocabularyGroup?.type);
  if (!permissions) return null;

  const addListHandler = async () => {
    setLoading(true);
    if (!token || !authenticated) {
      setLoading(false);
      navigate(`/${i18n.language}/register`);
      return;
    }
    const res = await createFromPredefinedVocabularyGroup(
      vocabulary.id,
      vocabularyGroup?.groupId ?? 0,
    );
    const vocabularyGroupId = res.data.vocabularyGroupId;

    if (+vocabularyGroupId === 0) {
      setLoading(false);
      return;
    }

    dispatch(removeSuggestedVocabularyGroup(vocabularyGroup));
    dispatch(
      setCurrentlySelectedVocabulary({
        ...vocabulary,
        vocabularyGroupList: [
          ...(vocabulary.vocabularyGroupList ? vocabulary.vocabularyGroupList : []),
          { ...vocabularyGroup, groupId: vocabularyGroupId, predefined: false, loading: false },
        ],
      }),
    );
    navigate(`/${i18n.language}/vocabularies`);
    setLoading(false);
  };

  const deleteGroup = async () => {
    setFormVisible(false);
    if (groupOnDelete) {
      await deleteVocabularyGroup(groupOnDelete.groupId);
      dispatch(deleteVocabularyGroupSlice(groupOnDelete));
      navigate(`/${i18n.language}/vocabularies`);
    }
  };

  const showForm = (vocabularyGroup: VocabularyGroup | null) => {
    setFormVisible(true);
    setGroupOnDelete(vocabularyGroup);
  };

  const closeForm = () => {
    setFormVisible(false);
    setGroupOnDelete(null);
  };

  const updateVocabularyGroupName = async (name: string) => {
    if (!token) return;

    dispatch(setVocabularyGroup({ ...vocabularyGroup, name }));
    await API.put(
      `/vocabulary-group/change`,
      {
        vocabularyGroupId: vocabularyGroup?.groupId,
        newVocabularyGroupName: name,
      },
    );
  };

  return (
    <div className="flex flex-col">
      <ButtonBack
        className="lg:pt-4"
        onClick={() => dispatch(resetWords())}
        backUrl={`/${i18n.language}/vocabularies`}
      />
      <div className="flex justify-between  flex-col lg:flex-row">
        <div className="flex mt-5 lg:mt-5 mb-0 sm:mb-5  justify-start  box-border items-stretch gap-2 flex-col sm:flex-row sm:gap-12">
          <div
            className="min-w-52 max-md:min-w-[10rem] p-4 rounded-lg border-2 bg-blue-50 w-52 h-52
                            max-md:w-[10rem] max-md:h-[10rem] flex justify-center max-sm:w-full "
          >
            <img
              className="md:w-[9.6rem] md:h-[9.6rem] h-[7rem] w-[7rem]"
              src={vocabularyGroup?.imageUrl ?? ""}
              alt=""
            />
          </div>
          <div className="flex flex-col justify-between lg:gap-0 gap-6">
            <div className="flex flex-col sm:items-start items-center gap-2 mt-4 sm:mt-0 max-w-full">
              {!permissions.includes("EDIT_GROUP_NAME") || !authenticated ? (
                <DefaultVocabularyGroupName
                  vocabularyGroupName={vocabularyGroup?.name ?? ""}
                  setShowInput={() => { }}
                />
              ) : (
                <VocabularyGroupName
                  vocabularyGroupName={vocabularyGroup?.name ?? ""}
                  updateName={updateVocabularyGroupName}
                />
              )}
              <span className="text-lg text-gray-500 font-medium  max-md:text-sm">
                {wordListLength} {t("WordsInGroup")}
              </span>
            </div>

            <ControlButton
              authenticated={authenticated}
              predefined={!permissions.includes("TRAIN_GROUP")}
              addListHandler={addListHandler}
            />
          </div>
          {permissions.includes("DELETE_GROUP") && authenticated && (
            <ButtonDeleteGroup
              showForm={() => showForm(vocabularyGroup || null)}
              isFormVisible={isFormVisible}
              onClose={closeForm}
              onOK={deleteGroup}
            />
          )}
        </div>
      </div>
    </div>
  );
};

type VocabularyGroupNameProps = {
  vocabularyGroupName: string;
  updateName: (name: string) => void;
};

function VocabularyGroupName({ vocabularyGroupName, updateName }: VocabularyGroupNameProps) {
  const [showInput, setShowInput] = useState(false);

  useEffect(() => {
    const handleKeyPress = (event: KeyboardEvent) => {
      if (event.key === 'Enter' && showInput) {
        setShowInput(false);
        const inputElement = document.querySelector('input[type="text"]') as HTMLInputElement;
        if (inputElement) {
          updateName(inputElement.value);
        }
      }
    };

    if (showInput) {
      document.addEventListener('keydown', handleKeyPress);
    }

    return () => {
      document.removeEventListener('keydown', handleKeyPress);
    };
  }, [showInput, updateName])

  return !showInput ? (
    <DefaultVocabularyGroupName
      vocabularyGroupName={vocabularyGroupName}
      setShowInput={setShowInput}
    />
  ) : (
    <input
      type="text"
      autoFocus
      contentEditable
      className="max-sm:text-center p-0 m-0 text-3xl max-md:max-w-[15rem] max-w-[20rem] max-md:text-3xl font-bold
								outline-none border-2 border-transparent"
      onBlur={(e) => {
        setShowInput(false);
        updateName(e.target.value);
      }}
      defaultValue={mapVocabularyGroupName(vocabularyGroupName, getFullLanguageNameForRequest(i18n.language) as Language)}
    />
  );
}

function DefaultVocabularyGroupName({
  vocabularyGroupName,
  setShowInput,
}: {
  vocabularyGroupName: string;
  setShowInput: (val: boolean) => void;
}) {
  return (
    <button
      onClick={() => setShowInput(true)}
      className="flex whitespace-nowrap max-md:max-w-[15rem] max-w-[20rem] text-3xl font-bold hover:border-blue-400 border-2 border-dashed
						border-transparent"
    >
      <span className="overflow-hidden text-ellipsis">
        {mapVocabularyGroupName(vocabularyGroupName, getFullLanguageNameForRequest(i18n.language) as Language)}
      </span>
    </button>
  );
}

type ControlButtonProps = {
  predefined: boolean;
  addListHandler: () => void;
  authenticated?: boolean;
};

function ControlButton({ authenticated = false, predefined, addListHandler }: ControlButtonProps) {
  const { t } = useTranslation();

  if (!authenticated) return <></>;

  return !predefined ? (
    <StartQuizButton title={t("StartQuiz")} className="w-full md:w-[12rem]" />
  ) : (
    <button
      onClick={addListHandler}
      className="btn-all bg-[#1E90FF] flex justify-evenly items-start gap-2 text-white px-6 py-2 rounded-md hover:bg-blue-600 w-full md:w-[12rem]"
    >
      <PlusIcon fontSize="small" />
      <span className="text-white whitespace-nowrap font-bold text-[0.9rem] lg:text-base">
        Add List
      </span>
    </button>
  );
}

export default WordListHeader;
