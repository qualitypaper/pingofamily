import {WordIndex} from "../PhraseConstruction";
import DraggableTextBlock from "./DraggableTextBlock";

type DropzoneProps = {
	isMobile: boolean;
	droppedTexts: WordIndex[];
	onTextBlockClick: (wordIndex: WordIndex) => void;
	isCorrect: boolean;
	initialPhraseWordsLength: number;
};

type DropzoneLineProps = {
	top?: boolean;
};

const DropzoneLine = ({top}: DropzoneLineProps) => {
	return (
		<div
			className={`${
				top && "border-t-0"
			} w-full border-2 h-[60px] border-r-0 border-l-0 border-slate-200 phrase-construction-container-width`}
		/>
	);
};

const Dropzone = ({
										droppedTexts,
										onTextBlockClick,
										isCorrect,
										initialPhraseWordsLength,
									}: DropzoneProps) => {
	const isMobile = window.innerWidth < 600;
	const requiredLines = isMobile
		? Math.ceil(initialPhraseWordsLength / 2.5)
		: Math.ceil(initialPhraseWordsLength / 4);
	const numLines = Math.max(requiredLines, 3);

	return (
		<div
			className={`gap-1 flex flex-row flex-nowrap  relative`}
			style={{pointerEvents: isCorrect ? "none" : "auto"}}
		>
			<div className="flex flex-col">
				{Array.from({length: numLines}, (_, i) => (
					<DropzoneLine key={i} top={i + 1 >= 0}/>
				))}
			</div>

			<div className="absolute flex flex-wrap items-center my-1.5 gap-x-2 phrase-construction-container-width">
				{droppedTexts.map((object, index) => (
					<DraggableTextBlock
						setWidth={(width: string) => console.log(width)}
						wordIndex={object}
						key={object.word + object.index}
						marginBottom="mb-3"
						onClickBlock={() => onTextBlockClick(object)}
						isCorrect={isCorrect}
					/>
				))}
			</div>
		</div>
	);
};

export default Dropzone;
