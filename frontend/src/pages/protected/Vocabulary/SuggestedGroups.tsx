import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import VocabularyGroupCard from "../../../components/Cards/VocabularyGroupCard";
import i18n from "../../../i18nf/i18n";
import { Language } from "../../../store/language/languageTypes";
import { VocabularyGroup } from "../../../store/vocabulary/vocabularyTypes";
import { getLocalizedLanguageName } from "../../../utils/globalUtils";
import { groupBy } from "../../../utils/trainingUtils";

type SuggestedGroupsProps = {
	suggestedVocabularyGroups: VocabularyGroup[];
};

function SuggestedGroups({ suggestedVocabularyGroups }: SuggestedGroupsProps) {
	const { t } = useTranslation();
	const [groupedGroups, setGroupedGroups] = useState(
		groupBy(suggestedVocabularyGroups, (e) => e.learningLanguage),
	);

	useEffect(() => {
		setGroupedGroups(
			groupBy(suggestedVocabularyGroups, (e) => e.learningLanguage),
		);
	}, [suggestedVocabularyGroups]);

	console.log(groupedGroups.entries())

	return (
		<div className="mt-4">
			<div className="flex justify-between items-center mb-8 sm:mb-4">
				<h1 className="text-2xl font-bold">{t("SuggestedList")}</h1>
			</div>
			{Array.from(groupedGroups.entries()).map(
				([lang, record]: [Language | undefined, VocabularyGroup[]]) => (
					<div
						className="flex flex-col gap-5 mb-5"
						key={lang ?? record.join(";")}
					>
						{groupedGroups.size > 1 && (
							<h1
								className="text-xl font-semibold">{`${t("MultipleSuggestedLists")} ${getLocalizedLanguageName(lang as Language, i18n.language)}`}</h1>
						)}
						<div className="vocabulary-group">
							{record?.map((vocabularyGroup: VocabularyGroup) => (
								<VocabularyGroupCard
									key={vocabularyGroup.groupId}
									vocabularyGroup={vocabularyGroup}
								/>
							))}
						</div>
					</div>
				),
			)}
			{suggestedVocabularyGroups.length === 0 && <h1>Nothing to offer :(</h1>}
		</div>
	);
}

export default SuggestedGroups;
