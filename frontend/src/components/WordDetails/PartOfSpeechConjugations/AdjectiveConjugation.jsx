import ConjugationsCard from "../ConjugationsCard";

const AdjectiveConjugation = ({conjugation}) => {
	if (!conjugation || !conjugation.conjugation) return <div></div>;

	return (
		<>
			{Object.entries(conjugation?.conjugation).map((item) => {
				const key = item[0];
				const value = item[1];

				return <ConjugationsCard key={key} keyValue={key} value={value}/>;
			})}
		</>
	);
};

export default AdjectiveConjugation;

