import { Button } from "@chakra-ui/react";
import { ReactComponent as ArrowBackIcon } from "assets/icons/arrow-back.svg";
import { MouseEvent } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";

type ButtonBackProps = {
  onClick?: (e: MouseEvent<HTMLButtonElement>) => void;
  backUrl?: string;
  textKey?: string;
  className?: string;
  classNameText?: string;
};

export const ButtonBack = ({
  onClick: optionalOnClick,
  backUrl,
  textKey,
  className,
  classNameText,
}: ButtonBackProps) => {
  const { t } = useTranslation();
  const navigate = useNavigate();

  return (
    <div
      className={`${className ?? "lg:pt-6"} pt-0 flex items-center justify-start gap-2 rounded-lg cursor-pointer`}
    >
      <Button
        onClick={(e: MouseEvent<HTMLButtonElement>) => {
          if (backUrl) {
            navigate(backUrl);
          } else {
            navigate(-1);
            optionalOnClick && optionalOnClick(e);
          }
        }}
        className="flex items-center gap-1"
      >
        <ArrowBackIcon />
        <p className={`font-semibold ${classNameText}`}>{t(textKey ?? "GoBack")}</p>
      </Button>
    </div>
  );
};
