import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { ReactComponent as NormalSkatesIcon } from "../../assets/penguins/normal_strongman.svg";
import FormButton from "../../components/Button/FormButton";
import LanguageSelect from "../../components/Footer/LanguageSelect";
import InputText from "../../components/Input/InputText";
import ErrorAuthorization from "../../components/Typography/ErrorText";
import PasswordInput from "../../features/user/PasswordInput";
import UnauthorizedFormComponent from "../../features/user/UnauthorizedFormComponent";
import i18n from "../../i18nf/i18n";
import {
  checkAuthentication,
  logOut,
  setUser
} from "../../store/user/userSlice";
import { clearVocabularySlice } from "../../store/vocabulary/vocabularySlice";
import { register } from "../../utils/userUtils";
import { GoToHome } from "./Login";
import GoogleAuthentication from "components/User/GoogleAuthentication";
import Divider from "components/Util/Divider";

function Register() {
  const dispatch = useDispatch();

  const INITIAL_REGISTER_OBJ = {
    fullName: "",
    password: "",
    email: "",
  };

  const [loading, setLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [open, setOpen] = useState(false);
  const [registerObj, setRegisterObj] = useState(INITIAL_REGISTER_OBJ);
  const { t } = useTranslation();
  const navigate = useNavigate();

  const checkPasswordValidity = (password) => {
    // const upperCaseRegex = /[A-Z]/;
    // const numberRegex = /[0-9]/;
    // const specialCharacterRegex = /[ `!@#$%^&*()_+-={}'"\\|>?~]/;

    // return !(!upperCaseRegex.test(password) || !specialCharacterRegex.test(password) || !numberRegex.test(password) || !(password.length > 6));
    return password.length >= 4;
  };

  const checkEmail = (email) => {
    return String(email)
      .toLowerCase()
      .match(
        /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|.(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
      );
  };

  const submitForm = async (e) => {
    if (e) {
      e.preventDefault();
    }

    setErrorMessage("");
    dispatch(clearVocabularySlice());

    if (registerObj.fullName.trim() === "") {
      setErrorMessage(t("NameRequired"));
      setOpen(true);
      return;
    } else if (registerObj.email.trim() === "") {
      setErrorMessage(t("EmailRequired"));
      setOpen(true);
      return;
    } else if (registerObj.password.trim() === "") {
      setErrorMessage(t("PasswordRequired"));
      setOpen(true);
      return;
    } else if (!checkEmail(registerObj.email.trim())) {
      setErrorMessage("Email is not valid")
      setOpen(true)
      return;
    } else if (!checkPasswordValidity(registerObj.password.trim())) {
      setErrorMessage(t("PasswordInvalid"));
      setOpen(true);
      return;
    }

    try {
      setLoading(true);
      const res = await register(registerObj);

      if (!res) {
        setErrorMessage(t("RegisterFailed"));
        setOpen(true);
        setLoading(false);
        return;
      } else if (!res.tokens) {
        setErrorMessage(t("RegisterFailed"));
        setOpen(true);
        setLoading(false);
        return;
      }

      dispatch(logOut());
      dispatch(
        setUser({
          userStreak: res.streak,
          tokenPair: res.tokens,
          userSettings: res.settings,
          userDetails: res.userDetails,
        }),
      );
      dispatch(checkAuthentication());
    } catch (error) {
      console.error("Registration error:", {
        message: error.message,
        stack: error.stack,
        name: error.name,
      });
      setErrorMessage(t("RegisterFailed"));
      setOpen(true);
      throw new Error("The registration has failed");
    } finally {
      setLoading(false);
    }

    navigate(`/${i18n.language}/form/learning`);
  };

  const handleKeyPress = (e) => {
    if (e.key === "Enter") {
      e.preventDefault();
      submitForm();
    }
  };

  const updateFormValue = ({ updateType, value }) => {
    setErrorMessage("");
    setRegisterObj({ ...registerObj, [updateType]: value });
  };

  return (
    <UnauthorizedFormComponent>
      <div className="bg-white rounded-xl relative">
        <div className="flex absolute  py-2 lg:py-4 pr-[3px] justify-between w-full">
          <span className="px-0 lg:px-2 flex items-center justify-center cursor-pointer">
            <GoToHome />
          </span>
          <LanguageSelect displayLabel={false} />
        </div>
        <form
          onSubmit={submitForm}
          onKeyDown={handleKeyPress}
          className="flex flex-col justify-between w-[90%] lg:w-[80%] mx-auto h-[70%]"
        >
          <NormalSkatesIcon className="w-[80%] sm:w-[80%] py-6 lg:py-0 h-[60%] object-contain mx-auto" />
          <div className="flex flex-col gap-3 w-full pb-2">
            <InputText
              type="text"
              defaultValue={registerObj.fullName}
              containerStyle="mt-4"
              value={registerObj.fullName}
              placeholder={t("FullName")}
              updateFormValue={(e) =>
                updateFormValue({
                  updateType: "fullName",
                  value: e.target.value,
                })
              }
            />

            <InputText
              value={registerObj.email}
              defaultValue={registerObj.email}
              type="email"
              containerStyle="mt-0"
              placeholder={t("Email")}
              updateFormValue={(e) =>
                updateFormValue({
                  updateType: "email",
                  value: e.target.value,
                })
              }
            />

            <PasswordInput
              onChange={(e) =>
                updateFormValue({
                  updateType: "password",
                  value: e.target.value,
                })
              }
            />
          </div>

          <FormButton value={t("Register")} loading={loading} />

          <Divider text="Or" />
          <GoogleAuthentication
            setAuthorizationObj={setRegisterObj}
            INITIAL_AUTHORIZATION_OBJ={INITIAL_REGISTER_OBJ}
          />

          <ErrorAuthorization open={open} setOpen={setOpen}>
            {errorMessage}
          </ErrorAuthorization>

          <div
            className="text-center font-normal text-sm lg:text-lg my-4 flex justify-center
            text-black text-opacity-60 items-center gap-1"
          >
            {t("AlreadyHaveAccount")}

            <Link to={`/${i18n.language}/login`}>
              <span
                className="inline-block font-normal text-black text-opacity-60
                hover:text-primary hover:underline hover:cursor-pointer"
              >
                {t("Login")}
              </span>
            </Link>
          </div>
        </form>
      </div>
    </UnauthorizedFormComponent>
  );
}

export default Register;
