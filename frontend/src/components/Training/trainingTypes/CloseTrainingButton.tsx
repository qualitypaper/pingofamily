import { useState } from "react";
import { ReactComponent as CloseIcon } from "assets/icons/close.svg";
import TrainingClosePopup from "../TrainingClosePopup";

export const CloseTrainingButton = ({
  onBackClick,
  className,
}: {
  onBackClick: () => void;
  className: string;
}) => {
  const [slidingForm, setSlidingForm] = useState(false);

  const click = () => {
    setSlidingForm(true);
  };

  return (
    <>
      <button
        className={`flex items-start justify-center pl-5 left-0 z-50 relative ${className}`}
        onClick={click}
      >
        <CloseIcon className="!w-6 !h-6 sm:!w-9 absolute sm:!h-9 hover:bg-gray-100 image-full p-0 md:p-[4px] text-color-big-text rounded-full duration-300 ease-out transition" />
      </button>
      {slidingForm && (
        <TrainingClosePopup setSlidingForm={setSlidingForm} onBackClick={onBackClick} />
      )}
    </>
  );
};
