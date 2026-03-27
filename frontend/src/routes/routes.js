import Vocabulary from "pages/protected/Vocabulary/Vocabulary";
import ProfileSettings from "pages/protected/User/ProfileSettings";
import Statistics from "../pages/protected/User/Statistics";

const routes = [
  {
    path: "/vocabularies/*",
    component: Vocabulary,
  },
  {
    path: "/statistics",
    component: Statistics,
  },
  {
    path: "/settings-profile",
    component: ProfileSettings,
  },
];

export const allSubRoutes = [
  "login",
  "register",
  "new-password",
  "vocabularies",
  "statistics",
  "settings-profile",
  "forgot-password",
  "auth",
  "contact-us",
  "privacy-policy",
];

export default routes;
