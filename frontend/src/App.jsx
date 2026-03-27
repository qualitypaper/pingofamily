import { lazy, Suspense, useEffect, useLayoutEffect } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch, useSelector } from "react-redux";
import { Navigate, Route, Routes, useLocation, useNavigate } from "react-router-dom";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { checkAuthentication } from "store/user/userSlice";
import initializeApp from "./app/init";
import { LoadingPage } from "./components/Loading/Loading";
import { removeNotificationMessage } from "./store/headerSlice";
import { selectTokenPair } from "./store/user/userSelector";
import { selectCurrentVocabularies } from "./store/vocabulary/vocabularySelector";
import { isSubPath, isValidLang } from "./utils/globalUtils";

const Layout = lazy(() => import("containers/Layout"));
const ForgotPassword = lazy(() => import("features/user/ForgotPassword"));
const Login = lazy(() => import("pages/public/Login"));
const RefreshToken = lazy(() => import("pages/public/RefreshToken"));
const Register = lazy(() => import("pages/public/Register"));
const MainPageContent = lazy(() => import("./components/MainPage/MainPageContent"));
const Training = lazy(() => import("./components/Training/Training"));
const NewPassword = lazy(() => import("./features/user/NewPassword"));
const PrivacyPolicy = lazy(() => import("./features/user/privacy/PrivacyPolicy"));
const CookiesComponent = lazy(() => import("./pages/protected/CookiesComponent"));
const FormLearning = lazy(() => import("./pages/protected/FormLearning"));
const FormNative = lazy(() => import("./pages/protected/FormNative"));
const Auth = lazy(() => import("./pages/public/Auth"));
const ContactUsPage = lazy(() => import("./pages/public/ContactUs"));
const MainPage = lazy(() => import("./pages/public/MainPage"));

initializeApp();

function App() {
  const dispatch = useDispatch();
  const tokenPair = useSelector(selectTokenPair);
  const vocabularies = useSelector(selectCurrentVocabularies);
  const { notificationMessage, notificationStatus } = useSelector((state) => state.header);
  const navigate = useNavigate();
  const location = useLocation();
  const { i18n } = useTranslation();

  useEffect(() => {
    dispatch(checkAuthentication());
  }, [dispatch]);

  // INITIATE_MESSAGE
  useEffect(() => {
    if (notificationStatus?.length === 0 || notificationMessage?.length === 0) {
      return;
    }

    if (notificationStatus === "info") toast.info(notificationMessage, "Info");
    else if (notificationStatus === "success") toast.success(notificationMessage, "Success");
    else if (notificationStatus === "error") toast.error(notificationMessage, "Error");

    dispatch(removeNotificationMessage());
  }, [notificationMessage, dispatch, notificationStatus]);

  useLayoutEffect(() => {
    console.info("URL changed", location);
    const pathname = location.pathname.split("/");

    if (isValidLang(pathname[1])) {
      if (pathname[1] !== i18n.language) {
        i18n.changeLanguage(pathname[1]).then();
        navigate(location.pathname, { replace: true })
      }
    } else if (isSubPath(pathname[1])) {
      const path = pathname.slice(1).join("/");

      navigate(`/en/${path}`, { replace: true });
    } else {
      const path = pathname.slice(2, pathname.length).join("/");
      const lang = i18n.options.lng ?? "en";
      navigate(`/${lang}/${path}`, { replace: true });
    }
  }, [i18n, i18n.options.lng, location, navigate]);

  return (
    <>
      <ToastContainer />
      <Suspense fallback={<LoadingPage size={36} />}>
        <Routes>
          <Route path=":lang" element={<MainPage />}>
            <Route index element={<MainPageContent />} />
            <Route path="contact-us" element={<ContactUsPage />} />
            <Route path="privacy-policy" element={<PrivacyPolicy />} />
          </Route>

          <Route path=":lang/training">
            <Route index element={<Training />} />
            <Route path="*" element={<Navigate to={`/${i18n.language}/training`} />} />
          </Route>

          <Route path=":lang">
            <Route path="login" element={<Login />} />
            <Route path="forgot-password" element={<ForgotPassword />} />
            <Route path="register" element={<Register />} />
            <Route path="new-password/:token" element={<NewPassword />} />
            <Route path="auth/:lastPickedVocabularyId" element={<Auth />} />
            <Route path="refresh-token" element={<RefreshToken />} />

            <Route path="*" element={<Layout />} />

            <Route path="form">
              <Route
                path="learning"
                element={<FormLearning token={tokenPair} vocabularies={vocabularies} />}
              />
              <Route path="native" element={<FormNative />} />
            </Route>
          </Route>
        </Routes>
        <CookiesComponent />
      </Suspense>
    </>
  );
}

export default App;
