import {LanguagesProps} from "./SectionLanguage";
import {useEffect, useRef} from "react";
import {Link} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {ReactComponent as CloseIcon} from 'assets/icons/close.svg';

interface StartLearningProps {
	onClose: () => void
	languages: LanguagesProps[]
	open: boolean
	selectedLanguage: string
}

const StartLearning = ({onClose, languages, selectedLanguage}: StartLearningProps) => {
	const dataLanguages = languages.filter((v) => v.value === selectedLanguage)
	const isValid = JSON.parse(sessionStorage.getItem('isValidated') ?? "{}").valid;
	const ref = useRef<HTMLDivElement>(null)
	const {t} = useTranslation()

	useEffect(() => {
		const outsideClick = (event: any) => {
			if (ref.current) {
				if (!ref.current?.contains(event.target)) {
					onClose()
				}
			}
		}
		document.addEventListener('mousedown', outsideClick, true)

		return () => {
			document.removeEventListener('mousedown', outsideClick, true)
		}
	}, [onClose])

	useEffect(() => {
		const clickESC = (event: any) => {
			if (event.key === "Escape") {
				onClose()
			}
		}
		document.addEventListener('mousedown', clickESC, true)

		return () => {
			document.removeEventListener('mousedown', clickESC, true)
		}
	}, [onClose]);

	console.log(dataLanguages)
	return (
		<div className="fixed inset-0 z-50 flex items-center justify-center w-full h-full bg-opacity-60 bg-black">
			<div ref={ref}
					 className="bg-[#F6F9FF] p-6 rounded-lg w-[20rem] lg:w-[30rem] sm:w-[25rem] flex flex-col h-[38rem] relative gap-4">
				<button
					className="absolute right-2 top-2 cursor-pointer bg-opacity-0 hover:bg-opacity-50 transition-all duration-300 ease-out p-1 rounded-full"
					onClick={onClose}
				>
					<CloseIcon/>
				</button>
				<div className="flex items-center justify-center gap-4">
					<h1 className="text-4xl lg:text-5xl font-bold">{dataLanguages[0].value}</h1>
					{dataLanguages[0].flag}
				</div>
				<img className="rounded-l" src={dataLanguages[0].img} alt=""/>
				<p className="mt-1 text-gray-600 font-bold text-[0.9rem] lg;text-[1rem] ">
					{dataLanguages[0].label}
				</p>
				{isValid ? (
					<Link to="vocabularies"
								className="mt-auto rounded-2xl bg-[#1E90FF] text-white text-center  py-1 px-4  w-[70%] mx-auto">
						<p
							className="font-semibold text-center text-xl lg:text-2xl text-white p-1 truncate">{t("StartLearning")}</p>
					</Link>
				) : (
					<Link to="login"
								className="mt-auto rounded-2xl bg-[#1E90FF] text-white  py-1 px-4  w-[70%] mx-auto">
						<p
							className="font-semibold text-xl lg:text-2xl text-white p-1 text-center truncate">{t("StartLearning")}</p>
					</Link>
				)

				}
			</div>
		</div>
	)
}

export default StartLearning
