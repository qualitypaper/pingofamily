import FormButton from "components/Button/FormButton";
import { ReactElement } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch, useSelector } from "react-redux";
import { Link, Navigate } from "react-router-dom";
import { ReactComponent as GermanIcon } from "../../assets/flags/germany.svg";
import { ReactComponent as SpainIcon } from "../../assets/flags/spain.svg";
import { ReactComponent as EnglishIcon } from "../../assets/flags/united_kingdom.svg";
import penguin_fatCake from "../../assets/penguins/fat_cake.svg";
import {
  selectSelectedLearningLanguage,
  setSelectedLearningLanguage,
} from "../../store/language/languageSlice";
import { selectTokenPair } from "../../store/user/userSelector";

const FormLearning = () => {
  const dispatch = useDispatch();
  const selectedLearningLanguage = useSelector(selectSelectedLearningLanguage);
  const { t, i18n } = useTranslation();
  const tokenPair = useSelector(selectTokenPair);

  const handleLanguageSelect = (language: string) => {
    dispatch(setSelectedLearningLanguage(language));
  };

  console.log(i18n.language)

  if (!tokenPair) {
    return <Navigate to={"/"} />;
  }

  return (
    <div className="w-full max-w-[64rem] m-auto">
      <form
        className="bg-[#F4F9FF] shadow-none rounded-md border-0 border-gray-200 p-4 flex gap-10 flex-col lg:flex-row flex-wrap items-center m-4 lg:items-stretch lg:gap-16 md:shadow-xl md:border-2"
      >
        <span>
          <img alt="" src={penguin_fatCake} className="w-[350px] lg:order-last" />
        </span>

        <div className="flex flex-col gap-6 justify-between">
          <div className="flex flex-col gap-4">
            <span className="text-2xl font-bold text-center">{t("ChooseLearningLanguage")}</span>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 gap-2">
              <FormLanguageButton
                language="ENGLISH"
                showLanguage="English"
                selected={selectedLearningLanguage === "ENGLISH"}
                handleLanguageSelect={handleLanguageSelect}
                icon={<EnglishIcon className="w-14 h-8" />}
              />

              <FormLanguageButton
                language="SPANISH"
                showLanguage="Español"
                selected={selectedLearningLanguage === "SPANISH"}
                handleLanguageSelect={handleLanguageSelect}
                icon={<SpainIcon className="w-14 h-8" />}
              />

              <FormLanguageButton
                language="GERMAN"
                showLanguage="Deutsch"
                selected={selectedLearningLanguage === "GERMAN"}
                handleLanguageSelect={handleLanguageSelect}
                icon={<GermanIcon className="w-14 h-8" />}
              />
            </div>
          </div>
          <Link to={`/${i18n.language}/form/native`}>
            <FormButton type="button">{t("Continue")}</FormButton>
          </Link>
        </div>
      </form>
    </div>
  );
};

export type FormLanguageButtonProps = {
  showLanguage: string;
  language: string;
  handleLanguageSelect: (language: string) => void;
  icon: ReactElement;
  selected: boolean;
};

export const FormLanguageButton = ({
  showLanguage,
  language,
  handleLanguageSelect,
  icon,
  selected,
}: FormLanguageButtonProps) => {
  return (
    <div
      className={`bg-white rounded-md hover:bg-blue-50 cursor-pointer border-2 p-1 ${selected && "border-blue-300"}`}
      onClick={() => handleLanguageSelect(language)}
    >
      <div className="flex items-center">
        {icon}
        <h2 className="text-lg font-semibold">
          {showLanguage.charAt(0).toUpperCase() + showLanguage.slice(1)}
        </h2>
      </div>
    </div>
  );
};

export default FormLearning;
