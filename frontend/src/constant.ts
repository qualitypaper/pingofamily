import { Language } from "store/language/languageTypes";

export const WEBSITE_NAME = "Pingo Family";

export const BUCKET = "--bucket-name--";
export const SOUND_TRAINING = "--training-completion-sound-url--";

export const BASE_URL: string = process.env.REACT_APP_BASE_URL ?? "";
export const SOCKET_URL: string = process.env.REACT_APP_SOCKET_URL ?? "";

export const ISO_2_LANGUAGES = ["en", "de", "es", "ro"];
export const LANGUAGES: Language[] = [
  "ENGLISH",
  "GERMAN",
  "SPANISH",
  "ROMANIAN",
];

export const POSSIBLE_TRANSLATIONS_WORD_LIMIT = 7;
export const STATISTICS_PERIOD = 7; // in days
