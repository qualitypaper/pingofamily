import { Progress } from "@chakra-ui/react";
import { isMobile } from "utils/globalUtils";

export type LinearProgressProps = {
  value?: number;
  color?: string;
};

const LinearProgress = ({ value, color }: LinearProgressProps) => {
  return (
    <Progress.Root
      width={isMobile() ? "10rem" : "15rem"}
      value={value ?? 100}
      colorPalette={color ?? "cyan"}
      variant="outline"
    >
      <Progress.Track>
        <Progress.Range />
      </Progress.Track>
    </Progress.Root>
  );
};

export default LinearProgress;
