import { ChangeEvent, KeyboardEvent, Ref } from "react";
import { AutoCompleteOption, TranslationOption } from "./WordListAddWordInput";
import { ReactComponent as SendIcon } from "assets/icons/send.svg";
import Input from "components/Input/Input";
import AutoComplete from "components/WordList/AutoComplete";
import { IconButton } from "@chakra-ui/react";
import { Loading } from "../../../components/Loading/Loading";

type WordListInputProps = {
  divRef?: Ref<HTMLDivElement>;
  handleKeyPress: (event: KeyboardEvent<HTMLInputElement>) => void;
  addWordField: string;
  updateAddWordFormValue: (event: ChangeEvent<HTMLInputElement>) => void;
  getTranslations: (word: string) => void;
  options: TranslationOption[];
  handleTranslationPick: (translation: any) => void;
  handleAutoCompletePick: (word: any) => void;
  isPossibleTranslationsResult: boolean;
  autoCompleteOptions: AutoCompleteOption[];
  loading: boolean;
  optionsLoading: boolean;
};

const WordListInput = ({
  divRef,
  handleAutoCompletePick,
  autoCompleteOptions,
  handleKeyPress,
  addWordField,
  updateAddWordFormValue,
  getTranslations,
  options,
  handleTranslationPick,
  isPossibleTranslationsResult,
  loading,
  optionsLoading,
}: Readonly<WordListInputProps>) => {
  const isResult = isPossibleTranslationsResult && !!addWordField;

  return (
    <div className="flex flex-col max-sm:w-ful w-full" ref={divRef}>
      <div className="flex relative">
        <Input
          autoFocus
          labelValue=""
          labelStyle=""
          loading={loading}
          containerStyle="w-full"
          placeholder=""
          type="text"
          name="Word"
          value={addWordField}
          className="w-full rounded-xl pl-3 py-6 text-xl border-2 border-indigo-300 dark:border-slate-600 hover:border-primary hover:shadow-indigo-50 hover:border-2 transition duration-300 ease-in-out"
          onKeyDown={handleKeyPress}
          onChange={updateAddWordFormValue}
        />

        {loading ? (
          <div className="absolute inset-y-0 right-5 flex justify-start items-center">
            <Loading size={20} />
          </div>
        ) : (
          addWordField && (
            <div className="absolute inset-y-0 right-2 flex items-center">
              <IconButton
                onClick={() => getTranslations(addWordField)}
                className="hover:scale-105 text-indigo-400 cursor-pointer"
              >
                <SendIcon height={24} width={24} color="indigo-400" />
              </IconButton>
            </div>
          )
        )}
      </div>
      <div className="relative left-0 w-full">
        {!isResult ? (
          <AutoComplete
            optionsLoading={optionsLoading}
            isResult={false}
            options={autoCompleteOptions}
            onPick={handleAutoCompletePick}
          />
        ) : (
          <AutoComplete
            optionsLoading={optionsLoading}
            isResult={isResult}
            options={options}
            onPick={handleTranslationPick}
          />
        )}
      </div>
    </div>
  );
};

export default WordListInput;
