import {ChangeEvent} from "react";

export type InputTextProps = {
	type: string | "text",
	value: string;
	containerStyle?: string,
	defaultValue?: string,
	placeholder?: string,
	updateFormValue: (event: ChangeEvent<HTMLInputElement>) => void,
	required?: boolean;
	disabled?: boolean;
	autoComplete?: string;
}


function InputText({
										 type,
										 containerStyle,
										 autoComplete,
										 value,
										 placeholder,
										 updateFormValue,
										 required,
										 disabled
									 }: Readonly<InputTextProps>) {

	return (
		<div className={`form-control ${containerStyle}`}>
			<input autoComplete={autoComplete} disabled={disabled} required={required} placeholder={placeholder}
						 type={type} value={value} onChange={updateFormValue}
						 className="outline-none text-md lg:text-lg border border-[#1154FF] rounded-xl py-3 px-4 border-opacity-80  w-full  bg-white "/>
		</div>
	)
}


export default InputText
