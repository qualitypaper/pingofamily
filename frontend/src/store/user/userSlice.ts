import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { RootState } from "app/store";
import { insertIntoSessionStorage, setAuthToken } from "../../app/localStorageService";
import { Language } from "../language/languageTypes";

export type AuthenticationResponse = {
  readonly tokens: TokenPair;
  readonly settings: UserSettings;
  readonly userDetails: UserDetails;
  readonly streak: UserStreak;
};

export type UserDetailsResponse = {
  readonly userSettings: UserSettings;
  readonly userDetails: UserDetails;
  readonly userStreak: UserStreak;
};

export type TrainingStatistics = {
  trainingId: number;
  date: number;
  accuracy: string;
  numberOfWords: number;
};

export type UserSettings = {
  interfaceLanguage: Language;
};

export type TokenPair = {
  readonly accessToken: string;
  readonly refreshToken: string;
};

export type UserDetails = {
  readonly email: string;
  readonly name: string;
  readonly roomCode: string;
  readonly profileImageUrl: string;
  readonly lastPickedVocabularyId: number;
};

export type UserStreak = {
  readonly currentStreak: number;
  readonly maxStreak: number;
};

export type UserState = {
  readonly userSettings: UserSettings;
  readonly userDetails: UserDetails;
  readonly tokenPair: TokenPair;
  readonly userStreak: UserStreak;
  readonly isAuthenticated: boolean;
};

export const createUserInitialState = (): UserState => ({
  userSettings: {
    interfaceLanguage: "ENGLISH",
  },
  tokenPair: {
    refreshToken: "",
    accessToken: "",
  },
  userDetails: {
    roomCode: "",
    lastPickedVocabularyId: 0,
    profileImageUrl: "",
    email: "",
    name: "",
  },
  userStreak: {
    currentStreak: 0,
    maxStreak: 0,
  },
  isAuthenticated: false,
})

export const USER_INITIAL_STATE: UserState = createUserInitialState();

export const checkAuthentication = createAsyncThunk(
  "user/checkAuthentication",
  async (_, thunkAPI) => {
    const state = thunkAPI.getState() as RootState;
    const tokenPair = state.user.tokenPair;
    const isValid = await isTokenValid(tokenPair);
    return isValid;
  },
);

export async function isTokenValid(tokenPair?: TokenPair): Promise<boolean> {
  if (!tokenPair) return Promise.resolve(false);

  const key: string = tokenPair.accessToken + tokenPair.refreshToken;
  const stored = sessionStorage.getItem(key);
  if (stored) {
    return Promise.resolve(JSON.parse(stored ?? '{"isValid": false}').isValid as boolean);
  }
  const isValid: boolean = !!tokenPair?.accessToken && !!tokenPair?.refreshToken;

  insertIntoSessionStorage(key, JSON.stringify({ isValid }));
  return Promise.resolve(isValid);
}

export const userSlice = createSlice({
  name: "user",
  initialState: USER_INITIAL_STATE,
  extraReducers: (builder) => {
    builder.addCase(checkAuthentication.fulfilled, (state, action) => {
      state.isAuthenticated = action.payload;
    });
  },
  reducers: {
    setAccessToken(state, action) {
      setAuthToken(action.payload);
      state.tokenPair.accessToken = action.payload;
    },
    setTokenPair(state, action) {
      setAuthToken(action.payload.accessToken);
      state.tokenPair = action.payload;
    },
    logOut() {
      localStorage.removeItem("authToken");
      return createUserInitialState();
    },
    setUser(state, action) {
      const { userDetails, tokenPair, userStreak, userSettings } = action.payload;
      if (userDetails) {
        state.userDetails = userDetails;
      }
      if (tokenPair) {
        state.tokenPair = tokenPair;
      }
      if (userStreak) {
        state.userStreak = userStreak;
      }
      if (userSettings) {
        state.userSettings = userSettings;
      }
    },
    setUserDetails(state, action) {
      state.userDetails = action.payload;
    },
    setUserStreak(state, action) {
      state.userStreak = action.payload;
    },
    setName(state, action) {
      state.userDetails.name = action.payload;
    },
  },
});

export const userReducer = userSlice.reducer;
export const {
  setAccessToken,
  setTokenPair,
  setUser,
  logOut,
  setUserDetails,
  setUserStreak,
  setName,
} = userSlice.actions;
