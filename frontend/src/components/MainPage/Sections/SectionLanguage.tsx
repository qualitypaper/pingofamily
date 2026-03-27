import React, {ReactNode, useState} from "react";
import {ReactComponent as UKFlag} from "../../../assets/flags/united_kingdom.svg"
import {ReactComponent as SpainFlag} from "../../../assets/flags/spain.svg"
import {ReactComponent as GermanFlag} from "../../../assets/flags/germany.svg"

import {useTranslation} from "react-i18next";
import {TFunction} from "i18next";
import penguinSkates from "../../../assets/penguins/fat_globe.svg"

import StartLearning from "./StartLearning";
import england from "../../../assets/backgrounds/england.png"
import germany from "../../../assets/backgrounds/german.png"
import spain from "../../../assets/backgrounds/spain.png"


export interface LanguagesProps {
	value: string
	label: string
	img: string
	flag: ReactNode;
}

export const LanguageButton = ({sectionName, icon, t, onClick}: {
	sectionName: string,
	icon: ReactNode,
	t: TFunction<"translation", null>,
	onClick: () => void
}) => {

	return (
		<div
			onClick={onClick}
			className="btn-flag hover:bg-blue-50 transition-all duration-100">
			<div className="flex items-center gap-1">
				{icon}
				<h2 className="text-lg font-semibold">{t(sectionName)}</h2>
			</div>

		</div>
	)
}
const SectionLanguage = () => {
	const {t} = useTranslation();
	const [selectedLanguage, setSelectedLanguage] = useState<string | null>(null);

	const languages: LanguagesProps[] = [
		{
			value: t("SectionLanguageOne"),
			label: t("TextInLanguagePopupEnglish"),
			flag: <UKFlag width="3.3rem" height="3.3rem"/>,
			img: england
		},
		{
			value: t("SectionLanguageTwo"),
			label: t("TextInLanguagePopupSpanish"),
			flag: <SpainFlag className="" width="3.3rem" height="3.3rem"/>,
			img: spain
		},
		{
			value: t("SectionLanguageThree"),
			label: t("TextInLanguagePopupGerman"),
			flag: <GermanFlag className="" width="3.3rem" height="3.3rem"/>,
			img: germany
		},
	]

	const languagesButton = [
		{value: t("SectionLanguageOne"), flag: <UKFlag/>},
		{value: t("SectionLanguageTwo"), flag: <SpainFlag/>},
		{value: t("SectionLanguageThree"), flag: <GermanFlag/>},
	]


	const openPopup = (word: string) => {
		setSelectedLanguage(word)
	}

	const closePopup = () => {
		setSelectedLanguage(null)
	}


	return (
		<>
			<section className="bg-[#4FA3E6] from-blue-800 via-blue-500 to-blue-800">
				<div className="max-w-[64rem] px-4 pt-0 pb-4 xl:p-0 mx-auto
					gap-0 sm:gap-0 lg:gap-24 flex justify-between items-center sm:items-start flex-col sm:flex-row">
					<div>
						<img src={penguinSkates} alt="Penguin"
								 className="mt-0 w-[100%] h-[100%] sm:w-[27em] lg:mt-3 max-w-[64rem] m-auto py-[18.3px] flex justify-between max-md:justify-around items-center "/>
					</div>
					<div className="p-4 lg:p-0">
						<h3 className="font-bold mb-8 mt-4 text-4xl lg:text-5xl text-white ">{t("SectionLanguageMainText")}
							<span
								className="text-blue-100"> {t("SectionLanguageContinuedText")}</span></h3>
						<div className="grid grid-cols-1 md:grid-cols-2">
							{languagesButton.map((e, index) =>
								<LanguageButton key={index} sectionName={e.value} icon={e.flag} t={t}
																onClick={() => openPopup(e.value)}/>
							)}
						</div>
					</div>
				</div>
			</section>
			{selectedLanguage && <StartLearning onClose={closePopup} languages={languages} open={!!selectedLanguage}
																					selectedLanguage={selectedLanguage}/>}
		</>

	)
}

export default SectionLanguage
