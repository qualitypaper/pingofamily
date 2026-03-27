import { store } from "app/store";
import { AuthenticationResponse, setUser, UserDetailsResponse } from "store/user/userSlice";
import { API } from "../app/init";
import { check400 } from "./globalUtils";

export const authenticate = async (loginObj: { email: string; password: string }) => {
  debugger
  return await API.post(`/auth/authenticate`, loginObj, {
    headers: {
      "Content-Type": "application/json",
      Authorization: "",
    },
    signal: AbortSignal.timeout(5000),
  });
};

export const register = async (registerObject: {
  fullName: string;
  email: string;
  password: string;
}): Promise<AuthenticationResponse | undefined> => {
  try {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 5000);

    const response = await API.post(`/auth/register`, registerObject, {
      headers: {
        "Content-Type": "application/json",
        Accept: "application/json",
        Authorization: "",
      },
      signal: controller.signal,
    });

    clearTimeout(timeoutId);

    if (check400(response.status) || Math.floor(response.status / 100) === 5) {
      throw new Error("Registration failed: " + response.status);
    }

    return response.data;
  } catch (error) {
    console.error(error);
    return;
  }
};

export function extractImageFile(url: string): string {
  if (!url) return "";

  return url?.split("/").pop() || "";
}

export async function getUserDetails(): Promise<UserDetailsResponse | undefined> {
  const response = await API.get("/user/get-user-details");
  if (!response || !response?.data) {
    return Promise.reject();
  }
  const { streak, userDetails, settings } = response.data;

  store.dispatch(setUser({ userStreak: streak, userDetails, userSettings: settings }));
  return Promise.resolve(userDetails);
}
