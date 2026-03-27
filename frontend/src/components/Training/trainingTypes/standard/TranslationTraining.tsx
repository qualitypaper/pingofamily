import useSimpleTraining from "hooks/useSimpleTraining";
import {getBlockWidth} from "../../../../utils/globalUtils";
import {addToCurrentCursorPosition} from "../../../../utils/trainingUtils";
import {TrainingTypesProps} from "../../Training";
import TrainingBodyText from "../../TrainingBodyText";
import TrainingBlock from "../TrainingBlock";
import TrainingImage from "../TrainingImage";
import TrainingInput from "./TrainingInput";

const TranslationTraining = ({targetWord, elem}: TrainingTypesProps) => {
	const simp = useSimpleTraining({targetWord, elem});

	return (
		<TrainingBlock
			isSkipped={simp.skipped}
			isCorrect={simp.correct}
			addChar={(c) => addToCurrentCursorPosition(simp.input, simp.setInput, c, simp.inputRef)}
			handleSkipClick={simp.handleSkipClick}
			showHint={simp.showHint}
		>
			<div className={`flex flex-col items-center`}>
				<TrainingImage
					src={targetWord.wordTranslation.wordFrom.imageUrl}
					className="mb-0"
				/>
				<TrainingBodyText
					text={targetWord.wordTranslation.wordTo.word}
					subtext={targetWord.wordTranslation.wordFrom.partOfSpeech.toLowerCase()}
					color="primary"
				/>
				<TrainingInput
					ref={simp.inputRef}
					width={getBlockWidth(elem.trainingExample.identifiedWord)}
					onChange={(e) => simp.setInput(e.target.value)}
					value={simp.input}
					correct={simp.correct ?? false}
					trainingExampleId={elem.trainingExample.id}
					correctValue={elem.trainingExample.identifiedWord}
				/>
			</div>
		</TrainingBlock>
	);
};
export default TranslationTraining;
