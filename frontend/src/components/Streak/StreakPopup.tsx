import { Dispatch, SetStateAction } from "react";
import { ReactComponent as Fire } from "../../assets/icons/fire.svg";
import SlidingForm from "../Popup/SlidingForm";
import { useSelector } from "react-redux";
import { selectUserStreak } from "../../store/user/userSelector";
import StreakInfo from "./StreakInfo";
import LineSeparator from "../Cards/LineSeparator";
import { useTranslation } from "react-i18next";

interface PopupContainerProps {
  setOpen: Dispatch<SetStateAction<boolean>>;
}

const StreakPopup = ({ setOpen }: PopupContainerProps) => {
  const { currentStreak, maxStreak } = useSelector(selectUserStreak);
  const { t } = useTranslation();

  console.log(currentStreak, maxStreak);

  return (
    <SlidingForm
      showCloseButton={false}
      className="w-full md:w-[42em]"
      onClose={() => setOpen(false)}
    >
      <div className="bg-[#FFE8CC] p-4 flex flex-col justify-center rounded-3xl gap-2">
        <div className="flex items-center">
          <Fire width="48" height="48" />
          <h2 className="font-semibold text-2xl">{t("YourStreak")}</h2>
        </div>
        <div className="bg-white flex flex-col justify-center w-full p-4 rounded-3xl">
          <StreakInfo
            title="Current"
            value={currentStreak}
            barValue={maxStreak === 0 ? 0 : (currentStreak * 100) / maxStreak}
          />
          <LineSeparator />
          <StreakInfo title="Longest" value={maxStreak} barValue={maxStreak === 0 ? 0 : 100} />
        </div>
      </div>
    </SlidingForm>
  );
};

export default StreakPopup;
