import {ObjectValue} from "./NounMappings";

const VerbConjugation = ({conjugation}) => {

	if (!conjugation || !conjugation.conjugation) {
		return null;
	}

	return (
		<>
			{conjugation?.conjugation?.map((e) => {
				if (!e) return null;

				return (
					<div
						key={e.tense}
						className="card p-4 bg-slate-100 dark:bg-slate-700 m-3  border border-slate-200 shadow-custom-blue"
					>
						{/*shadow-xl*/}
						<span className="mr-3 font-semibold ">{e.tense}:</span>
						<ObjectValue object={e.tenseConjugations}/>
					</div>
				);
			})}
		</>
	);
};

export default VerbConjugation;
