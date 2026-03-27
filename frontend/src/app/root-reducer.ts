import {combineReducers} from "@reduxjs/toolkit";
import {userReducer} from "../store/user/userSlice";
import {vocabularyReducer} from "../store/vocabulary/vocabularySlice";
import {headerReducer} from "../store/headerSlice";
import {modalReducer} from "../store/modalSlice";
import {trainingReducer} from "../store/training/trainingSlice";
import {languageReducer} from "../store/language/languageSlice";


export const appReducer = combineReducers({
	user: userReducer,
	header: headerReducer,
	vocabulary: vocabularyReducer,
	modal: modalReducer,
	language: languageReducer,
	training: trainingReducer,
})

export const rootReducer: typeof appReducer = (state, action) => {
	if (action.type === 'user/logout') {
		state = undefined;
	}

	return appReducer(state, action);
}
