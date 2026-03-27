import {useLocation} from "react-router-dom";
import {useEffect} from "react";


function UrlMapper() {

	const location = useLocation();

	useEffect(() => {
		console.log("Url changed", location);
	}, [location]);

	return (
		<div>
			UrlMapper
		</div>
	)
}

export default UrlMapper;
