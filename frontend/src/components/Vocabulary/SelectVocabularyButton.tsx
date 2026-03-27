import {
	removeVocabulary,
	setCurrentlySelectedVocabulary,
	setCurrentWords,
	setSuggestedVocabularyGroups,
} from "../../store/vocabulary/vocabularySlice";
import { useDispatch, useSelector } from "react-redux";
import { selectCurrentVocabularies } from "../../store/vocabulary/vocabularySelector";
import React, { ReactNode, useEffect, useState } from "react";
import { deleteVocabulary, updateLastPickedVocabulary, } from "../../utils/vocabularyUtils";
import { pickIcon } from "../../utils/iconUtils";
import { ReactComponent as PlusIcon } from "assets/icons/plus.svg";
import VocabularyListing from "./VocabularyListing";
import CreateVocabularyPopup from "../../features/vocabulary/Popups/CreateVocabularyPopup";
import i18n from "../../i18nf/i18n";
import { useNavigate } from "react-router-dom";
import { VocabularyType } from "../../store/vocabulary/vocabularyTypes";

export type SelectVocabularyButtonProps = {
	selected?: VocabularyType;
};

const SelectVocabularyButton = ({ selected }: SelectVocabularyButtonProps) => {
	const vocabularies = useSelector(selectCurrentVocabularies);
	const navigate = useNavigate();
	const dispatch = useDispatch();
	const [open, setOpen] = useState(false);
	const dropdownRef = React.useRef(null);
	const selectedStyles = selected ? "font-bold" : "";
	const [isFormVisible, setIsFormVisible] = useState<boolean>(false);

	const hideForm = () => {
		setIsFormVisible(false);
		setOpen(false);
	};

	useEffect(() => {
		function handleClickOutside(event: MouseEvent) {
			// @ts-ignore
			if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
				setOpen(false);
			}
		}

		document.addEventListener("mousedown", handleClickOutside);

		return () => {
			document.removeEventListener("mousedown", handleClickOutside);
		};
	}, []);

	const constructIcon = (learningIcon: ReactNode, nativeIcon: ReactNode) => {
		return (
			<div className="relative w-14 h-10">
				<div className="absolute inset-0">{learningIcon}</div>
				<div className="absolute bottom-0 right-0">{nativeIcon}</div>
			</div>
		);
	};

	const clickHandler = async (newSelected: VocabularyType) => {
		if (newSelected.id === selected?.id) return;
		dispatch(setSuggestedVocabularyGroups([]));
		dispatch(setCurrentlySelectedVocabulary(null));
		dispatch(setCurrentlySelectedVocabulary(newSelected));
		await updateLastPickedVocabulary(newSelected.id);
		setTimeout(() => {
			navigate(`/${i18n.language}/vocabularies`);
		}, 50)
	};

	const toggleSelectVocabulary = () => {
		setOpen(!open);
	};

	const deleteHandler = async (vocabulary: VocabularyType) => {
		const length = vocabularies.length;
		await deleteVocabulary(vocabulary.id);
		dispatch(removeVocabulary(vocabulary.id));
		if (length === 1) {
			dispatch(setCurrentlySelectedVocabulary(null));
			dispatch(setCurrentWords([]));
			return;
		}
		dispatch(setCurrentlySelectedVocabulary(vocabularies[0]));
	};

	return (
		<div className="relative" ref={dropdownRef}>
			<button
				tabIndex={0}
				onClick={toggleSelectVocabulary}
				className={`flex items-center w-16 ${selected && "border-0"}`}
			>
				{selected && vocabularies.length !== 0 ? (
					<span className="p-0 m-0">
						{constructIcon(
							pickIcon(selected.learningLanguage, true),
							pickIcon(selected.nativeLanguage, false)
						)}
					</span>
				) : (
					<PlusIcon className="text-gray-800 w-5 h-5" />
				)}
			</button>

			<div
				className={`z-50 flex right-0 absolute bg-white border border-slate-300 transition duration-300
                    font-bold rounded flex-col sm:flex-row overflow-auto max-h-80 max-w-80
                    ${open ? "opacity-100" : "opacity-0"}`}
			>
				{open &&
					vocabularies?.map((d: any, _: any) => {
						const latest = d.id === selected?.id;
						const learningIcon = pickIcon(d.learningLanguage, true);
						const nativeLanguage = pickIcon(d.nativeLanguage, false);

						return (
							<VocabularyListing
								icon={constructIcon(learningIcon, nativeLanguage)}
								selected={latest}
								key={d.id}
								vocabulary={d}
								setOpen={setOpen}
								setIsFormVisible={setIsFormVisible}
								clickHandler={clickHandler}
								isAddButton={d.numberOfWords === -1}
								setSelectedVocabulary={() => clickHandler(d)}
								deleteHandler={deleteHandler}
								selectedStyles={selectedStyles}
							/>
						);
					})}
			</div>
			{isFormVisible && <CreateVocabularyPopup onClose={hideForm} />}
		</div>
	);
}
	;

export default SelectVocabularyButton;
