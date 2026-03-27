import {pickIcon} from "../../utils/iconUtils";

export function StandardVocabularyListing({
																						vocabulary,
																						setSelectedVocabulary,
																						setOpen,
																						icon,
																						selectedStyles,
																						clickHandler,
																						toUrl,
																					}) {
	const onClickVocabulary = async (vocabulary) => {
		await clickHandler(vocabulary);
		setSelectedVocabulary(pickIcon(vocabulary.learningLanguage, true));
		setOpen(false);
	};

	return (
		<button
			className={`ease-out duration-300 hover:bg-slate-200 hover:cursor-pointer ${selectedStyles}`}
			onClick={() => onClickVocabulary(vocabulary)}
		>
			<div className="flex items-center pr-4 pl-4 pt-3 pb-3">
				<div className="flex flex-col gap-1 relative">
					<div className={`flex flex-col`}>
						{icon}
						<p className="text-sm font-bold">{vocabulary.learningLanguage}</p>
					</div>
				</div>
			</div>
		</button>
	);
}

export default StandardVocabularyListing;
