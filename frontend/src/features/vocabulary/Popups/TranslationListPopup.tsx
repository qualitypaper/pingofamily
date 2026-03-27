import {useState} from "react";
import {TranslationJson} from "../../../store/vocabulary/vocabularyTypes";
import SlidingForm from "../../../components/Popup/SlidingForm";
import {useTranslation} from "react-i18next";
import {PartOfSpeech} from "../../../store/training/trainingTypes";
import Dropdown from "../../../components/Input/Dropdown";
import {Spinner} from "@chakra-ui/react";
import Input from "../../../components/Input/Input";
import {LoadingPage} from "../../../components/Loading/Loading";

export type TranslationListProps = {
	translations: TranslationJson[];
	onClose: () => void;
	inspectedWord: string;
	onChoose: (translationJson: TranslationJson) => void;
	loading: boolean;
};

const TranslationRecord = ({
														 translation,
														 pos,
													 }: {
	translation: string;
	pos: string;
}) => {
	return (
		<>
      <span className="text-lg text-gray-700 md:text-base">
        {translation || ""}
      </span>
			<span className="text-sm text-gray-500 md:text-xs">{pos}</span>
		</>
	);
};

const TranslationsListPopup = ({
																 translations,
																 onClose,
																 inspectedWord,
																 onChoose,
																 loading,
															 }: TranslationListProps) => {
	console.log("🚀 ~ TranslationsListPopup ~ translations:", translations);

	const [ownTranslation, setOwnTranslation] = useState<string>("");
	const [partOfSpeech, setPartOfSpeech] = useState<PartOfSpeech>("NOUN");
	const {t} = useTranslation();

	const options = [
		{value: "NOUN", label: t("Noun")},
		{value: "VERB", label: t("Verb")},
		{value: "ADJECTIVE", label: t("Adjective")},
		{value: "ADVERB", label: t("Adverb")},
		{value: "OTHER", label: t("Other")},
	];

	function submitWithOwnTranslation(e: React.FormEvent) {
		e.preventDefault();

		onChoose({
			translation: ownTranslation,
			pos: partOfSpeech,
			language: "ENGLISH",
		});
	}

	return (
		<>
			{loading ? (
				<LoadingPage/>
			) : (
				<SlidingForm onClose={onClose} showCloseButton={false}>
					<div className="flex flex-col p-2 bg-white rounded-lg max-w-md mx-auto md:max-w-full">
						<>
							<h1 className="text-2xl font-bold text-gray-800 md:text-xl">
								{t("AddYourTranslation")}
							</h1>
							<span className="mt-2 text-xl font-medium text-gray-600 md:text-lg">
                {inspectedWord}
              </span>
							<div className="flex flex-col mt-4">
								{loading ? (
									<div className="relative w-10 h-10 flex justify-center items-center m-auto ">
										<Spinner boxSize="50px"/>
									</div>
								) : (
									<div className="max-h-[200px] overflow-y-scroll flex flex-col gap-2">
										{translations.map((translation) => (
											<div
												key={translation.translation + translation.pos}
												onClick={() => onChoose(translation)}
												className="flex justify-between items-center p-2 bg-gray-100 rounded
                                                hover:bg-gray-200 cursor-pointer transition-colors"
											>
												<TranslationRecord
													translation={translation.translation}
													pos={translation.pos}
												/>
											</div>
										))}
									</div>
								)}
								<form
									onSubmit={submitWithOwnTranslation}
									className="flex flex-col gap-3 mt-12"
								>
									<h2 className="text-lg font-bold text-gray-700 md:text-base">
										{t("AddYourTranslationT")}
									</h2>
									<div className="flex gap-2 flex-col items-stretch md:flex-row">
										<Input
											placeholder={t("Translation")}
											id="ownTranslation"
											value={ownTranslation}
											onChange={(e) => setOwnTranslation(e.target.value)}
											className="border border-gray-300 bg-blue-50 px-3 py-[5px] hover:border-blue-400 flex items-center justify-between transition-all duration-300"
										/>
										<Dropdown
											options={options}
											label=""
											onOptionSelect={setPartOfSpeech as any}
											selectedValue={partOfSpeech}
											labelClassName="font-bold text-lg lg:text-xl"
											containerClassName="flex"
											selectClassName="bg-blue-50 w-full h-full flex-1 px-1 md:w-36"
											optionClassName="hover:bg-blue-100 flex items-center gap-2"
											iconClassName=""
										/>
									</div>
									<button
										type="submit"
										className="w-full px-4 py-2 text-white bg-[#1E90FF] rounded hover:bg-blue-600 transition-colors"
									>
										{t("AddYourTranslation")}
									</button>
								</form>
							</div>
						</>
					</div>
				</SlidingForm>
			)}
		</>
	);
};

export default TranslationsListPopup;
