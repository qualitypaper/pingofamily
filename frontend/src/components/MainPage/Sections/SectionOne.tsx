import {useTranslation} from "react-i18next";
import {Link} from "react-router-dom";
import {ReactComponent as ArrowForward} from 'assets/icons/arrow.svg';
import {ReactComponent as PingoFamily} from "assets/penguins/pingofamily.svg";
import TypeWriter from "../TypeWriter";

export const SectionOne = () => {
	const isValid = JSON.parse(sessionStorage.getItem('isValidated') ?? "{}").valid;
	const {t} = useTranslation();
	const texts = [t("SectionLanguageOne"), t("SectionLanguageTwo"), t("SectionLanguageThree")];


	return (
		<div className="h-full w-full max-w-[64rem] m-auto mt-2 justify-between">
			<div className="flex justify-between flex-col sm:flex-row lg:gap-24 items-center gap-0 sm:gap-0 h-100vh">
				<PingoFamily
					className="mt-0 w-[90%] h-[100%] lg:mt-6 mw-full max-w-[64rem] m-auto
                        flex justify-between max-md:justify-around items-center sm:order-last"
				/>

				<div className="flex flex-col w-full justify-center px-4 pt-1 pb-4 xl:p-0">
					<div className="flex flex-col gap-4">
						<h1 className="font-bold mt-4 text-4xl lg:text-5xl ">
							{t("SectionOneMainText")}
							<br/>

							<TypeWriter texts={texts}/>
						</h1>
						<p className="font-medium text-xl">{t("SectionOneSubText")}</p>
					</div>
					<div className="flex gap-2 mt-4">
						{
							isValid ? (
								<Link to="vocabularies"
											className="flex p-2 btn-started rounded-md justify-center items-center w-full sm:w-[12rem]">
									<span className="font-semibold text-white">{t("GoToAccount")}</span>
									<ArrowForward fill="#fff"/>
								</Link>
							) : (
								<Link to="login"
											className="flex p-2 btn-started rounded-md justify-center items-center w-full sm:w-[12rem]">
									<span className="text-white font-semibold">{t("SectionOneButtonText")}</span>
									<ArrowForward fill="#fff"/>
								</Link>
							)
						}
					</div>
				</div>

			</div>
		</div>
	);
}
