import PlaySoundButton from "components/Button/VolumeUpButton";
import useSimpleTraining from "hooks/useSimpleTraining";
import { useRef } from "react";
import {
	addToCurrentCursorPosition,
	isCorrect
} from "../../../../utils/trainingUtils";
import TextAreaInput from "../../../Input/TextAreaInput";
import { TrainingTypesProps } from "../../Training";
import TrainingBlock from "../TrainingBlock";
import { Language } from "store/language/languageTypes";

const SentenceAudio = ({ elem, targetWord }: TrainingTypesProps) => {

	const textareaRef = useRef<HTMLTextAreaElement>(null);

	const simp = useSimpleTraining({ targetWord, elem, onHint, showAnswer, checkCorrect });

	function onHint(): string {
		const words = elem.trainingExample.sentence.split(" ");
		if (words.length < 2) {
			return "";
		}

		return words[0] + " " + words[1];
	}

	function showAnswer() {
		simp.setInput(elem.trainingExample.sentence);
	}

	function checkCorrect(input: string) {
		return isCorrect(input, elem.trainingExample.sentence, targetWord?.wordTranslation?.wordFrom?.language as Language);
	}

	return (
		<TrainingBlock
			isSkipped={simp.skipped}
			isCorrect={simp.correct}
			addChar={(c) =>
				addToCurrentCursorPosition(simp.input, simp.setInput, c, textareaRef)
			}
			handleSkipClick={simp.handleSkipClick}
			showHint={simp.showHint}
			isHint={simp.hint}
		>
			<div className="flex flex-col items-center">
				<PlaySoundButton soundUrl={elem.trainingExample.soundUrl} />
				{(simp.hint || simp.skipped || simp.correct) &&
					<span className="text-2xl font-semibold my-2 max-md:text-2xl max-md:mt-3 max-md:mb-3">
						{elem.trainingExample.sentenceTranslation}
					</span>
				}
				<TextAreaInput
					autoFocus
					disabled={simp.correct}
					value={simp.input || ""}
					updateFormValue={s => simp.setInput(s)}
					labelStyle=""
					labelValue=""
				/>
			</div>
		</TrainingBlock>
	);
};

export default SentenceAudio;
