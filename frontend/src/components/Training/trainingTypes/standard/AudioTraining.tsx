import {getBlockWidth, retry} from "utils/globalUtils";
import {addToCurrentCursorPosition, playSound,} from "../../../../utils/trainingUtils";
import {TrainingTypesProps} from "../../Training";
import TrainingBodyText from "../../TrainingBodyText";
import TrainingBlock from "../TrainingBlock";
import TrainingImage from "../TrainingImage";
import TrainingInput from "./TrainingInput";
import useSimpleTraining from "hooks/useSimpleTraining";
import {useEffect} from "react";
import PlaySoundButton from "../../../Button/VolumeUpButton";

const AudioTraining = ({elem, targetWord}: TrainingTypesProps) => {
	const simp = useSimpleTraining({targetWord, elem});
	const {soundUrl, identifiedWord} = elem.trainingExample;
	const {imageUrl} = targetWord.wordTranslation.wordFrom;

	useEffect(() => {
		if (document.readyState === "complete") {
			setTimeout(() => {
				retry(() => playSound(soundUrl), 5);
			}, 400);
		}
	}, [soundUrl]);

	return (
		<TrainingBlock
			isSkipped={simp.skipped}
			isCorrect={simp.correct}
			showHint={simp.showHint}
			addChar={(c) =>
				addToCurrentCursorPosition(simp.input, simp.setInput, c, simp.inputRef)
			}
			handleSkipClick={simp.handleSkipClick}
		>
			<div className={`flex flex-col items-center`}>
				<TrainingImage src={imageUrl}/>
				<PlaySoundButton soundUrl={soundUrl}/>

				<TrainingInput
					ref={simp.inputRef}
					width={getBlockWidth(identifiedWord)}
					value={simp.input}
					onChange={(e) => simp.setInput(e.target.value)}
					correct={simp.correct}
					trainingExampleId={elem.trainingExample.id}
					correctValue={identifiedWord}
				/>

				<TrainingBodyText
					visible={simp.hint || simp.correct}
					text={targetWord.wordTranslation.wordTo.word}
					subtext={targetWord.wordTranslation.wordFrom.partOfSpeech.toLowerCase()}
				/>
			</div>
		</TrainingBlock>
	);
};
export default AudioTraining;
