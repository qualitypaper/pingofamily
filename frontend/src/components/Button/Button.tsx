import { ReactNode } from "react";

export type ButtonProps = {
  icon?: ReactNode;
  title?: string;
  classname?: string;
  onClick?: (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => void;
  disabled?: boolean;
  children?: ReactNode;
  type?: "button" | "submit" | "reset";
  titleClassname?: string;
};

const Button = ({
  icon,
  title,
  classname,
  onClick,
  disabled,
  children,
  type,
  titleClassname,
}: ButtonProps) => {
  return (
    <button
      type={type ?? "button"}
      disabled={disabled}
      onClick={onClick}
      className={`${classname} flex justify-evenly items-center gap-1 border font-bold border-blue-200
                px-4 py-2 rounded-md hover:border-button-color transition duration-300`}
    >
      {icon}

      <span className={`font-bold text-base ${titleClassname}`}>{title}</span>
      {children}
    </button>
  );
};

export default Button;
