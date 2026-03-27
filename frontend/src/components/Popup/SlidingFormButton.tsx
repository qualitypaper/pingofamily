import { useNavigate } from "react-router-dom";
import i18n from "../../i18nf/i18n";
import { Button } from "@chakra-ui/react";


const SlidingFormButton = ({ onBackClick, setSlidingForm }: {
	onBackClick: () => void,
	setSlidingForm: (value: boolean) => void
}) => {
	const navigate = useNavigate();

	const clickHandler = () => {
		onBackClick()
		navigate(`/${i18n.language}/vocabularies`)
	}
	return (
		<div className="flex justify-start gap-2">
			<Button
				onClick={clickHandler}
				className="btn capitalize w-[6rem] bg-red-400 btn-ghost hover:bg-red-600 text-white font-bold rounded"
			>
				Exit
			</Button>
			<Button
				onClick={() => setSlidingForm(false)}
				className="btn capitalize w-[6rem] bg-color-big-text btn-ghost hover:bg-blue-500 text-white font-bold rounded">Cancel
			</Button>
		</div>
	)
}

export default SlidingFormButton;
