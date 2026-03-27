import { API } from "app/init";
import FormButton from "components/Button/FormButton";
import { useTranslation } from "react-i18next";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { selectTokenPair } from "store/user/userSelector";
import { isMobile } from "utils/globalUtils";
import { ReactComponent as PenguinRedCap } from "../../assets/penguins/nerd_cap.svg";
import {
  selectSelectedLearningLanguage,
  selectSelectedNativeLanguage,
  setSelectedNativeLanguage,
} from "../../store/language/languageSlice";
import { setCurrentlySelectedVocabulary } from "../../store/vocabulary/vocabularySlice";
import { pickIcon } from "../../utils/iconUtils";
import { FormLanguageButton } from "./FormLearning";
import { useState } from "react";

const FormNative = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const tokenPair = useSelector(selectTokenPair);
  const nativeLanguage = useSelector(selectSelectedNativeLanguage);
  const learningLanguage = useSelector(selectSelectedLearningLanguage);

  const [loading, setLoading] = useState<boolean>(false);

  const { i18n, t } = useTranslation();

  function handleLanguageSelect(language: string) {
    dispatch(setSelectedNativeLanguage(language));
  }

  async function submit() {
    setLoading(true)
    try {
      const res = await API.post(
        `/vocabulary/create`,
        {
          nativeLanguage: nativeLanguage,
          learningLanguage: learningLanguage,
        },
        {
          headers: {
            Authorization: "Bearer " + tokenPair?.accessToken,
          },
        },
      );
      if (!res || !res.data) {
        throw new Error("Failed to create a vocabulary")
      }
      dispatch(setCurrentlySelectedVocabulary(res.data));
    } catch (e) {
      console.error(e);
      return;
    } finally {
      setLoading(false)
    }

    navigate(`/${i18n.language}/vocabularies`);
  }

  return (
    <div className="w-full max-w-[64rem] m-auto">
      <div className="bg-[#F4F9FF] shadow-none rounded-md border-0 border-gray-200 gap-10 lg:gap-16 p-4 flex flex-col lg:flex-row justify-between m-4 md:shadow-xl md:border-2">
        <div className="flex justify-center lg:order-last">
          <PenguinRedCap width={isMobile() ? 300 : 400} height={isMobile() ? 300 : 400} />
        </div>

        <div className="flex flex-col w-full gap-6 justify-between">
          <div className="flex flex-col gap-4">
            <p className="text-3xl font-bold text-center lg:text-3xl">
              {t("ChooseNativeLanguage")}
            </p>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 gap-2">
              {learningLanguage !== "ENGLISH" && (
                <FormLanguageButton
                  handleLanguageSelect={handleLanguageSelect}
                  language="ENGLISH"
                  showLanguage="English"
                  icon={pickIcon("ENGLISH", false, "w-14 h-8")}
                  selected={nativeLanguage === "ENGLISH"}
                />
              )}

              {learningLanguage !== "GERMAN" && (
                <FormLanguageButton
                  handleLanguageSelect={handleLanguageSelect}
                  language="GERMAN"
                  showLanguage="Deutsch"
                  icon={pickIcon("GERMAN", false, "w-14 h-8")}
                  selected={nativeLanguage === "GERMAN"}
                />
              )}

              {learningLanguage !== "SPANISH" && (
                <FormLanguageButton
                  handleLanguageSelect={handleLanguageSelect}
                  language="SPANISH"
                  showLanguage="Español"
                  icon={pickIcon("SPANISH", false, "w-14 h-8")}
                  selected={nativeLanguage === "SPANISH"}
                />
              )}
            </div>
          </div>

          <FormButton
            type="button"
            onClick={submit}
            loading={loading}
          >
            {t("Continue")}
          </FormButton>
        </div>
      </div>
    </div>
  );
};

export default FormNative;
