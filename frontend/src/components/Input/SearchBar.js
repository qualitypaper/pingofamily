import {useTranslation} from "react-i18next";

function SearchBar({searchText, styleClass, placeholderText, setSearchText}) {
	const updateSearchInput = (value) => {
		setSearchText(value)
	}
	const {t} = useTranslation();

	return (
		<div className={"inline-block " + styleClass}>
			<div className="input-group  relative flex flex-wrap items-stretch w-full ">
				<input type="search" value={searchText} placeholder={placeholderText || t("Search")}
							 onChange={(e) => updateSearchInput(e.target.value)}
							 className="input input-sm input-bordered  w-full max-w-xs"/>
			</div>
		</div>
	)
}

export default SearchBar