import { InputHTMLAttributes, Ref, useMemo } from "react";

export type TrainingInputProps = {
	correctValue: string;
	width: string;
	value?: string;
	trainingExampleId: number;
	defaultValue?: string;
	correct: boolean;
	ref: Ref<HTMLInputElement>
} & InputHTMLAttributes<HTMLInputElement>;

const TrainingInput = ({
	width,
	value,
	trainingExampleId,
	defaultValue,
	correct,
	correctValue,
	ref,
	...rest
}: TrainingInputProps) => {
	const measuredProps: InputHTMLAttributes<HTMLInputElement> = useMemo(() => ({
		tabIndex: 0,
		autoFocus: true,
		readOnly: correct,
		ref: ref,
		value: value ?? defaultValue,
		autoComplete: "off",
		autoCorrect: "off",
		spellCheck: "false",
		autoCapitalize: "off",
		className: `training-input my-3 ml-2
					${correct
				? "bg-green-50 border-2 border-green-400"
				: "bg-slate-50 border-2 border-blue-200"
			}`,
		...rest
	}), [correct, defaultValue, ref, rest, value]);

	// const Measured = (p: InputHTMLAttributes<HTMLInputElement>) => <input {...p} />
	// const { size, portal } = usePrerenderSize(Measured, measuredProps);

	return (
		<>
			{/* {portal} */}
			<input
				{...measuredProps}
				style={{ width: width }}
			/>
		</>
	);
}

export default TrainingInput;
