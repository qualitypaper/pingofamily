import axios from "axios";

const checkAuth = (TOKEN: string, lang: string) => {
  const PUBLIC_ROUTES = [
    "login",
    "forgot-password",
    "register",
    "contact-us",
    "auth",
  ];

  const isPublicPage =
    PUBLIC_ROUTES.some((r) => window.location.href.includes(r)) ||
    window.location.href === lang;

  if (!TOKEN && !isPublicPage) {
    window.location.href = `/${lang}/login`;
  } else {
    axios.defaults.headers.common["Authorization"] = `Bearer ${TOKEN}`;

    return TOKEN;
  }
};

export default checkAuth;
