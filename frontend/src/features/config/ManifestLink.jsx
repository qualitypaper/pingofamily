import {Helmet} from "react-helmet-async";
import {useTranslation} from "react-i18next";

const ManifestLink = () => {
	const {i18n} = useTranslation();
	const lang = i18n.language || "en";
	return (
		<Helmet>
			<link rel="manifest" href={`/manifest.${lang}.json`}/>
		</Helmet>
	);
};

export default ManifestLink;
