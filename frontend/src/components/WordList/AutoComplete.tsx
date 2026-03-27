import { ReactComponent as ForwardArrowIcon } from "assets/icons/arrow.svg";
import { Button } from "@chakra-ui/react";
import { useTranslation } from "react-i18next";
import { AutoCompleteProps } from "../../features/vocabulary/WordList/WordList";
import {
  AutoCompleteOption,
  TranslationOption,
} from "../../features/vocabulary/WordList/WordListAddWordInput";
import { PartOfSpeech } from "../../store/training/trainingTypes";
import { pickIcon } from "../../utils/iconUtils";
import Dropdown from "../Input/Dropdown";
import { useState } from "react";
import { InfoTip } from "../Button/ToggleTip";
import Input from "../Input/Input";
import { Loading } from "../Loading/Loading";

function AutoComplete({
  options,
  onPick,
  isResult,
  optionsLoading,
}: Readonly<AutoCompleteProps<AutoCompleteOption | TranslationOption>>) {
  const { t } = useTranslation();
  const [ownTranslation, setOwnTranslation] = useState("");
  const [partOfSpeech, setPartOfSpeech] = useState("NOUN");

  const posList = [
    { value: "NOUN", label: t("Noun") },
    { value: "VERB", label: t("Verb") },
    { value: "ADJECTIVE", label: t("Adjective") },
    { value: "ADVERB", label: t("Adverb") },
    { value: "OTHER", label: t("Other") },
  ];

  function handleOwnTranslationPick() {
    onPick({
      elem: {
        translation: ownTranslation,
        pos: partOfSpeech.toUpperCase() as PartOfSpeech,
      },
      language: "ENGLISH",
    } as TranslationOption);
  }

  if (!options) return <div></div>;

  return (
    <div className="bg-white shadow-md absolute left-0 w-full rounded z-50">
      <div className="w-full rounded max-h-[265px] overflow-y-auto">
        {optionsLoading ? (
          <div className="mx-3 my-1">
            <Loading />
          </div>
        ) : (
          options?.filter(option => {
            if (typeof option.elem === "string") {
              return !!option.elem;
            } else {
              return !!option.elem.translation;
            }
          }).map((option, i) => (
            <div key={i} className="m-0 text-xl z-50" onClick={() => onPick(option)}>
              <div className="w-full flex pl-3 justify-between hover:bg-gray-100 cursor-pointer duration-100 ease-in-out items-center">
                <div className="flex gap-2 items-center p-1">
                  {typeof option.elem === "string" ? (
                    <>
                      <span>{(option as AutoCompleteOption).elem}</span>
                      <span>{pickIcon(option.language)}</span>
                    </>
                  ) : (
                    <>
                      <span>{(option as TranslationOption).elem.translation}</span>
                      <span>{(option as TranslationOption).elem.pos?.toLowerCase()}</span>
                      <span>{pickIcon(option.language)}</span>
                    </>
                  )}
                </div>
                <ForwardArrowIcon height="18" width="18" />
              </div>
            </div>
          ))
        )}
      </div>

      {isResult && (
        <div className="flex justify-between items-center w-full">
          <div className="flex flex-col max-sm:flex-col pb-2 mx-[1rem] justify-between">
            <form
              onSubmit={() => handleOwnTranslationPick()}
              className="flex items-center justify-start gap-1 mt-3"
            >
              <div className="w-full flex gap-2">
                <Input
                  required
                  placeholder="Translation"
                  onChange={(e) => setOwnTranslation(e.target.value)}
                  className="p-2 outline-none bg-blue-50 text-color-[#2e425b] w-full lg:w-[20rem] md:w-[15rem] h-[36px] px-1 border border-slate-200
                               rounded placeholder-[#50668f] max-md:text-base text-lg
                               focus:border-blue-500 transition-all duration-300"
                />

                <Dropdown
                  options={posList}
                  label=""
                  onOptionSelect={setPartOfSpeech as any}
                  selectedValue={partOfSpeech}
                  labelClassName="text-sm lg:text-md"
                  containerClassName="flex items-ceneter"
                  selectClassName="bg-blue-50 w-full h-[36px] px-1 md:w-36"
                  optionClassName="hover:bg-blue-100 flex items-center "
                  selectedOptionClassName="text-sm md:text-lg"
                  iconClassName=""
                />

                <Button
                  type="submit"
                  className="btn-all rounded h-auto max-w-[10rem] text-white bg-[#1E90FF]"
                >
                  <span className="font-bold text-sm lg:text-md text-white">{t("Add")}</span>
                </Button>
                <InfoTip content={t("AddOwnTranslationTooltip")} />
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default AutoComplete;
