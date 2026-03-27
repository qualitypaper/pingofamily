import TitleCard from "../Cards/TitleCard"
import AdjectiveConjugation from "./PartOfSpeechConjugations/AdjectiveConjugation";
import VerbConjugation from "./PartOfSpeechConjugations/VerbConjugation";
import NounConjugation from "./PartOfSpeechConjugations/NounConjugation";
import {useTranslation} from "react-i18next";
import {IConjugation, PartOfSpeech} from "../../store/training/trainingTypes";

type ConjugationProps = {
	conjugation: IConjugation;
	pos: PartOfSpeech;
}

const Conjugation = ({conjugation, pos}: ConjugationProps) => {
	const {t} = useTranslation();
	if (pos === "OTHER") return <></>;

	return (
		<TitleCard title={t("ConjugationText")} border className="w-full">
			<div className="grid  ">
				{
					{
						"VERB": <VerbConjugation conjugation={conjugation}/>,
						"NOUN": <NounConjugation conjugation={conjugation}/>,
						'ADJECTIVE': <AdjectiveConjugation conjugation={conjugation}/>,
						'PROPN': <NounConjugation conjugation={conjugation}/>,
					}[pos]
				}
			</div>
		</TitleCard>
	)
}

export default Conjugation