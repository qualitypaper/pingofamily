import {useTranslation} from "react-i18next";
import {useSelector} from "react-redux";
import {Link} from "react-router-dom";
import {ViewListButton} from "../../pages/protected/Vocabulary/VocabularyGroupList";
import {VocabularyGroupContent} from "./VocabularyGroupContent";
import {ButtonBack} from "../Button/ButtonBack";
import React from "react";
import {RootState} from "../../app/store";

const MoreSuggestedGroupsComponent = () => {
	const {t} = useTranslation();
	const suggestedVocabularyGroups = useSelector((state: RootState) => state.vocabulary.suggestedVocabularyGroups);

	return (
		<>
			<ButtonBack className="lg:pt-4"/>
			<div>
				<h1 className="text-2xl mt-4 font-bold">{t("SuggestedList")}</h1>
				<div className="vocabulary-group ">
					{suggestedVocabularyGroups?.map(vocabularyGroup => (
						<section
							className="rounded-xl z-10 mt-4 hover:z-30 pt-[0.7rem] pb-4 shadow w-full bg-blue-100 hover:bg-white hover:shadow-xl"
							key={vocabularyGroup.groupId}
						>
							<Link to={`${vocabularyGroup.groupId || 0}`}>
								<VocabularyGroupContent vocabularyGroup={vocabularyGroup}/>
							</Link>
							<div
								className="vocabulary-group-block flex  mt-0 max-h-0 opacity-0 overflow-hidden p-0 invisible justify-center items-start gap-4"
							>
								<ViewListButton classname="w-full " title={t("ViewList")}
																vocabularyGroup={vocabularyGroup}/>
							</div>
						</section>
					))}
				</div>
			</div>
		</>
	);
};


export default MoreSuggestedGroupsComponent;
