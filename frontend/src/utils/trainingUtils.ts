import { Dispatch, RefObject, SetStateAction } from "react";
import { API } from "../app/init";
import { WordIndex } from "../components/Training/trainingTypes/standard/PhraseConstruction";
import { TrainingExampleWithIndex } from "../store/training/trainingTypes";

const spanishArticles = ["el", "la", "los", "las", "un", "una", "unos", "unas", "el/la", "la/los"];
const germanArticles = [
  "der",
  "die",
  "das",
  "des",
  "den",
  "dem",
  "ein",
  "eine",
  "einen",
  "einem",
  "einer",
  "zu",
];
const englishArticles = ["a", "an", "the", "to"];

export const articles = new Map([
  ["GERMAN", germanArticles],
  ["SPANISH", spanishArticles],
  ["ENGLISH", englishArticles],
]);

const audios: Map<string, any> = new Map();

export type Ref<T> = RefObject<T>;

export type HtmlInputElement = HTMLInputElement | null;
export type HtmlImageElement = HTMLImageElement | null;

export const completeInputTrainingRef = (ref: Ref<HtmlInputElement | HTMLTextAreaElement>) => {
  if (ref.current) {
    ref.current.style.color = "black";
    ref.current.style.border = "";
    ref.current.disabled = true;
  }
};

export const getHintWord = (text: string, lang: string): string => {
  const { word, article } = removeArticles(text, lang);

  return (article ? article + " " : "") + word.slice(0, 2);
};

export const addToCurrentCursorPosition = (
  input: string,
  setInput: (v: string) => void,
  textToAdd: string,
  inputRef?: Ref<HtmlInputElement | HTMLTextAreaElement>,
  cursorPosition?: number,
): void => {
  const cursor = cursorPosition ?? inputRef?.current?.selectionStart ?? -1;
  if (cursor === -1) {
    setInput(input + textToAdd);
  } else {
    setInput(input.slice(0, cursor) + textToAdd + input.slice(cursor));
  }

  inputRef?.current && inputRef.current.focus();
  inputRef?.current &&
    inputRef.current.setSelectionRange(cursor + textToAdd.length, cursor + textToAdd.length);
};

export const removeArticles = (text: string, lang: string): { article: string; word: string } => {
  let article = "";
  let arr: string[];

  if (lang === "all") {
    arr = Array.from(articles.values()).flat();
  } else {
    arr = articles.has(lang) ? (articles.get(lang) ?? []) : [];
  }

  for (let value of arr) {
    for (let e of text.split(" ")) {
      if (value.includes(e.trim())) {
        article += e.trim();
        text = text.replace(e.trim(), "");
      }
    }
  }

  return { article, word: text.trim() };
};

export const isCorrect = (input: string, word: string, lang: string): boolean => {
  const { word: wordWithoutArticle } = removeArticles(word, lang);
  const { word: inputWithoutArticle } = removeArticles(input, lang);

  return (
    removeSpecialCharacters(inputWithoutArticle.toLowerCase()).trim() ===
    removeSpecialCharacters(wordWithoutArticle.toLowerCase()).trim()
  );
};

export const hintTriggerEvent = (
  setInput: Dispatch<SetStateAction<string>>,
  word: string,
  inputRef?: Ref<HtmlInputElement>,
  imageRef?: Ref<HtmlImageElement>,
) => {
  const hintWord = getHintWord(word, "all");
  setInput(hintWord);
  imageRef && revealImage(imageRef);
  inputRef?.current && inputRef.current.focus();
  inputRef?.current && inputRef.current.setSelectionRange(hintWord.length, hintWord.length);
};

export const revealAnswer = (
  setInput: Dispatch<SetStateAction<string>>,
  word: string,
  imageRef?: Ref<HtmlImageElement>,
): void => {
  setInput(word);
  imageRef && revealImage(imageRef);
};

export const revealImage = (imageRef: Ref<HtmlImageElement>) => {
  if (imageRef?.current) {
    imageRef.current.style.visibility = "visible";
  }
};

export const sendCompleteMessage = async (body: {}) => {
  return await API.post("/learning/complete", body);
};

export const removeSpecialCharacters = (text: string) => {
  // eslint-disable-next-line no-control-regex
  let regex = /[^\p{L}\p{N}\s#]/gu;

  return text.replace(regex, "");
};

export const insert = (array: any, object: WordIndex) => {
  const temp = [...array];
  temp[object.index] = object;
  // return [...array.slice(0, object.index), object, ...array.slice(object.index)]
  return temp;
};

export const constructMap = (object: {}): Map<string, string[]> => {
  if (!object) return new Map<string, string[]>();

  const map = new Map<string, string[]>();

  Object.entries(object).forEach(([key, value]) => {
    if (Array.isArray(value)) {
      map.set(key, value as string[]);
    } else {
      map.set(key, []);
    }
  });

  return map;
};
export const playSound = async (
  source: string | undefined,
  resolve: VoidFunction = () => { },
  reject: (reason?: any) => void = () => { },
) => {
  if (!source) return;
  else if (audios.has(source)) {
    audios.get(source).stop()
    audios.delete(source);
  }

  const audio = new Audio(source);
  
  audio.onended = () => {
    audios.delete(source);
    resolve();
  };
  audio.onerror = (e) => reject(e);
  audio.onload = () => {
    audio.currentTime = 0;
  };
  const promise = audio.play();
  
  audios.set(source, { play: promise, stop: () => audio.pause() });

  await promise;
};



export const fetchTraining = async (training: {
  vocabularyGroupId: number;
}) => {
  return await API.post(`/learning/generate`, training);
};

export function groupBy<K, V>(list: Array<V>, keyGetter: (input: V) => K): Map<K, Array<V>> {
  const map = new Map();

  list.forEach((item) => {
    const key = keyGetter(item);
    const collection = map.get(key);
    if (!collection) {
      map.set(key, [item]);
    } else {
      collection.push(item);
    }
  });

  return map;
}

export const completeTraining = async (
  finalTrainingSequence: TrainingExampleWithIndex[],
  learningSessionId: number,
) => {
  const mistakes: {
    trainingId: number;
    mistakes: CompleteTrainingSecondLevelResponse[];
  }[] = [];

  const groupedByTrainingId = groupBy(finalTrainingSequence, (training) => training.trainingId);

  for (let [key, value] of groupedByTrainingId) {
    const temp: CompleteTrainingSecondLevelResponse[] = [];

    const groupedByTrainingExampleId = groupBy(value, (e) => e.trainingExample.id);

    for (let [trainingExampleId, answers] of groupedByTrainingExampleId.entries()) {
      temp.push({
        trainingExampleId,
        answers: answers.map((e) => ({
          hint: e.hint,
          skipped: e.skipped,
          timestamp: e.timestamp,
        })),
      });
    }

    mistakes.push({
      trainingId: key,
      mistakes: [...temp],
    });
  }

  const json = {
    learningSessionId,
    results: [...mistakes],
  };

  return await sendCompleteMessage(json);
};

export type CompleteTrainingSecondLevelResponse = {
  trainingExampleId: number;
  answers: TrainingExampleAnswer[];
};

export type TrainingExampleAnswer = {
  hint: boolean;
  skipped: boolean;
  timestamp: Date;
};

export const compareCollapsed = (first: string, second: string) => {
  return (
    removeSpecialCharacters(first.toLowerCase()).replaceAll(" ", "") ===
    removeSpecialCharacters(second.toLowerCase()).replaceAll(" ", "")
  );
};
