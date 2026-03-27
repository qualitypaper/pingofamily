import {Textarea} from "@chakra-ui/react";
import {InputLableProps} from "./InputLabel";
import {ForwardedRef, forwardRef, TextareaHTMLAttributes} from "react";

type TextAreaInputProps = {
	containerStyle?: string;
	defaultValue?: string;
	placeholder?: string;
	value: string;
	updateFormValue: (value: string) => void;
	disabled: boolean;
} & InputLableProps &
	TextareaHTMLAttributes<HTMLTextAreaElement>;

const TextAreaInput = forwardRef(
	(
		{
			labelValue,
			labelStyle,
			disabled,
			containerStyle,
			value,
			placeholder,
			updateFormValue,
		}: TextAreaInputProps,
		ref: ForwardedRef<HTMLTextAreaElement>
	) => {
		const updateInputValue = (val: string) => {
			updateFormValue(val);
		};

		return (
			<div className={`form-control ${containerStyle}`}>
				<label className="label">
          <span
						className={"label-text text-base-content text-center " + labelStyle}
					>
            {labelValue}
          </span>
				</label>
				<Textarea
					required
					ref={ref}
					disabled={disabled}
					autoComplete="off"
					autoCapitalize="off"
					autoCorrect="off"
					spellCheck={false}
					value={value}
					className={`textarea border-2 border-slate-300 h-[100px] lg:w-[500px] md:w-[400px] 
            max-md:w-[300px]  text-base ${
						disabled ? "bg-slate-200" : "bg-white"
					}`}
					placeholder={placeholder || ""}
					onChange={(e) => updateInputValue(e.target.value)}
				/>
			</div>
		);
	}
);

export default TextAreaInput;
