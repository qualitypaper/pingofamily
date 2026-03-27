import React, {useState} from 'react';
import {useTranslation} from 'react-i18next';
import Dropdown from '../Input/Dropdown';

interface OwnTranslationInputProps {
	onSubmit: (translation: string, partOfSpeech: string) => void;
	option: { value: string; label: string; }[];
}

const OwnTranslationInput: React.FC<OwnTranslationInputProps> = ({onSubmit, option}) => {
	const [ownTranslation, setOwnTranslation] = useState('');
	const [partOfSpeech, setPartOfSpeech] = useState('');
	const {t} = useTranslation();

	const handleSubmit = (e: React.FormEvent) => {
		e.preventDefault();
		onSubmit(ownTranslation, partOfSpeech);
		setOwnTranslation('');
	};

	return (
		<form
			onSubmit={handleSubmit}
			className="flex items-center justify-start gap-1 mt-3"
		>
			<div className="w-full flex gap-1">
				<input
					placeholder="Translation"
					value={ownTranslation}
					onChange={(e) => setOwnTranslation(e.target.value)}
					className="p-2 outline-none bg-blue-50 text-color-[#2e425b] w-full lg:w-[20rem] md:w-[15rem] h-[36px] px-1 border border-slate-200
                    rounded placeholder-[#50668f] max-md:text-base text-lg
                    focus:border-blue-500 transition-all duration-300"
				/>

				<Dropdown
					options={option}
					label=""
					onOptionSelect={setPartOfSpeech as any}
					selectedValue={partOfSpeech}
					labelClassName="text-sm lg:text-md"
					containerClassName="flex items-ceneter gap-2"
					selectClassName="bg-blue-50 w-full h-[36px] px-1 md:w-36"
					optionClassName="hover:bg-blue-100 flex items-center "
					selectedOptionClassName="text-sm md:text-lg"
					iconClassName=""
				/>

				<button className="btn-all btn-sm btn-info rounded h-auto w-[40%] text-white bg-[#1E90FF]">
          <span className="font-bold text-sm lg:text-md text-white">
            {t("Add")}
          </span>
				</button>
				{/* <Tooltip describeChild title="Works only if word is in the learning language">
          <IconButton>
            <InfoIcon />
          </IconButton>
        </Tooltip> */}
			</div>
		</form>
	);
};

export default OwnTranslationInput; 
