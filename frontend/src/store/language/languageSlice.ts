import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import {RootState} from "../../app/store";
import {Language} from "./languageTypes";

interface LanguageState {
	selectedLearningLanguage: Language;
	selectedNativeLanguage: Language;
}

const initialState: LanguageState = {
	selectedLearningLanguage: "ENGLISH",
	selectedNativeLanguage: "GERMAN",
};

const languageSlice = createSlice({
	name: "language",
	initialState,
	reducers: {
		setSelectedLearningLanguage: (state, action: PayloadAction<string>) => {
			state.selectedLearningLanguage = action.payload as Language;
		},
		setSelectedNativeLanguage: (state, action: PayloadAction<string>) => {
			state.selectedNativeLanguage = action.payload as Language;
		},
	},
});

export const {setSelectedLearningLanguage, setSelectedNativeLanguage} =
	languageSlice.actions;

export const selectSelectedLearningLanguage = (state: RootState) =>
	// @ts-ignore
	state.language.selectedLearningLanguage;

export const selectSelectedNativeLanguage = (state: RootState) =>
	// @ts-ignore
	state.language.selectedNativeLanguage;

export const languageReducer = languageSlice.reducer;
