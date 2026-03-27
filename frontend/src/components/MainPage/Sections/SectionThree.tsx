import {useTranslation} from "react-i18next";
import penguinIceberg from "../../../assets/penguins/nerd_book.svg";

export const SectionThree = () => {
	const {t} = useTranslation();

	return (
		<section className="w-full h-full">
			<div className="content w-full max-w-[64rem] m-auto justify-between">
				<div className="flex justify-between flex-col sm:flex-row items-center gap-0 sm:gap-14 mt-4">
					<img alt="" src={penguinIceberg}
							 className="mt-0 w-[70%] h-[100%] sm:w-[40%] lg:mt-6 smw-full max-w-[64rem]
						 	m-auto flex justify-between max-md:justify-around items-center sm:order-last "/>
					<div className="flex flex-col w-full justify-center">
						<div className="flex flex-col gap-4 px-4 pt-0 pb-4 xl:p-0">
							<h1 className="font-bold text-blue-600 text-4xl lg:text-5xl mt-4">{t('SectionThirdMainText')}</h1>
							<p
								className="font-medium text-xl"
								dangerouslySetInnerHTML={{__html: t('SectionThirdSubText')}}
							/>
						</div>
					</div>
				</div>
			</div>
		</section>
	)
}
