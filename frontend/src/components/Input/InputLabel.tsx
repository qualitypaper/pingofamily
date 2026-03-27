export type InputLableProps = {
	labelValue: string,
	labelStyle: string,
}

const InputLabel = ({labelValue, labelStyle}: InputLableProps) => {
	return (
		<label className="label inline-block ">
			<span className={"label-text text-base-content " + labelStyle}>{labelValue}</span>
		</label>
	)
}
export default InputLabel