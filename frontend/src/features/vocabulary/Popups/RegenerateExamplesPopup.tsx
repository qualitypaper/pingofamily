import {useRef, useState} from 'react';
import {Difficulty} from '../../../custom';
import SlidingForm from '../../../components/Popup/SlidingForm';
import {useTranslation} from "react-i18next";
import Dropdown from "../../../components/Input/Dropdown";

export type RegenerateExamplesType = {
	regenerateExamples: (difficulty: Difficulty) => void;
	onClose: () => void;
}

function RegenerateExamplesPopup({regenerateExamples, onClose}: Readonly<RegenerateExamplesType>) {
	const [difficulty, setDifficulty] = useState<Difficulty>('EASY');
	const [isOpen, setIsOpen] = useState(false);
	const selectRef = useRef<HTMLDivElement>(null);
	const {t} = useTranslation();

	const difficultyOptions = [
		{value: 'EASY', label: t('Easy')},
		{value: 'MEDIUM', label: t('Medium')},
		{value: 'HARD', label: t('Hard')},
	];

	const handleRegenerateExamples = () => {
		regenerateExamples(difficulty);
		onClose();
	};

	const handleOptionClick = (value: string) => {
		setDifficulty(value as Difficulty);
		setIsOpen(false);
	};

	return (
		<SlidingForm onClose={onClose} showCloseButton={false}>
			<div className="flex flex-col items-center gap-6 ">
				<div className="flex flex-col gap-4 m-4">
					<Dropdown
						options={difficultyOptions}
						label={t("RegenerateExamples")}
						onOptionSelect={handleOptionClick}
						selectedValue={difficulty}
						isOpen={isOpen}
						setIsOpen={setIsOpen}
						selectRef={selectRef}
						labelClassName="mb-2 font-bold text-lg lg:text-xl"
						containerClassName=" flex flex-col gap-4"
						selectClassName="bg-blue-50 px-3 py-2"
						optionClassName="hover:bg-blue-100 flex items-center gap-2"
						iconClassName=""
					/>
				</div>
				<button
					type="submit"
					onClick={handleRegenerateExamples}
					className="w-full bg-[#1E90FF] hover:bg-blue-600 text-white font-bold py-2 px-4 rounded"
				>
					{t("RegenerateExamples")}
				</button>
			</div>
		</SlidingForm>
	);
}

export default RegenerateExamplesPopup;
