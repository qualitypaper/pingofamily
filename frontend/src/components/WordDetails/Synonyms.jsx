import TitleCard from "../Cards/TitleCard"
import {useTranslation} from "react-i18next";

const Synonyms = ({synonyms}) => {
	const {t} = useTranslation()
	if (!synonyms) return <></>

	return (
		<div>
			<TitleCard title={t("Synonyms")} classname>
				<div
					className={`grid grid-rows-4 text-sm lg:text-lg  grid-flow-col max-sm:grid-flow-row max-sm:grid-cols-2`}>
					{
						synonyms.map((item, index) => <span key={item + String(index)}
																								className="p-1 text-black">{item}</span>)
					}
				</div>
			</TitleCard>
		</div>
	)
}

export default Synonyms