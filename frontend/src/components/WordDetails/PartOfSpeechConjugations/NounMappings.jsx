import ConjugationsCard from "../ConjugationsCard";

export const ObjectValue = ({object}) => {
	return Object.entries(object).map(([key, value]) => {
		return (
			<div
				key={key}
				className="whitespace-nowrap flex items-center justify-start ml-2 text-sm"
			>
				<span className="capitalize mr-3">{key}:</span>
				<span>{value}</span>
			</div>
		);
	});
};

const NounMappings = ({mappings}) => {
	return (
		<div className="flex flex-wrap">
			{Object.entries(mappings).map((item) => {
				const key = item[0];
				const value = item[1];

				if (typeof value === "string") {
					return <ConjugationsCard key={key} keyValue={key} value={value}/>;
				}
				return (
					<ConjugationsCard
						key={key}
						keyValue={key}
						centerPlacement={false}
						value={<ObjectValue object={value}/>}
					/>
				);
			})}
		</div>
	);
};

export default NounMappings;
