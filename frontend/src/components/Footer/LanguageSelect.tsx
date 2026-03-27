import { API } from "app/init";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useLocation, useNavigate } from "react-router-dom";
import { LANGUAGES } from "../../features/vocabulary/Popups/CreateVocabularyPopup";
import i18n from "../../i18nf/i18n";
import { selectIsAuthenticated } from "../../store/user/userSelector";
import { getFullLanguageNameForRequest, getShortLanguageName } from '../../utils/globalUtils';
import Dropdown from "../Input/Dropdown";

const LanguageSelect = ({ displayLabel = false }: { displayLabel?: boolean }) => {
	const isAuthenticated = useSelector(selectIsAuthenticated);
	const navigate = useNavigate();
	const location = useLocation();
	const [selectLanguage, setSelectLanguage] = useState(i18n.language);

	useEffect(() => {
		const currentLang = i18n.language;
		setSelectLanguage(getFullLanguageNameForRequest(currentLang));
		for (let i = 0; i < LANGUAGES.length; i++) {
			const e = LANGUAGES[i];
			if (i18n.language === e.value) {
				const temp = LANGUAGES[0];
				LANGUAGES[0] = e;
				LANGUAGES[i] = temp;
				break;
			}
		}
	}, []);


	const handleLanguageChange = async (value: string) => {
		const newLanguage = getShortLanguageName(value);

		const newPathname = location.pathname.replace(`/${i18n.language}`, `/${newLanguage}`);
		navigate(newPathname, { replace: true });
		window.history.replaceState({}, '', newPathname);

		i18n.changeLanguage(newLanguage);

		if (!isAuthenticated) return;
		await API.get(`/user/change-interface-language?language=${getFullLanguageNameForRequest(newLanguage)}`);
	};

	return (
		<Dropdown
			displayLabel={displayLabel}
			label=""
			options={LANGUAGES}
			onOptionSelect={handleLanguageChange}
			selectedValue={selectLanguage}
			defaultValue={LANGUAGES.find(e => e.value === getFullLanguageNameForRequest(i18n.language))}
			labelClassName="mb-2 font-bold text-lg lg:text-xl"
			containerClassName="my-custom-container "
			selectedOptionClassName="font-bold lg:text-lg hidden md:block"
			selectClassName="bg-blue-50 px-3 py-2 w-full lg:max-w-[170px] rounded-md"
			optionClassName="hover:bg-blue-100 flex items-center gap-2"
			iconClassName="hidden md:block "
		/>
	)
};

export default LanguageSelect;
