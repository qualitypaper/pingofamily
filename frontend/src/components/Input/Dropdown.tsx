import React, {ReactNode, useEffect, useRef, useState} from "react";
import {ReactComponent as ArrowDown} from "../../assets/icons/arrowdown.svg";

type Option = {
	label: string;
	value: string;
	flag?: ReactNode;
};

interface DropdownProps {
	options: Option[];
	label: string;
	onOptionSelect: (value: string) => void;
	defaultValue?: Option;
	selectedValue?: string;
	labelClassName?: string;
	containerClassName?: string;
	selectClassName?: string;
	optionClassName?: string;
	iconClassName?: string;
	isOpen?: boolean;
	selectedOptionClassName?: string;
	setIsOpen?: (isOpen: boolean) => void;
	selectRef?: React.MutableRefObject<HTMLDivElement | null>;
	displayLabel?: boolean;
}

const Dropdown: React.FC<DropdownProps> = ({
																						 options,
																						 label,
																						 onOptionSelect,
																						 defaultValue,
																						 selectedValue,
																						 labelClassName,
																						 containerClassName,
																						 selectClassName,
																						 selectedOptionClassName,
																						 optionClassName,
																						 iconClassName,
																						 isOpen: controlledIsOpen,
																						 setIsOpen: setControlledIsOpen,
																						 selectRef: controlledSelectRef,
																						 displayLabel = true,
																					 }) => {
	const internalSelectRef = useRef<HTMLDivElement>(null);
	const selectRef = controlledSelectRef || internalSelectRef;
	const [isOpen, setIsOpen] = useState(controlledIsOpen || false);
	const [selectedOption, setSelectedOption] = useState<string | null>(
		defaultValue?.value || null,
	);
	const [openUpwards, setOpenUpwards] = useState(false);

	const handleOptionClick = (value: string) => {
		setSelectedOption(value);
		if (setControlledIsOpen) {
			setControlledIsOpen(false);
		} else {
			setIsOpen(false);
		}
		onOptionSelect(value);
	};

	useEffect(() => {
		const handleClickOutside = (event: MouseEvent) => {
			if (
				selectRef.current &&
				!selectRef.current.contains(event.target as Node)
			) {
				if (setControlledIsOpen) {
					setControlledIsOpen(false);
				} else {
					setIsOpen(false);
				}
			}
		};
		document.addEventListener("mousedown", handleClickOutside);
		return () => {
			document.removeEventListener("mousedown", handleClickOutside);
		};
	}, [selectRef, setControlledIsOpen]);

	useEffect(() => {
		if (selectedValue !== undefined) {
			setSelectedOption(selectedValue);
		}
	}, [selectedValue]);

	useEffect(() => {
		if (controlledIsOpen !== undefined) {
			setIsOpen(controlledIsOpen);
		}
	}, [controlledIsOpen]);

	const handleDropdownClick = () => {
		const newIsOpen = !isOpen;
		if (setControlledIsOpen) {
			setControlledIsOpen(newIsOpen);
		} else {
			setIsOpen(newIsOpen);
		}
		if (selectRef.current) {
			const rect = selectRef.current.getBoundingClientRect();
			const spaceBelow = window.innerHeight - rect.bottom;
			const dropdownHeight = options.length * 40;
			setOpenUpwards(spaceBelow < dropdownHeight);
		}
	};

	const selectedOptionObject = options.find(
		(option) => option.value === selectedOption,
	);
	const selectedLabel = displayLabel ? selectedOptionObject?.label : "";
	const selectedFlag = selectedOptionObject?.flag;

	return (
		<div className={containerClassName}>
			<label className={labelClassName}>{label}</label>
			<div className="relative" ref={selectRef}>
				<div
					className={`${selectClassName} border border-gray-300 rounded-xl cursor-pointer flex items-center gap-2 justify-between`}
					onClick={handleDropdownClick}
				>
					<div className="flex items-center gap-2">
						{selectedFlag && <span>{selectedFlag}</span>}
						<span className={selectedOptionClassName}>{selectedLabel}</span>
					</div>
					<ArrowDown
						className={`transition-transform w-6 h-6 ${isOpen ? "transform rotate-180" : ""} ${iconClassName}`}
					/>
				</div>
				{isOpen && (
					<div
						className={`absolute ${openUpwards ? "bottom-full" : "top-full"} w-full rounded bg-white shadow-lg z-50`}
					>
						{options.map((option) => (
							<div
								key={option.value}
								className={`${optionClassName} px-3 py-1.5 cursor-pointer hover:bg-blue-100`}
								onClick={() => handleOptionClick(option.value)}
							>
								{option.flag && <span>{option.flag}</span>}
								{displayLabel && (
									<span className={selectedOptionClassName}>
                    {option.label}
                  </span>
								)}
							</div>
						))}
					</div>
				)}
			</div>
		</div>
	);
};

export default Dropdown;
