import { ButtonHTMLAttributes, ReactNode } from "react";
import LoadingWave from "../Loading/LoadingWave";

type FormButtonProps = {
  value?: string;
  children?: ReactNode;
  loading?: boolean;
} & ButtonHTMLAttributes<HTMLButtonElement>;

function FormButton({ value, children, loading = false, ...rest }: FormButtonProps) {
  return (
    <button
      type={rest.type ?? "submit"}
      className={
        "h-12 p-2 text-md lg:text-lg font-bold text-white w-[50%] " +
        "bg-button-color hover:bg-[#3C72FF] " +
        "mt-2 rounded-xl mx-auto flex justify-center items-center" +
        (loading && " loading-active")
      }
      {...rest}
    >
      {loading ? <LoadingWave /> : (value ?? children ?? <></>)}
    </button>
  );
}

export default FormButton;
