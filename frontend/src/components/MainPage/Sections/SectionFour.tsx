import penguinFishes from "../../../assets/penguins/nerd_writer.svg"
import {useTranslation} from "react-i18next";


export const SectionFour = () => {
	const {t} = useTranslation();
	return (
		<section className="w-full h-full">
			<div className="content w-full max-w-[64rem] m-auto  justify-between">
				<div className="flex justify-between  gap-0 sm:gap-14 flex-col sm:flex-row items-center mb-32 ">
					<img alt="" src={penguinFishes}
							 className="mt-0 w-[70%] h-[100%] sm:w-[40%] lg:mt-6 smw-full max-w-[64rem] m-auto py-[18.3px]  max-md:justify-around items-center  flex justify-end"/>

					<div className="flex flex-col w-full justify-center">
						<div className="flex flex-col  gap-4  p-4 xl:p-0">
							<h2 className="font-bold text-blue-600 text-4xl lg:text-5xl mt-8">{t('SectionFourthMainText')}</h2>
							<p className="font-medium text-xl">{t('SectionFourthSubText')}</p>
						</div>
						<div className="flex gap-2  mt-10 ">
						</div>
					</div>
				</div>
				<div>

				</div>
			</div>
		</section>
	)
}
