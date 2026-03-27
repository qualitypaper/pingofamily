import ConjugationsCard from "../ConjugationsCard";
import NounMappings from "./NounMappings";
import {useEffect} from "react";
import {useDispatch} from "react-redux";
import {setCurrentlyInspectedWordGender} from "../../../store/vocabulary/vocabularySlice";

const NounConjugation = ({conjugation}) => {
	const dispatch = useDispatch();
	useEffect(() => {
		dispatch(
			setCurrentlyInspectedWordGender(conjugation?.conjugation?.gender ?? ""),
		);
	}, [conjugation, dispatch]);

	if (!conjugation || !conjugation?.conjugation) return null;
	const {
		gender,
		mapping,
		mappings,
		pluralFeminine,
		singularFeminine,
		plural,
	} = conjugation?.conjugation;

	return (
		<>
			<div className="flex justify-start items-start">
				{gender && <ConjugationsCard keyValue="Gender" value={gender}/>}
				{plural && <ConjugationsCard keyValue="Plural" value={plural || ""}/>}
				{singularFeminine && (
					<ConjugationsCard
						keyValue="Singular feminine"
						value={singularFeminine || ""}
					/>
				)}
				{pluralFeminine && (
					<ConjugationsCard
						keyValue="Plural feminine"
						value={pluralFeminine || ""}
					/>
				)}
			</div>
			{mapping && <NounMappings mappings={mapping}/>}
			{mappings && <NounMappings mappings={mappings}/>}
		</>
	);
};

export default NounConjugation;
