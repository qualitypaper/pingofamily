import {ReactElement} from "react";
import {ReactComponent as GermanFlag} from "../assets/flags/germany.svg";
import {ReactComponent as SpainFlag} from "../assets/flags/spain.svg";
import {ReactComponent as UKFlag} from "../assets/flags/united_kingdom.svg";
import {ReactComponent as RomanianFlag} from "../assets/flags/romania.svg";
import {ReactComponent as PlusIcon} from "../assets/icons/plus.svg";
import {Language} from "../store/language/languageTypes";

export function pickIcon(
	language: Language | null,
	learning: boolean = false,
	classname?: string,
): ReactElement {
	if (!language) return <></>;

	const className = classname ?? (learning ? " w-14 h-10" : " w-7 h-[22px]");

	switch (language) {
		case "GERMAN":
			return <GermanFlag className={className}/>;
		case "SPANISH":
			return <SpainFlag className={className}/>;
		case "ENGLISH":
			return <UKFlag className={className}/>;
		// case 'RUSSIAN':
		//     return <RussianFlag className={className}/>
		case "ROMANIAN":
			return <RomanianFlag className={className}/>;
		default:
			return <PlusIcon className={className}/>;
	}
}
