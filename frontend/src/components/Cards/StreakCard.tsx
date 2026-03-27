import { Dispatch, SetStateAction, useEffect } from "react";
import { WrappedTitleCard } from "./TitleCard";
import penguinStreak from "../../assets/penguins/fat_fire.svg";
import FireGif from "../../assets/animations/FireGif.gif";
import { useSelector } from "react-redux";
import { ReactComponent as CheckMark } from "../../assets/icons/Check.svg";
import { selectUserStreak } from "../../store/user/userSelector";
import { useTranslation } from "react-i18next";

interface StreakCardProps {
  setShowTrainingComplete: Dispatch<SetStateAction<boolean>>;
  showStreak: Date | null;
  setShowStreak: Dispatch<SetStateAction<Date | null>>;
}

export const StreakCard = ({
  setShowTrainingComplete,
  showStreak,
  setShowStreak,
}: StreakCardProps) => {
  const { currentStreak } = useSelector(selectUserStreak);
  const { t } = useTranslation();
  const days = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
  const today = new Date();
  const startOfWeak = new Date();
  startOfWeak.setDate(startOfWeak.getDate() - startOfWeak.getDay());

  useEffect(() => {
    const showStreakCard = () => {
      if (!showStreak) return true;
      const now = new Date();
      const hoursPassed = (now.getTime() - new Date(showStreak).getTime()) / (1000 * 60 * 60);
      return hoursPassed >= 24;
    };

    if (showStreakCard()) {
      setShowStreak(new Date());
    }
  }, [showStreak, setShowStreak]);

  const renderCalendar = () => {
    const rows = [];
    for (let i = 0; i < 7; i++) {
      const isTodayStreak = startOfWeak.getDate() === today.getDate();
      const isYesterdayStreak = startOfWeak.getDate() === today.getDate() - 1;

      rows.push(
        <div key={i} className="flex flex-col items-center justify-center gap-3">
          <span>{days[startOfWeak.getDay()]}</span>
          <div className="border-b h-1 w-full"></div>
          <div className="flex items-center justify-center w-10 h-10">
            {isTodayStreak ? (
              <div className="relative flex items-center justify-center">
                <span className="absolute bg-[#69AFFF] rounded-full w-10 h-10" />

                <span className="relative text-2xl">
                  <CheckMark />
                </span>
              </div>
            ) : (
              <span className={`${isYesterdayStreak ? "bg-blue-100 p-2 rounded-full" : ""}`}>
                {startOfWeak.getDate()}
              </span>
            )}
          </div>
        </div>,
      );
      startOfWeak.setDate(startOfWeak.getDate() + 1);
    }
    return rows;
  };

  return (
    <div className="h-screen flex items-center justify-center">
      <WrappedTitleCard alignment="grid place-items-center text-center" title="">
        <div className={`flex flex-col items-center gap-4`}>
          <div className="flex flex-wrap  flex-col items-center m-0 lg:m-2 p-0 lg:p-4 gap-3">
            <img src={penguinStreak} className="w-[33em] h-[33em]" alt="Penguin" />
            <div className="flex  items-center gap-2">
              <p className="  text-[2em] lg:text-[3em]  font-semibold ">{t("StreakCardText")}</p>
              <img className="w-[2em] h-[2em]" src={FireGif} alt="" />
            </div>
            <div className="text-[25px] font-semibold flex items-center gap-2">
              {t("CurrentStreak")}{" "}
              <p className="text-[30px] font-semibold text-[#1D4AFF]">
                {currentStreak} {t("Day")}
              </p>
            </div>
          </div>
          <div className="flex bg-[#D9D9D9] border-black  rounded-xl bg-opacity-15 p-3 gap-2">
            {renderCalendar()}
          </div>
          <div className="flex flex-wrap justify-center w-full p-4">
            <button
              onClick={() => setShowTrainingComplete(true)}
              className="bg-[#0C7DFF] text-white w-[35%] mx-auto rounded-xl p-[0.6rem] font-semibold"
            >
              {t("StreakCardTextTwo")}
            </button>
          </div>
        </div>
      </WrappedTitleCard>
    </div>
  );
};

export default StreakCard;
