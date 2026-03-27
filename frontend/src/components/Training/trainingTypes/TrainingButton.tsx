import { Button } from "@chakra-ui/react";

export type TrainingButtonProps = {
  style?: React.CSSProperties;
  children: React.ReactNode;
  color?: string;
} & React.HTMLAttributes<HTMLButtonElement>;

const TrainingButton = ({
  style,
  color,
  onClick,
  children,
  ...rest
}: TrainingButtonProps) => {
  return (
    <Button
      style={style}
      onClick={onClick}
      className={`cursor-pointer text-center p-3 w-64 rounded-xl my-4 bg-button-color`}
      {...rest}
    >
      {children}
    </Button>
  );
};

export default TrainingButton;
