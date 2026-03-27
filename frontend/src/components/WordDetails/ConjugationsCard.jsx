import React from 'react';

const ConjugationsCard = ({keyValue, value, centerPlacement}) => {

	return (
		<div className="card p-6 bg-slate-100 dark:bg-slate-700 m-5">
			<div className="text-black capitalize">{keyValue}:</div>
			<span
				className={`${centerPlacement && 'grid place-items-center'} mt-3 whitespace-nowrap max-md:text-lg max-sm:text-sm`}>{value}</span>
		</div>
	)
}

export default ConjugationsCard