import { ChangeEvent, FormEvent, useState } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import InputText from "../../components/Input/InputText";
import { ButtonBack } from "../../components/Button/ButtonBack";
import ErrorAuthorization, { ErrorText } from "../../components/Typography/ErrorText";
import PasswordInput from "../../features/user/PasswordInput";
import UnauthorizedFormComponent from "../../features/user/UnauthorizedFormComponent";
import i18n from "../../i18nf/i18n";
import { checkAuthentication, logOut, setUser } from "../../store/user/userSlice";
import {
  setCurrentlySelectedVocabulary,
  setVocabularies,
} from "../../store/vocabulary/vocabularySlice";
import { authenticate } from "../../utils/userUtils";
import { fetchUserVocabularies } from "../../utils/vocabularyUtils";

import { selectAccessToken } from "store/user/userSelector";
import { getShortLanguageName } from "utils/globalUtils";
import { ReactComponent as PenguinCoffee } from "../../assets/penguins/normal_coffee.svg";
import FormButton from "../../components/Button/FormButton";
import LanguageSelect from "../../components/Footer/LanguageSelect";
import { VocabularyType } from "../../store/vocabulary/vocabularyTypes";
import GoogleAuthentication from "components/User/GoogleAuthentication";
import Divider from "components/Util/Divider";

export type LoginFormParams = {
  updateType: string;
  value: any;
};

export const GoToHome = () => {
  return (
    <ButtonBack
      backUrl="/"
      textKey="GoToHomePage"
      className="mb-0 lg:mb-3 bg-none shadow-none max-sm:ml-4"
      classNameText="hidden"
    />
  );
};

function Login() {
  const dispatch = useDispatch();
  const INITIAL_LOGIN_OBJ = {
    password: "",
    email: "",
  };

  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [showError, setShowError] = useState(false);
  const [loginObj, setLoginObj] = useState(INITIAL_LOGIN_OBJ);
  const accessToken = useSelector(selectAccessToken);
  const navigate = useNavigate();
  const { t } = useTranslation();

  const submitForm = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setErrorMessage("");

    if (loginObj.email.trim() === "") {
      setErrorMessage("Email is required!");
      setShowError(true);
      return;
    } else if (loginObj.password.trim() === "") {
      setErrorMessage("Password is required!");
      setShowError(true);
      return;
    }

    setLoading(true);
    let response;
    try {
      response = await authenticate(loginObj);

      if (!response || !response.data) {
        throw new Error();
      }
    } catch (error) {
      console.error(error);
      setErrorMessage(t("InvalidEmailOrPassword"));
      setShowError(true);
      setLoading(false);
      return;
    }

    const { tokens, streak, userDetails, settings } = response.data;

    if (accessToken) {
      dispatch(logOut());
    }

    dispatch(
      setUser({ tokenPair: tokens, userStreak: streak, userDetails, userSettings: settings }),
    );
    // @ts-ignore
    dispatch(checkAuthentication());

    let lang = getShortLanguageName(settings.interfaceLanguage);

    await i18n.changeLanguage(lang);

    const fetchedVoc = await fetchUserVocabularies();

    if (fetchedVoc.length === 0) {
      navigate(`/${i18n.language}/form/learning`);
    } else {
      dispatch(setVocabularies(fetchedVoc));

      if (!userDetails.lastPickedVocabularyId) {
        dispatch(setCurrentlySelectedVocabulary(fetchedVoc[0]));
      } else {
        const element = fetchedVoc.find(
          (v: VocabularyType) => v.id === userDetails.lastPickedVocabularyId,
        );
        if (element) dispatch(setCurrentlySelectedVocabulary(element));
        else dispatch(setCurrentlySelectedVocabulary(fetchedVoc[0]));
      }

      navigate(`/${i18n.language}/vocabularies`);
    }
    setLoading(false);
  };

  const updateFormValue = ({ updateType, value }: LoginFormParams) => {
    setErrorMessage("");
    setLoginObj({ ...loginObj, [updateType]: value });
  };

  return (
    <>
      <ErrorAuthorization open={showError} setOpen={setShowError}>
        {errorMessage}
      </ErrorAuthorization>
      <UnauthorizedFormComponent>
        <div className="bg-white rounded-xl relative">
          <div className="flex absolute  py-2 lg:py-4 pr-[3px] justify-between w-full">
            <span className="px-0 lg:px-2 flex items-center justify-center cursor-pointer">
              <GoToHome />
            </span>

            <LanguageSelect />
          </div>
          <form
            onSubmit={submitForm}
            className="flex flex-col justify-between w-[90%] lg:w-[75%] mx-auto h-[70%]"
          >
            <PenguinCoffee
              className="w-[100%] sm:w-[80%] pt-8 pb-6 lg:pt-1 h-[60%] object-contain mx-auto"
            />
            <div className="mb-2 gap-1 flex flex-col">
              <div className=" flex flex-col gap-2">
                <InputText
                  autoComplete="username"
                  value={loginObj.email}
                  type="email"
                  placeholder={t("Email")}
                  containerStyle=""
                  updateFormValue={(e) =>
                    updateFormValue({
                      updateType: "email",
                      value: e.target.value,
                    })
                  }
                />
                <PasswordInput
                  onChange={(e: ChangeEvent<HTMLInputElement>) =>
                    updateFormValue({
                      updateType: "password",
                      value: e.target.value,
                    })
                  }
                />
              </div>

              <Link to={`/${i18n.language}/forgot-password`} className="text-right">
                <span
                  className="text-sm text-black inline-block text-opacity-60
                                        hover:text-button-color hover:underline hover:cursor-pointer"
                >
                  {t("ForgotPassword")}
                </span>
              </Link>
            </div>
            <FormButton loading={loading} value={t("Login")} />

            <Divider text="Or"/>
            <GoogleAuthentication
              INITIAL_AUTHORIZATION_OBJ={INITIAL_LOGIN_OBJ}
              setAuthorizationObj={setLoginObj}
            />

            <ErrorText styleClass="mt-8">{errorMessage}</ErrorText>

            <div
              className="text-center font-normal text-sm lg:text-lg mb-4 flex justify-center text-black
                            text-opacity-60 items-center gap-1"
            >
              {t("DontHaveAccount")}

              <Link to={`/${i18n.language}/register`}>
                <span
                  className="inline-block font-normal text-black text-opacity-60
                                hover:text-button-color hover:underline hover:cursor-pointer"
                >
                  {t("Register")}
                </span>
              </Link>
            </div>
          </form>
        </div>
      </UnauthorizedFormComponent>
    </>
  );
}

export default Login;
