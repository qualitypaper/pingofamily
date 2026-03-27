import {MouseEventHandler, useEffect} from "react";
import {WordIndex} from "../PhraseConstruction";

export interface DraggableTextBlockProps {
	wordIndex: WordIndex;
	onClickBlock: MouseEventHandler<HTMLSpanElement>;
	setWidth: (width: string) => void;
	isCorrect: boolean;
	marginBottom: string
}

const DraggableTextBlock = ({wordIndex, onClickBlock, setWidth, isCorrect, marginBottom}: DraggableTextBlockProps) => {

	useEffect(() => {
		if (wordIndex?.word?.trim() !== "emptyyy" && !wordIndex?.width) {
			setWidth(wordIndex?.word?.length + 'ch');
		}
	}, [setWidth, wordIndex?.word, wordIndex?.width]);

	if (!wordIndex?.word)
		return (<span style={{width: wordIndex?.width}} className={`inline-flex w-[${wordIndex?.width}]`}></span>);
	const {word, width} = wordIndex;


	const className = `${marginBottom} px-[1rem] py-[0.5rem] cursor-pointer 
        border-color-slate-50 border-b-4 border-t-2 border-l-2 border-r-2  hover:border-blue-200 dark:border-slate-50 
         rounded-md text-[1.1rem] ${isCorrect ? 'bg-green-100 border-green-200' : 'bg-slate-50'}`;

	return (
		<button
			onClick={word.trim() !== "emptyyy" ? onClickBlock : undefined}
			className={className}
		>
			{word.trim() !== "emptyyy" ? (
				<span className="inline-flex justify-center" style={{width, pointerEvents: "none"}}>
                    {word}
                </span>
			) : (
				<span style={{width}} className={`inline-flex w-[${width}]`}></span>
			)}
		</button>
	);
};

export default DraggableTextBlock;