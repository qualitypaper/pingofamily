const CustomSelect = ({changeValue, label, exceptValues, values}) => {

	return (
		<div className="w-full relative">
			<label className="block text-gray-700 dark:text-gray-300 text-sm">{label}</label>
			<div className="relative z-10 pt-2">
				<select
					onChange={changeValue}
					className="bg-white border border-gray-300 rounded-lg p-1 focus:outline-none w-full">
					<option disabled selected value></option>
					{
						values.filter(val => !exceptValues?.find(e => e.toLowerCase() === val.toLowerCase()))
							.map(e => (
								<option key={e} className="bg-white dark:bg-gray-800">{e}</option>
							))
					}
				</select>
			</div>
		</div>
	);
};

export default CustomSelect;