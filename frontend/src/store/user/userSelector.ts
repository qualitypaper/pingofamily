import { createSelector } from "reselect";
import { RootState } from "../../app/store";

const selectUserReducer = (state: RootState) => state.user;

export const selectTokenPair = (state: RootState) => selectUserReducer(state).tokenPair;

export const selectIsAuthenticated = createSelector(
  [selectUserReducer],
  (state) => state.isAuthenticated,
);

export const selectAccessToken = (state: RootState) => selectUserReducer(state).tokenPair.accessToken ?? "";

export const selectRoomCode = createSelector(
  [selectUserReducer],
  (state) => state.userDetails.roomCode,
);

export const selectUserStreak = createSelector([selectUserReducer], (state) => state.userStreak);

export const selectLastPickedVocabularyId = createSelector(
  [selectUserReducer],
  (state) => state.userDetails.lastPickedVocabularyId,
);

export const selectUserDetails = createSelector([selectUserReducer], (state) => state.userDetails);
