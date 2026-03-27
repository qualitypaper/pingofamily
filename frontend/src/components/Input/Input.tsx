import {Input as ChakraInput} from "@chakra-ui/react";
import {InputHTMLAttributes} from "react";

export type InputProps = {
	labelValue?: string;
	labelStyle?: string | "";
	containerStyle?: string;
	className?: string;
	width?: string;
	size?: "sm" | "md" | "lg";
	color?: string | undefined;
	autoFocus?: boolean;
	loading?: boolean;
	inputPadding?: string;
} & Omit<InputHTMLAttributes<HTMLInputElement>, "size">;

const Input = ({
								 labelValue,
								 labelStyle,
								 containerStyle,
								 className,
								 width,
								 onKeyDown,
								 autoFocus,
								 loading,
								 inputPadding,
								 ...otherProps
							 }: InputProps) => {
	const inputStyle = inputPadding ? {padding: inputPadding} : {};

	return (
		<div className={`flex justify-between items-center ${containerStyle}`}>
			<div className="flex flex-col w-full">
				{labelValue && <label className={`mb-1 ${labelStyle}`}>{labelValue}</label>}
				<ChakraInput
					autoComplete="off"
					autoFocus={autoFocus || false}
					disabled={loading}
					className={className + " rounded-xl"}
					style={inputStyle}
					onKeyDown={onKeyDown}
					{...otherProps}
				/>
			</div>
		</div>
	);
};

export default Input;
