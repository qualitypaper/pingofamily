import { useMediaQuery } from "@react-hook/media-query";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import LanguageSelect from "../components/Footer/LanguageSelect";
import { WEBSITE_NAME } from "../constant";
import { LogoSidebar } from "./Header/LeftSidebar";

export const Footer = ({ className }: { className?: string }) => {
	const { t } = useTranslation();
	const isMobile = useMediaQuery("(max-width: 480px)");

	return (
		<footer className={`${className} flex flex-col  bg-gray-50  w-full m-auto relative snow`}>
			<div className="flex flex-col gap-2 m-auto w-full bg-gray-50  max-w-[64rem] py-4 px-4">
				<div className="flex justify-between items-center">
					<LogoSidebar className="text-[0.9rem] text-color-big-text font-bold lg:text-2xl"
						title={isMobile ? "" : WEBSITE_NAME} />
					<div className="flex gap-4 items-center">
						<LanguageSelect displayLabel />
					</div>
				</div>
			</div>

			<CopyrightNotice text={t("CopyrightNotice")} />
		</footer>
	);
};

const CopyrightNotice = ({ text }: { text: string }) => {
	const {t, i18n} = useTranslation();

	return (
		<div
			className="gap-3 bottom-0 border-t border-slate-300 text-center w-full p-4 bg-gray-50  text-slate-800 text-xs flex justify-center items-center">
			<div>
				&copy; {new Date().getFullYear()} {text}
			</div>
			<Link className="hover:underline" to={`/${i18n.language}/privacy-policy`}>
				Privacy Policy
			</Link>

			<Link to={`/${i18n.language}/contact-us`} className="hover:underline">
				{t("FooterContactUs")}
			</Link>
		</div>
	)
}
