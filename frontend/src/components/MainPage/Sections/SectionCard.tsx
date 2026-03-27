import {CardMain} from "../CardMain";
import {ReactComponent as Search} from "assets/icons/search.svg";
import {ReactComponent as Earth} from "assets/icons/earth.svg";
import {ReactComponent as Mountain} from "assets/icons/mountains.svg";
import {ReactComponent as Academic} from "assets/icons/academic.svg";
import {ReactComponent as Horse} from "assets/icons/horse.svg";
import {useTranslation} from "react-i18next";


export const SectionCard = () => {
	const {t} = useTranslation();

	const cardName = [
		{
			mainText: t("SectionCardSubTextMainOne"),
			subText: t('SectionCardSubTextOne'),
			img: <Search className="w-[5rem] h-[5rem]"/>
		},
		{
			mainText: t("SectionCardSubTextMainTwo"),
			subText: t('SectionCardSubTextTwo'),
			img: <Earth className="w-[5rem] h-[5rem]"/>
		},
		{
			mainText: t("SectionCardSubTextMainThree"),
			subText: t('SectionCardSubTextThree'),
			img: <Mountain className="w-[5rem] h-[5rem]"/>
		},
		{
			mainText: t("SectionCardSubTextMainFour"),
			subText: t('SectionCardSubTextFour'),
			img: <Academic className="w-[5rem] h-[5rem]"/>
		},
		{
			mainText: t("SectionCardSubTextMainFive"),
			subText: t('SectionCardSubTextFive'),
			img: <Horse className="w-[5rem] h-[5rem]"/>
		},
	]
	return (
		<section className="w-full h-[100%]  mt-18">
			<div className="content w-full max-w-[64rem] flex flex-col m-auto justify-between">
				<div className="text-center mb-10">
					<h2 className="font-bold pt-12 pb-0 text-4xl lg:text-5xl lg:pb-6  p-4 xl:p-0">{t("SectionCardMainText")}</h2>
				</div>
				<div
					className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 lg:gap-4 p-4 xl:p-0 lg:pb-24 relative ">
					{cardName.map((value, index) => {
						return <CardMain key={index} mainText={value.mainText} subText={value.subText} img={value.img}/>
					})}
				</div>
			</div>
		</section>
	)
}
