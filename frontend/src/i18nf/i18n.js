import i18n from "i18next";
import I18nextBrowserLanguageDetector from "i18next-browser-languagedetector";
import {initReactI18next} from "react-i18next";
import english from "./language/english";
import german from "./language/german";
import spanish from "./language/spanish";

i18n
	.use(initReactI18next)
	.use(I18nextBrowserLanguageDetector)
	.init({
		resources: {
			en: {
				translation: english,
			},
			de: {
				translation: german,
			},
			es: {
				translation: spanish,
			},
		},
		lng: "en",
		fallbackLng: ["en"],
		keySeparator: false,
		react: {
			useSuspense: false,
		},
		interpolation: {
			escapeValue: false,
		},
	});

export default i18n;
