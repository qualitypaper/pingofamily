import { useTranslation } from "react-i18next";
import { ItemPairContainer } from "../../features/vocabulary/SpanContainer";
import { WordExampleTranslation } from "../../store/training/trainingTypes";
import TitleCard from "../Cards/TitleCard";

type ExamplesProps = {
	wordExample: WordExampleTranslation;
	imageUrl: string;
}

const Examples = ({ wordExample, imageUrl }: ExamplesProps) => {
	const { t } = useTranslation();

	return (
		<div className="flex gap-2 w-full max-sm:items-center">
			<TitleCard title={t("Examples")} className="flex-row">
				<div className={`flex justify-between gap-1 mr-2 flex-wrap lg:flex-nowrap`}>
					<div
						className={`flex flex-col    gap-1 mr-2  w-full  text-gray-900 dark:text-slate-300 `}>
						<ItemPairContainer item1={wordExample?.example ?? ''}
							item2={wordExample?.exampleTranslation ?? ''}
							additionalStyles1="font-semibold"
							soundUrl={wordExample.soundUrl ?? ""}
							additionalStyles2="font-semibold"
						/>
					</div>

				</div>
			</TitleCard>
			<img className="w-60 h-30 border-2 border-gray-300 max-lg:w-28 max-lg:h-24" src={imageUrl} alt="" />
		</div>
	)
}

export default Examples
