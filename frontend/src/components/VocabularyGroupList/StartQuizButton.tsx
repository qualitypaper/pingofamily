import { ReactComponent as QuizIcon } from "assets/icons/quiz.svg";
import { Link } from "react-router-dom";
import i18n from "../../i18nf/i18n";
import Button from "../Button/Button";
import { FC } from "react";

interface ButtonStartQuizProps {
  title: string;
  vocabularyGroupId?: number;
  className?: string;
}

const StartQuizButton: FC<ButtonStartQuizProps> = ({ title, vocabularyGroupId, className }) => {
  return (
    <Link
      to={`/${i18n.language}/training${vocabularyGroupId ? `?vocabularyGroupId=${vocabularyGroupId}` : ""}`}
      className="h-10 "
    >
      <Button
        classname={`${className} !justify-center w-full text-white bg-blue-500 whitespace-nowrap hover:bg-blue-600`}
        titleClassname="text-white font-bold text-base"
        title={title}
        icon={<QuizIcon fill="#fff" />}
      />
    </Link>
  );
};

export default StartQuizButton;
