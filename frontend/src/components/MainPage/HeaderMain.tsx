import { Link } from "react-router-dom";
import { LogoSidebar } from "../../containers/Header/LeftSidebar";
import { useTranslation } from "react-i18next";
import { TFunction } from "i18next";
import { ReactNode } from "react";
import { WEBSITE_NAME } from "../../constant";
import { useSelector } from "react-redux";
import { selectIsAuthenticated } from "store/user/userSelector";

export const HeaderMain = () => {
  const { t } = useTranslation();
  const isAuthenticated = useSelector(selectIsAuthenticated);

  return (
    <div className="w-full m-auto">
      <header className="w-full max-w-[64rem] xl:px-0  m-auto py-[18.3px] flex justify-between items-center px-5">
        <div>
          {isAuthenticated ? (
            <LogoSidebar
              className="font-bold text-2xl text-color-big-text "
              classNameTitle="hidden md:block"
              title={WEBSITE_NAME}
            />
          ) : (
            <LogoSidebar
              className="font-bold text-2xl text-color-big-text"
              classNameTitle="hidden md:block"
              title={WEBSITE_NAME}
            />
          )}
        </div>
        <div className="flex gap-2">
          {isAuthenticated ? (
            <>
              <HeaderButton
                t={t}
                textKey="GoToAccount"
                toUrl="vocabularies"
                className="btn-login"
              />
            </>
          ) : (
            <>
              <HeaderButton t={t} textKey="Login" toUrl="login" className="btn-login" />
              <HeaderButton t={t} textKey="SignUp" toUrl="register" className="btn-sign-up">
                <div className="arrow-wrapper">
                  <div className="arrow"></div>
                </div>
              </HeaderButton>
            </>
          )}
        </div>
      </header>
      <hr className="line" />
    </div>
  );
};

type HeaderButtonProps = {
  t: TFunction<"translation", null>;
  textKey: string;
  toUrl: string;
  className?: string;
  children?: ReactNode;
  onClick?: () => void;
};

export function HeaderButton({
  t,
  textKey,
  toUrl,
  className,
  children,
}: Readonly<HeaderButtonProps>) {
  return (
    <Link to={toUrl}>
      <button className={`${className} rounded-md text-xs md:text-base p-5`}>
        {t(textKey)}
        {children}
      </button>
    </Link>
  );
}
