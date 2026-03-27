type Callback = (...args: any[]) => any;
const subscribers = new Set<Callback>();

export function getAuthToken() {
  return localStorage.getItem("authToken");
}

// Notify all subscribers when authToken changes
export function setAuthToken(token: string) {
  insertIntoLocalStorage("authToken", token);
}

export function subscribeAuthToken(callback: Callback) {
  subscribers.add(callback);
  return () => subscribers.delete(callback); // Unsubscribe function
}

function notifySubscribers() {
  const newToken = getAuthToken();
  if (newToken === null) return;

  subscribers.forEach((callback) => callback(newToken));
}

export type StorageEventType = "localStorageInserted" | "sessionStorageInserted";

export class StorageEvent extends Event {
  key: string;
  value: string;

  constructor(eventType: StorageEventType, key: string, value: string) {
    super(eventType);
    this.key = key;
    this.value = value;
  }
}

export const insertIntoLocalStorage = (key: string, value: string) => {
  const event = new StorageEvent("localStorageInserted", key, value);

  localStorage.setItem(key, value);
  document.dispatchEvent(event);
};

export const insertIntoSessionStorage = (key: string, value: string) => {
  const event = new StorageEvent("sessionStorageInserted", key, value);
  document.dispatchEvent(event);

  sessionStorage.setItem(key, value);
};

const storageInsertHandler = (e: StorageEvent) => {
  notifySubscribers();
  console.log(`Inserted in localstorage: ${e.key}:${e.value}`);
};

// @ts-ignore
document.addEventListener("localStorageInserted", storageInsertHandler);
// @ts-ignore
document.addEventListener("sessionStorageInserted", storageInsertHandler);
