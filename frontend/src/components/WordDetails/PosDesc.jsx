import TitleCard from "../Cards/TitleCard"
import {useTranslation} from "react-i18next";

const Desc = ({desc, descTranslation}) => {
	return (
		<div className="flex  gap-4 max-md:gap-2 max-md:text-base ">
			<div
				className="text-xs flex flex-wrap font-medium gap-4 max-md:gap-2 max-md:text-base lg:text-xs  break-all ">
                <span
									className="break-all   text-md flex text-color-big-text lg:text-xl md:text-lg font-semibold ">{desc}</span>
				<span className="break-all   text-md flex lg:text-xl md:text-lg font-semibold">{descTranslation}</span>
			</div>

		</div>

	)
}

const PosDesc = ({desc, descTranslation}) => {
	const {t} = useTranslation()
	return (
		<div>
			<TitleCard title={t("DescriptionText")} className="flex-row">
				<Desc desc={desc} descTranslation={descTranslation}/>
			</TitleCard>
		</div>
	)
}

export default PosDesc