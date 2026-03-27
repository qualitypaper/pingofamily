import {useSelector} from "react-redux";
import {selectCurrentlySelected} from "../../store/vocabulary/vocabularySelector";

const specialCharacters: { [key: string]: string[] } = {
	ENGLISH: [],
	GERMAN: ['ß', 'ä', 'ö', 'ü'],
	SPANISH: ['á', 'é', 'í', 'ó', 'ú', 'ü', 'ñ'],
};

interface SpecialCharactersProps {
	onCharacterClick: (char: string) => void;
}

const SpecialCharactersBlock: React.FC<SpecialCharactersProps> = ({onCharacterClick}) => {
	const vocabulary = useSelector(selectCurrentlySelected);
	const characters = specialCharacters[vocabulary.learningLanguage ?? ""] || [];

	return (
		<div className="mt-2 absolute top-10 right-3">
			<div
				className="flex flex-col items-center align-middle  z-50 gap-2 p-4 bg-white rounded-md shadow-lg w-full lg:w-56">
				<span className="font-bold text-lg lg:text-xl">{vocabulary.learningLanguage}</span>
				{characters.length > 0 ? (
					<div className="grid grid-cols-1 lg:grid-cols-4 gap-2 items-center">

						{characters.map((char: string) => (
							<button
								key={char}
								onClick={() => onCharacterClick(char)}
								className="px-3.5 py-1  rounded-lg bg-slate-50 border-2 border-blue-200 hover:bg-slate-100 duration-300 hover:border-blue-500"
							>
								{char}
							</button>
						))}
					</div>
				) : (
					<span className="text-gray-500">No special characters available for this language</span>
				)}
			</div>
		</div>
	);
};

export default SpecialCharactersBlock;
