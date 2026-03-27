import React from "react";
import { Link } from "react-router-dom";
import { ReactComponent as GoogleImage } from "../../assets/icons/google.svg";
import { BASE_URL } from "constant";
import { useTranslation } from "react-i18next";

const GoogleAuthentication = ({
  setAuthorizationObj,
  INITIAL_AUTHORIZATION_OBJ,
}: {
  setAuthorizationObj: any;
  INITIAL_AUTHORIZATION_OBJ: any;
}) => {
  const { t } = useTranslation();

  return (
    <div className="flex flex-col items-center justify-center w-full">
      <Link
        onClick={() => setAuthorizationObj(INITIAL_AUTHORIZATION_OBJ)}
        to={`${BASE_URL}/login/oauth2/authorization/google`}
        className="social-btn !rounded-xl"
      >
        <button className="w-full flex items-center justify-center" type="button">
          <GoogleImage className="absolute left-3" />
          <p className="font-bold text-black text-opacity-60">{t("ContinueGoogle")}</p>
        </button>
      </Link>
    </div>
  );
};

export default GoogleAuthentication;