import {ReactComponent as BookIcon} from 'assets/icons/book.svg';
import {ReactComponent as TranslateIcon} from 'assets/icons/translate.svg';
import {ReactComponent as ExampleIcon} from "assets/icons/example.svg";
import {ReactComponent as MonitorIcon} from 'assets/icons/monitor.svg';
import {useTranslation} from "react-i18next";
import {Dispatch, SetStateAction} from "react";

const HeaderWordDetails = ({selectedItem, setSelectedItem}: {
	selectedItem: string,
	setSelectedItem: Dispatch<SetStateAction<string>>
}) => {
	const {t} = useTranslation();

	const HeaderWordDetailsObject = {
		Dictionary: t("Dictionary"),
		Conjugation: t("ConjugationText"),
		Examples: t("Examples"),
		Thesaurus: t("Thesaurus")
	}

	const HeaderWordDetailsIcon = [
		<BookIcon fontSize="small" key="icri" height={24} className="sub-header"/>,
		<TranslateIcon fontSize="small" key="vus" height={24} className="sub-header"/>,
		<ExampleIcon fontSize="small" key="asi" height={24} className="sub-header"/>,
		<MonitorIcon fontSize="small" key="dvr" height={24} className="sub-header"/>
	];

	const handleClick = (item: string) => {
		setSelectedItem(item);
	};

	return (
		<div className="inline-block  w-full header-sub   sm:w-full  xs:max-w-xs text-sm">
			<ul className="flex ">
				{Object.entries(HeaderWordDetailsObject).map(([key, value], index) => {
					return (
						<div key={key}>
							<button key={key}
											className={`cursor-pointer header-sub-title text-color-text flex items-center hover:bg-gray-50 ${selectedItem === key && 'header-sub-title-active'}`}
											onClick={() => handleClick(key)}>
								<span className="">{HeaderWordDetailsIcon[index] || ''}</span>

								<p className="ml-2 text-sm font-semibold lg:text-[15px]">{value}</p>
							</button>
						</div>
					);
				})}
			</ul>
		</div>
	);
};

export default HeaderWordDetails;
