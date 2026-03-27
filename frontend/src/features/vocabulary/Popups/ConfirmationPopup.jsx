import SlidingForm from "../../../components/Popup/SlidingForm";
import {useTranslation} from "react-i18next";

const ConfirmationPopup = ({onClose, onOk, message}) => {
	const {t} = useTranslation();

	// const constructMessage = () => {
	//   const words = [];

	//   for (let word of message.split(" ")) {
	//     words.push(<span key={word + Math.random() * 12341243}>{word + " "}</span>);
	//   }

	//   return words;
	// };

	return (
		<SlidingForm onClose={onClose} position="right-2 top-2">
			<div
				className="w-full max-w-xs sm:max-w-md h-auto rounded flex flex-col gap-6 justify-start items-start p-6 bg-white">
				<span className="text-lg font-bold">{message}</span>
				<div className="flex justify-end items-center gap-4 w-full">
					<button
						onClick={onOk}
						className="btn-all flex-1 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 transition duration-300"
					>
						<span className="p-[3px] text-white">{t('DeleteVocabularyGroupConfirm')}</span>
					</button>
					<button
						onClick={onClose}
						className="flex-1 px-4 py-2 bg-gray-300 text-gray-700 rounded hover:bg-gray-400 transition duration-300"
					>
						{t('DeleteVocabularyGroupCancel')}
					</button>
				</div>
			</div>
		</SlidingForm>

	);
};

export default ConfirmationPopup;
