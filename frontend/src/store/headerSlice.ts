import {createSlice} from '@reduxjs/toolkit'

export type Notification = "info" | "success" | "error";

export type HeaderState = {
	notificationMessage: string;
	notificationStatus: Notification;
	lastNotificationTime: number;
}

const INITIAL_STATE: HeaderState = {
	notificationMessage: "",
	notificationStatus: "info",
	lastNotificationTime: Date.now(),
}

export const headerSlice = createSlice({
	name: 'header',
	initialState: INITIAL_STATE,
	reducers: {
		removeNotificationMessage: (state) => {
			state.notificationMessage = ""
		},
		showNotification: (state, action) => {
			if (!action.payload.message || action.payload.message === "")
				return;

			const currentTime = Date.now();
			if (state.lastNotificationTime && currentTime - state.lastNotificationTime < 2000) return;

			state.notificationMessage = action.payload.message
			state.notificationStatus = action.payload.status
			state.lastNotificationTime = currentTime;
		},
	}
})

export const {removeNotificationMessage, showNotification} = headerSlice.actions

export const headerReducer = headerSlice.reducer