import PlaySoundButton from "components/Button/VolumeUpButton";
import { FC } from "react";
import { Loading } from "../../components/Loading/Loading";

type SpanContainerProps = {
  item1: string;
  item2: string;
  additionalStyles1?: string;
  additionalStyles2?: string;
  loading?: boolean;
  soundUrl: string;
};

export const ItemPairContainer: FC<SpanContainerProps> = ({
  item1,
  item2,
  additionalStyles1,
  additionalStyles2,
  soundUrl,
  loading,
}: SpanContainerProps) => {

  return (
    <>
      <span
        className={`text-md flex gap-1 items-center lg:text-xl md:text-lg ${additionalStyles1}`}
      >
        <PlaySoundButton soundUrl={soundUrl} />
        <span className="text-color-big-text">{item1}</span>
      </span>

      <span className={`md:break-all lg:text-lg max-md:text-md text-md  ${additionalStyles2}`}>
        {item2}
      </span>
      {loading && <Loading />}
    </>
  );
};
