import { useTranslation } from "react-i18next";
import TrainingButton from "./trainingTypes/TrainingButton";

type SkipButtonProps = {
	handleSkipClick: () => void;
	isCorrect: boolean;
};

const SkipButton = ({ isCorrect, handleSkipClick }: SkipButtonProps) => {
	const { t } = useTranslation();

	return (
		<>
			<div className="flex justify-center flex-grow-0">
				<TrainingButton
					onClick={handleSkipClick}
					style={{ backgroundColor: isCorrect ? "#2fd56c" : undefined }}
				>
					<span className="text-white font-bold text-xl">
						{isCorrect ? t("Continue") : t("Skip")}
					</span>
				</TrainingButton>
			</div>
		</>
	);
};

export default SkipButton;
