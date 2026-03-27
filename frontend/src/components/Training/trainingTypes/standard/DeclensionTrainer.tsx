import React, {useEffect, useState} from "react";
import {useDispatch} from "react-redux";
import {
	incrementFinalSequenceIndex,
	incrementHintsNumber,
	incrementMistakeNumber
} from "../../../../store/training/trainingSlice";
import {TrainingTypesProps} from "../../Training";
import TrainingBlock from "../TrainingBlock";
import {useTranslation} from "react-i18next";

const declineWord = (word: string, language: string) => {
	const declensions: any = {
		nominative: word,
		genitive: getGenitive(word, language),
		dative: getDative(word, language),
		accusative: getAccusative(word, language),
	};
	return declensions;
};

const getGenitive = (word: string, language: string) => {
	if (language === "german") {
		if (word.endsWith("s")) {
			return word + "'";
		}
		return word + "'s";
	}
	return word;
};

const getDative = (word: string, language: string) => {
	if (language === "german") {
		return `to ${word}`;
	}
	return `to ${word}`;
};

const getAccusative = (word: string, language: string) => {
	if (language === "german") {
		const pronouns = {
			I: "mich",
			you: "dich",
			he: "ihn",
			she: "sie",
			it: "es",
			we: "uns",
			they: "sie",
		};
		return pronouns[word as keyof typeof pronouns] || word;
	}
	if (language === "spanish") {
		const pronouns = {
			I: "me",
			you: "te",
			he: "lo",
			she: "la",
			it: "lo",
			we: "nos",
			they: "los",
		};
		return pronouns[word as keyof typeof pronouns] || word;
	}
	return word;
};

const DeclensionTrainer = ({targetWord, elem}: TrainingTypesProps) => {
	const dispatch = useDispatch();

	const [nominative, setNominative] = useState<string>("");
	const [genitive, setGenitive] = useState<string>("");
	const [dative, setDative] = useState<string>("");
	const [accusative, setAccusative] = useState<string>("");
	const [correct, setCorrect] = useState<boolean>(false);
	const [skipped, setSkipped] = useState<boolean>(false);

	const {t} = useTranslation();
	const declension = {
		nominative: "Normative",
		genitive: "Genitive",
		dative: "Dative",
		accusative: "Accusative",
	};

	const language = "german";
	const textDecl = declineWord(targetWord.wordTranslation.wordTo.word, language);

	const showCorrectAnswer = () => {
		setNominative(textDecl.nominative);
		setGenitive(textDecl.genitive);
		setDative(textDecl.dative);
		setAccusative(textDecl.accusative);

		dispatch(incrementMistakeNumber({index: elem.index, trainingType: elem.trainingExample.trainingType}));
		setCorrect(false);
		setSkipped(true);
	};

	const showHint = () => {
		dispatch(incrementHintsNumber({index: elem.index, trainingType: elem.trainingExample.trainingType}));
	};

	const onChangeNominative = (event: React.ChangeEvent<HTMLInputElement>) => {
		setNominative(event.target.value);
		if (event.target.value.trim() === textDecl.nominative) {
			setCorrect(true);
		}
	};

	const onChangeGenitive = (event: React.ChangeEvent<HTMLInputElement>) => {
		setGenitive(event.target.value);
		if (event.target.value.trim() === textDecl.genitive) {
			setCorrect(true);
		}
	};

	const onChangeDative = (event: React.ChangeEvent<HTMLInputElement>) => {
		setDative(event.target.value);
		if (event.target.value.trim() === textDecl.dative) {
			setCorrect(true);
		}
	};

	const onChangeAccusative = (event: React.ChangeEvent<HTMLInputElement>) => {
		setAccusative(event.target.value);
		if (event.target.value.trim() === textDecl.accusative) {
			setCorrect(true);
		}
	};

	useEffect(() => {
		const allCorrect =
			nominative.trim() === textDecl.nominative &&
			genitive.trim() === textDecl.genitive &&
			dative.trim() === textDecl.dative &&
			accusative.trim() === textDecl.accusative;

		if (allCorrect) {
			setSkipped(true);
			setCorrect(true);
		}
	}, [nominative, genitive, dative, accusative, textDecl]);

	const handleSkipClick = () => {
		const anyCorrect =
			nominative.trim() === textDecl.nominative ||
			genitive.trim() === textDecl.genitive ||
			dative.trim() === textDecl.dative ||
			accusative.trim() === textDecl.accusative;

		if (correct || anyCorrect) {
			setNominative("");
			setGenitive("");
			setDative("");
			setAccusative("");
			setSkipped(false);
			setCorrect(false);
			dispatch(incrementFinalSequenceIndex());
		} else {
			showCorrectAnswer();
		}
	};

	return (
		<TrainingBlock
			isSkipped={skipped}
			isCorrect={correct}
			addChar={setNominative}
			handleSkipClick={handleSkipClick}
			showHint={showHint}
		>
			<div>
				<h1 className="text-3xl">{targetWord.wordTranslation.wordTo.word}</h1>
				<table className="table-auto border-collapse border border-black w-full mt-4">
					<thead>
					<tr>
						<th className="border border-black px-4 py-2">{t('Declination')}</th>
						<th className="border border-black px-4 py-2">{t('YourAnswer')}</th>
					</tr>
					</thead>
					<tbody>
					<tr>
						<td className="border border-black px-4 py-2">{declension.nominative}</td>
						<td className="border border-black px-4 py-2">
							<input
								value={nominative}
								onChange={onChangeNominative}
								className={`outline-none text-md lg:text-lg ${
									nominative.trim() !== textDecl.nominative ? "border border-[#1154FF] border-opacity-80 rounded-lg py-1 px-2 w-full bg-white" : "bg-green-50 border-[1px] border-green-400 rounded-lg py-1 px-2 w-full"
								}`}
								type="text"
							/>
						</td>
					</tr>
					<tr>
						<td className="border border-black px-4 py-2">{declension.genitive}</td>
						<td className="border border-black px-4 py-2">
							<input
								value={genitive}
								onChange={onChangeGenitive}
								className={`outline-none text-md lg:text-lg ${
									genitive.trim() !== textDecl.genitive ? "border border-[#1154FF] border-opacity-80 rounded-lg py-1 px-2 w-full bg-white" : "bg-green-50 border-[1px] border-green-400 rounded-lg py-1 px-2 w-full"
								}`}
								type="text"
							/>
						</td>
					</tr>
					<tr>
						<td className="border border-black px-4 py-2">{declension.dative}</td>
						<td className="border border-black px-4 py-2">
							<input
								value={dative}
								onChange={onChangeDative}
								className={`outline-none text-md lg:text-lg ${
									dative.trim() !== textDecl.dative ? "border border-[#1154FF] border-opacity-80 rounded-lg py-1 px-2 w-full bg-white" : "bg-green-50 border-[1px] border-green-400 rounded-lg py-1 px-2 w-full"
								}`}
								type="text"
							/>
						</td>
					</tr>
					<tr>
						<td className="border border-black px-4 py-2">{declension.accusative}</td>
						<td className="border border-black px-4 py-2">
							<input
								value={accusative}
								onChange={onChangeAccusative}
								className={`outline-none text-md lg:text-lg ${
									accusative.trim() !== textDecl.accusative ? "border border-[#1154FF] rounded-lg py-1 px-2 border-opacity-80 w-full bg-white" : "bg-green-50 border-[1px] border-green-400 rounded-lg py-1 px-2 w-full"
								}`}
								type="text"
							/>
						</td>
					</tr>
					</tbody>
				</table>
			</div>
		</TrainingBlock>
	);
};

export default DeclensionTrainer;
