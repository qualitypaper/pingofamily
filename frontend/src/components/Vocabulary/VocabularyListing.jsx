import {useTranslation} from "react-i18next";
import {useDispatch} from "react-redux";
import i18n from "../../i18nf/i18n";
import {showNotification} from "../../store/headerSlice";
import AddVocabularyButton from "./AddVocabularyButton";
import StandardVocabularyListing from "./StandardVocabularyListing";

function VocabularyListing({
														 vocabulary,
														 selected,
														 setSelectedVocabulary,
														 setOpen,
														 icon,
														 clickHandler,
														 isAddButton,
														 deleteHandler,
														 selectedStyles,
														 setIsFormVisible,
													 }) {
	const {t} = useTranslation();
	const dispatch = useDispatch();

	async function onClick() {
		if (vocabulary.loading) {
			dispatch(
				showNotification({
					message: t("VocabularyIsBeingProcessed"),
					status: "info",
				}),
			);
			return;
		}
		await clickHandler(vocabulary);
	}

	return (
		<>
			{isAddButton ? (
				<AddVocabularyButton
					setOpen={setOpen}
					setIsFormVisible={setIsFormVisible}
				/>
			) : (
				<StandardVocabularyListing
					selected={selected}
					setOpen={setOpen}
					vocabulary={vocabulary}
					clickHandler={onClick}
					icon={icon}
					selectedStyles={selectedStyles}
					setSelectedVocabulary={setSelectedVocabulary}
					toUrl={`/${i18n.language}/vocabularies/${vocabulary.id}`}
					deleteHandler={() => deleteHandler(vocabulary)}
				/>
			)}
		</>
	);
}

export default VocabularyListing;
