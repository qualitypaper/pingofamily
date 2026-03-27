import {useEffect} from "react";
import {useDispatch} from "react-redux";
import i18n from "../../i18nf/i18n";
import {Navigate} from "react-router-dom";
import {logOut} from "../../store/user/userSlice";

export const LogOut = () => {
	const dispatch = useDispatch();

	useEffect(() => {
		dispatch(logOut());
	}, []);

	return <Navigate to={`/${i18n.language}/login`}/>;
};
