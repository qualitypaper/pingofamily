import { FC, memo, useCallback, useEffect, useState } from "react";
import { useDispatch } from "react-redux";
import { SOUND_TRAINING } from "constant";
import {
	addMistakeToTrainingSequence,
	incrementFinalSequenceIndex,
	incrementHintsNumber,
	incrementMistakeNumber,
} from "../../../../store/training/trainingSlice";
import { shuffleArray } from "../../../../utils/globalUtils";
import { constructMap, insert, playSound, removeSpecialCharacters, } from "../../../../utils/trainingUtils";
import { constructWordsTranslationSentence, TrainingTypesProps, } from "../../Training";
import DraggableTextBlock from "./phraseConstructionComponents/DraggableTextBlock";
import Dropzone from "./phraseConstructionComponents/Dropzone";
import TrainingBlock from "../TrainingBlock";
import TrainingImage from "../TrainingImage";

export type WordIndex = {
	word: string;
	index: number;
	width: string;
	fixed: boolean;
};

const PhraseConstruction: FC<TrainingTypesProps> = memo(({ elem, targetWord }: TrainingTypesProps) => {
	const dispatch = useDispatch();
	const { imageUrl } = targetWord.wordTranslation.wordFrom;
	const [initialPhraseWords, setInitialPhraseWords] = useState<WordIndex[]>([]);
	const [initiallyShuffledPhraseWords, setInitiallyShuffledPhraseWords] = useState<WordIndex[]>([]);
	const [phraseWords, setPhraseWords] = useState<WordIndex[]>(initiallyShuffledPhraseWords);
	const [droppedWords, setDroppedWords] = useState<WordIndex[]>([]);
	const [isMobile, setIsMobile] = useState<boolean>(window.innerWidth <= 768);
	const [correct, setCorrect] = useState<boolean>(false);
	const [translationSentence, setTranslationSentence] = useState<any[]>([]);
	const [skipped, setSkipped] = useState<boolean>(false);

	useEffect(() => {
		const handleWindowSizeChange = () => {
			setIsMobile(window.innerWidth <= 768 && initialPhraseWords.length < 5);
		};
		window.addEventListener("resize", handleWindowSizeChange);
		return () => {
			window.removeEventListener("resize", handleWindowSizeChange);
		};
	}, [initialPhraseWords.length]);

	useEffect(() => {
		const wordsTranslationMapFirst = constructWordsTranslationSentence(
			elem.trainingExample.sentenceTranslation,
			constructMap(elem.trainingExample.wordsTranslation)
		);

		setTranslationSentence(wordsTranslationMapFirst ?? []);
	}, [
		elem.trainingExample.sentenceTranslation,
		elem.trainingExample.wordsTranslation,
		targetWord.wordTranslation,
	]);

	const handleCompletion = useCallback(
		async (hintOrSkipped: boolean) => {
			setCorrect(true);
			await playSound(elem.trainingExample.soundUrl, () => {
				if (!hintOrSkipped) {
					dispatch(incrementFinalSequenceIndex());
					setCorrect(false);
					setSkipped(false);
					setPhraseWords([]);
					setDroppedWords([]);
					setInitialPhraseWords([]);
					setInitiallyShuffledPhraseWords([]);
				}
			});
		},
		[dispatch, elem.trainingExample.soundUrl]
	);

	const handleTextBlockClick = useCallback((object: WordIndex[], hintOrSkipped: boolean = false) => {
		const indexes: number[] = [];
		const newDroppedWords = hintOrSkipped ? [] : [...droppedWords];
		object.forEach((e) => {
			indexes.push(e.index);
			newDroppedWords.push(e);
		});

		setDroppedWords(newDroppedWords);
		setPhraseWords((prevTexts) =>
			prevTexts.map((t) =>
				!indexes.includes(t.index)
					? { ...t }
					: {
						index: t.index,
						word: "emptyyy",
						width: t.width,
						fixed: hintOrSkipped,
					}
			)
		);

		if (
			initialPhraseWords.length !== 0 &&
			newDroppedWords.length === initialPhraseWords.length
		) {
			for (let i = 0; i < newDroppedWords.length; i++) {
				if (initialPhraseWords[i].word !== newDroppedWords[i].word) break;
				if (i === newDroppedWords.length - 1) {
					playSound(SOUND_TRAINING);
					handleCompletion(hintOrSkipped);
				}
			}
		}
	},
		[droppedWords, handleCompletion, initialPhraseWords]
	);

	const resetWordBlocks = useCallback(() => {
		setPhraseWords(
			phraseWords.map((wordIndex) => {
				const temp = { ...wordIndex };
				temp.word = "";
				return temp;
			})
		);
	}, [phraseWords]);

	const showCorrectAnswer = useCallback(() => {
		setSkipped(true);
		setDroppedWords([]);
		resetWordBlocks();
		handleTextBlockClick(initialPhraseWords, true);
		dispatch(addMistakeToTrainingSequence(elem));
		dispatch(
			incrementMistakeNumber({
				index: elem.index,
				trainingType: elem.trainingExample.trainingType,
			})
		);
	}, [
		dispatch,
		elem,
		handleTextBlockClick,
		initialPhraseWords,
		resetWordBlocks,
	]);

	const handleContinueClick = useCallback(() => {
		if (correct) {
			setDroppedWords([]);
			setPhraseWords(shuffleArray(initiallyShuffledPhraseWords));
			// setInitialPhraseWords([]);
			setCorrect(false);
			setSkipped(false);
			dispatch(incrementFinalSequenceIndex());
		} else {
			showCorrectAnswer();
		}
	}, [correct, dispatch, initiallyShuffledPhraseWords, showCorrectAnswer]);

	const findWords = (toFind: string[], array: WordIndex[]): WordIndex[] => {
		const result: WordIndex[] = [];

		for (const element of array) {
			if (toFind.includes(element.word)) {
				result.push(element);
			}
		}

		return result;
	};

	const hintClickTrigger = () => {
		dispatch(
			incrementHintsNumber({
				index: elem.index,
				trainingType: elem.trainingExample.trainingType,
			})
		);
		const newDroppedWords = findWords(
			initialPhraseWords.slice(0, 2).map((e) => e.word),
			initiallyShuffledPhraseWords
		);

		setPhraseWords(initiallyShuffledPhraseWords);
		setDroppedWords([]);

		handleTextBlockClick(newDroppedWords, true);
	};

	useEffect(() => {
		function processWords(words: string[], toArray: WordIndex[]) {
			let index = 0;
			for (const element of words) {
				if (element === "" || element === " ") {
					continue;
				}
				const word = element.replace(/\\+u([0-9a-fA-F]{4})/g, (a, b) =>
					String.fromCharCode(parseInt(b, 16))
				);

				const data: WordIndex = {
					word: removeSpecialCharacters(word),
					index: index,
					width: "",
					fixed: false,
				};

				toArray.push(data);
				index += 1;
			}
		}

		const temp: WordIndex[] = [];
		const shuffledTemp: WordIndex[] = [];
		const splitArray = elem.trainingExample.sentence
			.replaceAll("\n", "")
			.split(" ");
		const shuffledArray = shuffleArray(splitArray.slice(0));

		processWords(splitArray, temp);
		processWords(shuffledArray, shuffledTemp);

		setPhraseWords(shuffledTemp);
		setInitialPhraseWords(temp);
		setInitiallyShuffledPhraseWords(shuffledTemp);
		setDroppedWords([]);
	}, [elem.trainingExample.sentence]);

	const handleTextBlockRemoveClick = (object: WordIndex) => {
		setDroppedWords((prevTexts) =>
			prevTexts.filter((t) => t.index !== object.index)
		);
		setPhraseWords(insert(phraseWords, object));
	};

	const updatePhraseWordsWidth = (object: WordIndex, width: string) => {
		const temp = [...phraseWords].map((t) =>
			t?.index === object?.index ? { ...t, width } : t
		);

		setPhraseWords(temp);
		setInitiallyShuffledPhraseWords(temp);
	};

	return (
		<div className="w-full h-full overflow-x-hidden flex justify-center">
			<TrainingBlock
				isSkipped={skipped}
				isCorrect={correct}
				showHint={hintClickTrigger}
				handleSkipClick={handleContinueClick}
				addChar={() => {
				}}
			>
				<div className="flex flex-col items-center">
					<div className="flex flex-col  justify-center w-full gap-8 items-center">
						<div
							className={`mb-3 text-center flex-col gap-4 justify-center items-center flex flex-wrap font-semibold text-xl md:text-2xl`}
						>
							<div className="flex flex-col gap-4 items-center justify-center flex-wrap">
								<TrainingImage src={imageUrl} />
								<p
									className="border-b-4  bg-slate-50 border-sky-200 p-2 text-white  text-center rounded-lg flex-wrap  ">
									{translationSentence}
								</p>
							</div>
						</div>
					</div>

					<Dropzone
						isMobile={isMobile}
						droppedTexts={droppedWords}
						initialPhraseWordsLength={initialPhraseWords.length || 0}
						onTextBlockClick={handleTextBlockRemoveClick}
						isCorrect={correct}
					/>

					<div
						style={{ pointerEvents: correct ? "none" : "auto" }}
						className="mt-10 flex flex-wrap gap-2  phrase-construction-container-width"
					>
						{phraseWords.map((object, index) => (
							<DraggableTextBlock
								wordIndex={object}
								onClickBlock={() =>
									handleTextBlockClick(new Array<WordIndex>(object))
								}
								setWidth={(width: string) =>
									updatePhraseWordsWidth(object, width)
								}
								key={object.word + String(object.index)}
								marginBottom="mb-2"
								isCorrect={false}
							/>
						))}
					</div>
				</div>
			</TrainingBlock>
		</div>
	);
}
);

export default PhraseConstruction;
