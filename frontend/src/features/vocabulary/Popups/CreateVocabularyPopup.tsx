import {useEffect, useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import {useDispatch, useSelector} from "react-redux";
import {useNavigate} from "react-router-dom";
import Dropdown from "../../../components/Input/Dropdown";
import i18n from "../../../i18nf/i18n";
import {showNotification} from "../../../store/headerSlice";
import {createVocabulary, setCurrentlySelectedVocabulary,} from "../../../store/vocabulary/vocabularySlice";
import {createVocabularyRequest} from "../../../utils/vocabularyUtils";
import SlidingForm from "../../../components/Popup/SlidingForm";
import {selectCurrentVocabularies} from "../../../store/vocabulary/vocabularySelector";

import {ReactComponent as GermanFlag} from '../../../assets/flags/germany.svg';
import {ReactComponent as SpainFlag} from '../../../assets/flags/spain.svg';
import {ReactComponent as UKFlag} from "../../../assets/flags/united_kingdom.svg";
import {ReactComponent as RomanianFlag} from "../../../assets/flags/romania.svg";


export const LANGUAGES = [
	{value: 'ENGLISH', label: "English", flag: <UKFlag/>},
	{value: 'SPANISH', label: "Español", flag: <SpainFlag/>},
	{value: 'GERMAN', label: "Deutsch", flag: <GermanFlag/>},
	{value: 'ROMANIAN', label: "Română", flag: <RomanianFlag/>}
	// {value: 'RUSSIAN', label: "Русский", flag: <img alt="" src=""/>}
];

const CreateVocabularyPopup = ({onClose}: { onClose: () => void }) => {
	const dispatch = useDispatch();
	const navigate = useNavigate();
	const [learningLanguage, setLearningLanguage] = useState<string>("");
	const [nativeLanguage, setNativeLanguage] = useState<string>("");
	const formRef = useRef(null);
	const {t} = useTranslation();
	const [languages, setLanguages] = useState(LANGUAGES);
	const [nativeLanguages, setNativeLanguages] = useState(LANGUAGES);
	const vocabularies = useSelector(selectCurrentVocabularies);

	useEffect(() => {
		const temp = []
		for (const lang of LANGUAGES) {
			const count = vocabularies.reduce((acc, e) => e.learningLanguage?.toLowerCase() === lang.value.toLowerCase() ? acc + 1 : acc, 0)
			if (count < LANGUAGES.length - 1) {
				temp.push(lang);
			}
		}
		if (temp.length === 0) {
			dispatch(showNotification({
				status: "info",
				message: "All possible vocabularies were created"
			}))
			onClose();
			return;
		}
		setLanguages(temp);
	}, [dispatch, onClose, vocabularies]);

	useEffect(() => {
		console.log(vocabularies);
		setNativeLanguages(() => LANGUAGES.filter((lang) => lang.value.toLowerCase() !== learningLanguage.toLowerCase()
			&& !vocabularies.some(e => e.nativeLanguage?.toLowerCase() === lang.value.toLowerCase()
				&& e.learningLanguage?.toLowerCase() === learningLanguage.toLowerCase()))
		);
	}, [learningLanguage, vocabularies]);

	const submitHandler = async (e: any) => {
		e.preventDefault();
		if (learningLanguage === "" || nativeLanguage === "") {
			return;
		}
		if (learningLanguage === nativeLanguage) {
			dispatch(
				showNotification({
					status: 3,
					message: t("CreateVocabularyError"),
				})
			);
			return;
		}
		onClose();
		const res = await createVocabularyRequest({
			learningLanguage: learningLanguage.toUpperCase(),
			nativeLanguage: nativeLanguage.toUpperCase(),
		});
		const response = res.data;

		dispatch(createVocabulary(response));
		dispatch(setCurrentlySelectedVocabulary(response));
		navigate(`/${i18n.language}/vocabularies`);
	};

	const changeLearningLanguage = (value: string) => {
		setLearningLanguage(value);
	};

	const changeNativeLanguage = (value: string) => {
		setNativeLanguage(value);
	};

	return (
		<SlidingForm position="right-[10px] top-[8px]" onClose={onClose}>
			<form
				ref={formRef}
				onSubmit={submitHandler}
				className={`w-full h-full rounded flex flex-col gap-10 justify-start items-center`}
			>
                <span className="p-0 text-xl font-bold m-0">
                  {t("CreateVocabularyText")}
                </span>
				<div className="w-full flex flex-col justify-start font-bold gap-4">
					<Dropdown
						label={t("SelectLearningLanguage")}
						options={languages}
						onOptionSelect={changeLearningLanguage}
						defaultValue={{value: learningLanguage, label: learningLanguage}}
						labelClassName="mb-2 font-semibold text-lg"
						containerClassName="w-full flex flex-col gap-1"
						selectClassName="bg-blue-50 px-1 py-1 "
						optionClassName="hover:bg-blue-100 flex items-center gap-2"
						iconClassName=""
					/>
					<Dropdown
						label={t("SelectNativeLanguage")}
						options={nativeLanguages}
						onOptionSelect={changeNativeLanguage}
						defaultValue={{value: nativeLanguage, label: nativeLanguage}}
						labelClassName="mb-2 font-semibold text-lg"
						containerClassName="w-full flex flex-col gap-1"
						selectClassName="bg-blue-50 px-1 py-1 w-full"
						optionClassName="hover:bg-blue-100 flex items-center gap-2"
						iconClassName=""
					/>
				</div>
				<button
					type="submit"
					className="btn btn-all w-72 bg-color-big-text btn-ghost hover:bg-blue-500 text-white font-bold rounded"
				>
					Create
				</button>
			</form>
		</SlidingForm>
	);
};

export default CreateVocabularyPopup;
