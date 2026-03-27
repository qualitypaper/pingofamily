import { useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch, useSelector } from "react-redux";
import Input from "../../../components/Input/Input";
import SlidingForm from "../../../components/Popup/SlidingForm";
import { selectCurrentlySelected } from "../../../store/vocabulary/vocabularySelector";
import { createVocabularyGroupR } from "../../../store/vocabulary/vocabularySlice";
import { createVocabularyGroup } from "../../../utils/vocabularyUtils";

type CreateVocabularyGroupProps = {
	onClose: () => void;
}
const CreateVocabularyGroupPopup = ({ onClose }: CreateVocabularyGroupProps) => {
	const dispatch = useDispatch();
	const [textForm, setTextForm] = useState('');
	const formRef = useRef(null);
	const vocabulary = useSelector(selectCurrentlySelected)
	const { t } = useTranslation();
	const changeTextForm = (value: string) => {
		setTextForm(value)
	}

	const submitHandler = async (e: React.FormEvent) => {
		e.preventDefault();
		onClose();
		const response = await createVocabularyGroup({ vocabularyId: vocabulary.id, name: textForm });
		dispatch((createVocabularyGroupR(response.data)));
	}
	return (
		<SlidingForm position="right-[10px] top-[8px]" onClose={onClose}>
			<form ref={formRef} onSubmit={submitHandler}
				className={`w-full h-full rounded flex flex-col gap-10 justify-start items-center`}>
				<span className='p-0 text-xl font-bold m-0'>{t("CreateVocabularyGroup")}</span>
				<div className="w-full flex flex-col justify-start font-bold gap-4">
					<Input className="border-2 border-gray-300" placeholder={t("VocabularyGroupName")}
						onChange={(e: any) => changeTextForm(e.target.value)} />
				</div>
				<button
					className="btn btn-all w-72 bg-color-big-text btn-ghost hover:bg-blue-500 text-white font-bold rounded"
					type="submit">

					{t("CreateVocabularyGroupText")}
				</button>
			</form>
		</SlidingForm>
	)
}

export default CreateVocabularyGroupPopup
