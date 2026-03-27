import {useTranslation} from "react-i18next";
import i18n from '../i18nf/i18n';
import {ReactComponent as SquaresIcon} from 'assets/icons/4squares.svg';

const iconClasses = `h-6 w-6`;

const SidebarRoutes = () => {
	const {t} = useTranslation();

	return [
		{
			path: `/${i18n.language ?? "en"}/vocabularies`,
			icon: <SquaresIcon className={iconClasses}/>,
			name: t('HeaderVocabularies'),
		}
	];
};

export default SidebarRoutes;