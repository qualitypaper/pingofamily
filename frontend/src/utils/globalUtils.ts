import { v4 as uuid } from "uuid";
import { ISO_2_LANGUAGES } from "constant";
import { allSubRoutes } from "../routes/routes";
import { Language } from "../store/language/languageTypes";

export const check400 = (status: number) => {
  return Math.floor(status / 100) === 4;
};

export const getRandomUUID = (): string => {
  return uuid();
};

export const getBlockWidth = (word: string): string => {
  let width;
  if (word.length >= 7) width = word.length + 1;
  else width = word.length + 2;

  return width + "ch";
};

export const shuffleArray = (array: any[]) => {
  for (let i = 0; i < array.length; i++) {
    const j = Math.floor(Math.random() * (i + 1));
    [array[i], array[j]] = [array[j], array[i]];
  }
  return array;
};

export const isValidLang = (lang: string) => {
  if (lang.length !== 2) return false;

  return ISO_2_LANGUAGES.includes(lang.toLowerCase());
};

export const isSubPath = (lang: string) => {
  if (lang.length < 2) return false;

  if (lang.startsWith("/")) {
    lang = lang.substring(1);
  }
  if (lang.endsWith("/")) {
    lang = lang.substring(0, lang.length - 1);
  }

  return allSubRoutes.includes(lang);
};

export const getShortLanguageName = (full: string) => {
  switch (full.toLocaleLowerCase()) {
    case "english":
      return "en";
    case "spanish":
      return "es";
    case "german":
      return "de";
    default:
      return full;
  }
};

export const getFullLanguageNameForRequest = (toLangShortened: string): string => {
  switch (toLangShortened.toLocaleLowerCase()) {
    case "en":
      return "ENGLISH";
    case "es":
      return "SPANISH";
    case "de":
      return "GERMAN";
    default:
      return toLangShortened;
  }
};

export const extractDate = (utc: number) => {
  const date = new Date(utc).toISOString();
  return date.substring(0, date.indexOf("T"));
};

export const capitalize = (word: string, index: number = 0) => {
  return word.charAt(index).toUpperCase() + word.slice(index + 1);
};

const languageTranslations = new Map([
  [
    "GERMAN",
    new Map([
      ["ENGLISH", "englisch"],
      ["SPANISH", "spanisch"],
    ]),
  ],
  [
    "ENGLISH",
    new Map([
      ["GERMAN", "german"],
      ["SPANISH", "spanish"],
    ]),
  ],
  [
    "SPANISH",
    new Map([
      ["GERMAN", "alemán"],
      ["ENGLISH", "inglés"],
    ]),
  ],
]);

// toLang is a short form of the language
export const getLocalizedLanguageName = (lang: Language, toLangShortened: string) => {
  const toLang: string = getFullLanguageNameForRequest(toLangShortened);
  const translations = languageTranslations.get(toLang);

  return translations?.get(lang) ?? lang;
};

export const retry = (fn: () => Promise<any>, retriesLeft = 5, interval = 100): Promise<any> => {
  return new Promise((resolve, reject) => {
    fn()
      .then(resolve)
      .catch((error) => {
        if (retriesLeft === 1) {
          reject(error);
          return;
        }
        setTimeout(() => {
          retry(fn, retriesLeft - 1, interval)
            .then(resolve)
            .catch(reject);
        }, interval);
      });
  });
};

export function pair(arr1: any[], arr2: any[]): any[] {
  const res: any[] = [];
  let i;
  for (i = 0; i < Math.min(arr1.length, arr2.length); i++) {
    res.push(arr1[i]);
    res.push(arr2[i]);
  }

  if (i < arr1.length) {
    for (let j = i; j < arr1.length; j++) {
      res.push(arr1[j]);
    }
  }

  if (i < arr2.length) {
    for (let j = i; j < arr2.length; j++) {
      res.push(arr2[j]);
    }
  }

  return res;
}

export function isMobile(): boolean {
  return window.innerWidth < 768;
}