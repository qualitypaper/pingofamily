import {TranslationJson} from "../../../store/vocabulary/vocabularyTypes";
import SlidingForm from "../../../components/Popup/SlidingForm";

export type TranslationListProps = {
	translations: TranslationJson[];
	onClose: () => void;
	inspectedWord: string;
	onChoose: (translationJson: TranslationJson) => void;
}

const TranslationsListPopup = ({translations, onClose, inspectedWord, onChoose}: TranslationListProps) => {

	return (
		<SlidingForm onClose={onClose} position="right-6 top-3">
			<div className="flex flex-col">
				<h1 className="text-xl max-md:text-lg font-semibold">Possible translations</h1>
				<span className="font-semibold m-[2px]">{inspectedWord}</span>
				<div className="flex flex-col mt-2 gap-1">
					{
						translations.map(translation => (
							<div key={translation.translation + translation.pos} onClick={() => onChoose(translation)}
									 className="leading-none btn btn-sm text-neutral normal-case border-1 border-slate-200 btn-ghost justify-start items-end">
                                <span className="text-lg">
                                    {translation.translation}
                                </span>
								<span className="ml-2 text-md mb-1">{translation.pos}</span>
							</div>
						))
					}
				</div>
			</div>
		</SlidingForm>
	)
}

export default TranslationsListPopup
