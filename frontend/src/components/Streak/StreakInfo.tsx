import LinearProgress from "components/Loading/LinearProgress";
import { useTranslation } from "react-i18next";

interface StreakInfoProps {
  title: string;
  value: number;
  barValue?: number;
}

const StreakInfo = ({ title, value, barValue }: StreakInfoProps) => {
  const { t } = useTranslation();

  return (
    <div className="flex flex-col gap-2">
      <p className="font-semibold">{title}</p>
      <div className="flex justify-between items-center gap-2">
        <p className="font-semibold text-xl">
          {value} {t("Days")}
        </p>
        <LinearProgress value={barValue ?? 0} color="orange" />
      </div>
    </div>
  );
};

export default StreakInfo;
