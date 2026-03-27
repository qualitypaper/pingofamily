import {VocabularyGroup} from "../../store/vocabulary/vocabularyTypes";
import i18n from "../../i18nf/i18n";
import {mapDefaultVocabularyName} from "../../utils/vocabularyUtils";

export const VocabularyGroupContent = ({
																				 vocabularyGroup,
																			 }: {
	vocabularyGroup: VocabularyGroup;
}) => {
	return (
		<div className="flex flex-col gap-4 w-full">
			<div className="flex gap-4 w-full">
				<img
					className="w-12 h-12 ml-2 mb-2 relative md:w-16 md:h-16"
					src={vocabularyGroup.imageUrl}
					alt=""
				/>
				<div className="flex flex-col gap-1 w-full ">
					<div className="flex items-start flex-col ">
            <span className="text-lg  w-48 font-bold md:text-xl truncate dark:text-gray-300">
              {vocabularyGroup.name?.includes("VOCABULARY")
								? vocabularyGroup.name?.substring(0, 1).toUpperCase() +
								vocabularyGroup.name?.substring(
									1,
									vocabularyGroup.name?.indexOf("VOCABULARY"),
								) +
								mapDefaultVocabularyName(i18n.language)
								: vocabularyGroup.name}
            </span>
						<span className="text-sm">{vocabularyGroup?.wordsNumber}</span>
					</div>
				</div>
			</div>
		</div>
	);
};
