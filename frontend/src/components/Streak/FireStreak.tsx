import React, { useState } from "react";
import StreakPopup from "./StreakPopup";
import { ReactComponent as Fire } from "assets/icons/fire.svg";
import { ReactComponent as FireZero } from "assets/icons/fire_zero.svg";
import { useSelector } from "react-redux";
import { selectUserStreak } from "store/user/userSelector";

export const FireStreak = () => {
  const { currentStreak } = useSelector(selectUserStreak);
  const [open, setOpen] = useState<boolean>(false);

  return (
    <div>
      <button onClick={() => setOpen(true)} className="items-center flex">
        {!currentStreak || currentStreak === 0 ? (
          <FireZero className="w-9 h-9" />
        ) : (
          <Fire className="w-8 h-8" />
        )}
        <p className={`${currentStreak === 0 ? "text-gray-500" : "text-black"} text-xl`}>
          {String(currentStreak ?? 0)}
        </p>
      </button>
      {open && <StreakPopup setOpen={setOpen} />}
    </div>
  );
};
