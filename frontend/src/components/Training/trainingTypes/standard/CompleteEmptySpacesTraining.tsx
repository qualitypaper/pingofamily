import useSimpleTraining from "hooks/useSimpleTraining";
import { memo, ReactNode, useEffect, useState } from "react";
import { Language } from "../../../../store/language/languageTypes";
import { getBlockWidth } from "../../../../utils/globalUtils";
import { addToCurrentCursorPosition, constructMap, removeSpecialCharacters } from "../../../../utils/trainingUtils";
import { constructWordsTranslationSentence, TrainingTypesProps, } from "../../Training";
import TrainingBodyText from "../../TrainingBodyText";
import TrainingBlock from "../TrainingBlock";
import TrainingImage from "../TrainingImage";
import TrainingInput from "./TrainingInput";

const CompleteEmptySpacesTraining = memo(({ elem, targetWord }: TrainingTypesProps) => {
	const wordFrom = targetWord?.wordTranslation?.wordFrom;
	const imageUrl = wordFrom?.imageUrl;
	const identifiedWord = elem.trainingExample.identifiedWord;
	const [firstSentencePart, setFirstSentencePart] = useState<ReactNode[]>();
	const [secondSentencePart, setSecondSentencePart] = useState<ReactNode[]>();

	const simp = useSimpleTraining({ targetWord, elem });

	useEffect(() => {
		const temp = removeSpecialCharacters(
			elem.trainingExample.formattedString
		);
		const constructTrainingSentence = () => {
			const tempArr = temp.split("###");

			const wordsTranslationMapFirst = constructWordsTranslationSentence(
				tempArr[0],
				constructMap(elem.trainingExample?.wordsTranslation || {}),
				wordFrom?.language as Language,
				identifiedWord
			);
			const wordsTranslationMapSecond = constructWordsTranslationSentence(
				tempArr[1],
				constructMap(elem.trainingExample?.wordsTranslation || {})
			);

			setFirstSentencePart(wordsTranslationMapFirst);
			setSecondSentencePart(wordsTranslationMapSecond);
		};

		constructTrainingSentence();
	}, [
		elem.trainingExample,
		elem.trainingExample.formattedString,
		elem.trainingExample?.wordsTranslation,
		elem.trainingExample.identifiedWord,
		identifiedWord.length,
		imageUrl,
		identifiedWord,
		wordFrom?.language,
	]);

	return (
		<div className="h-full content w-full max-w-[64rem] m-auto">
			<TrainingBlock
				isCorrect={simp.correct}
				isSkipped={simp.skipped}
				addChar={(c) =>
					addToCurrentCursorPosition(
						simp.input,
						simp.setInput,
						c,
						simp.inputRef
					)
				}
				showHint={simp.showHint}
				isHint={simp.hint}
				handleSkipClick={simp.handleSkipClick}
			>
				<TrainingImage src={imageUrl ?? simp.image} />
				<div className="flex flex-col items-center text-center justify-center w-full">
					<span className="text-3xl font-semibold mt-5 mb-2 max-md:text-2xl ">
						{firstSentencePart}
						<TrainingInput
							ref={simp.inputRef}
							onChange={(e) => simp.setInput(e.target.value)}
							correctValue={elem.trainingExample.identifiedWord}
							key={`${elem.trainingExample.id}:${elem.trainingExample.sentence}`}
							width={getBlockWidth(identifiedWord)}
							value={simp.input}
							trainingExampleId={elem.trainingExample.id}
							correct={simp.correct ?? false}
						/>
						{secondSentencePart}
					</span>

					<TrainingBodyText
						text={elem.trainingExample.sentenceTranslation}
						visible={simp.hint || simp.correct}
					/>
				</div>
			</TrainingBlock>
		</div>
	);
}
);

export default CompleteEmptySpacesTraining;
