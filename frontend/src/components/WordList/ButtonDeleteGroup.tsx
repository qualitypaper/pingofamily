import React from "react";
import {ReactComponent as DeleteIcon} from "../../assets/icons/delete.svg";
import ConfirmationPopup from "../../features/vocabulary/Popups/ConfirmationPopup";
import {useTranslation} from "react-i18next";

export type ButtonDeleteGroupProps = {
	onClose: () => void;
	onOK: () => void;
	showForm: () => void;
	isFormVisible: boolean;
}

export const ButtonDeleteGroup = ({showForm, onClose, onOK, isFormVisible}: ButtonDeleteGroupProps) => {
	const {t} = useTranslation();
	return (
		<div className="flex items-start gap-2 cu-p mt-0 lg:mt-4 sm:hidden">
			<div
				onClick={showForm}
				className="px-6 py-2 flex justify-center items-center gap-2 w-full  lg:w-full text-white bg-red-500 rounded-md cursor-pointer hover:bg-red-500
                 transition duration-300 ease-in-out">
				<DeleteIcon className="w-5 h-5"/>
				<span
					className="font-bold text-center text-sm text-white lg:text-lg whitespace-nowrap justify-end">{t("DeleteVocabularyGroup")}</span>
			</div>
			{isFormVisible && (
				<>
					<ConfirmationPopup onClose={onClose} onOk={onOK}
														 message={t("DeleteVocabularyGroupText")}/>
				</>
			)}
		</div>
	)
}
