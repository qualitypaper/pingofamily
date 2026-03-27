import { API } from "app/init";
import { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { Bar, BarChart, CartesianGrid, Legend, Tooltip, XAxis, YAxis } from "recharts";
import { STATISTICS_PERIOD } from "../../../constant";
import { selectTokenPair } from "../../../store/user/userSelector";
import { extractDate, isMobile } from "../../../utils/globalUtils";

type Statistic = {
  completedAt: number;
  vocabularyId: number;
  lsId: number;
};

type ChartData = {
  date: string;
  trainingCount: number;
};

const Statistics = () => {
  const { accessToken } = useSelector(selectTokenPair);
  const [data, setData] = useState<ChartData[]>();

  useEffect(() => {
    const fetch = async () => {
      const res = await API.get(`/user/get-statistics?days=${STATISTICS_PERIOD}`);
      if (!res?.data) return;
      const statistics = res.data as Statistic[];
      const temp: ChartData[] = [];

      const xAxis = [];
      for (let i = STATISTICS_PERIOD - 1; i >= 0; i--) {
        xAxis.push(extractDate(Date.now() - i * 24 * 3600 * 1000));
      }

      for (let i = STATISTICS_PERIOD - 1; i >= 0; i--) {
        const date = xAxis[i];
        const filtered = statistics.filter((x) => extractDate(x.completedAt) === date);
        temp.unshift({ date, trainingCount: filtered.length });
      }
      setData(temp);
    };
    fetch().then();
  }, [accessToken]);

  console.log(data);

  return (
    <div className="flex justify-center items-center m-10">
      <BarChart width={isMobile() ? 350 : 600} height={isMobile() ? 300 : 400} data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="date" />
        <YAxis />
        <Tooltip />
        <Legend />
        <Bar dataKey="trainingCount" name="Training Count" fill="#FC9502" />
      </BarChart>
    </div>
  );
};

export default Statistics;
