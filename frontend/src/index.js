import React from "react";
import { ChakraProvider, defaultSystem } from "@chakra-ui/react";
import ReactDOM from "react-dom/client";
import { I18nextProvider } from "react-i18next";
import { Provider } from "react-redux";
import { BrowserRouter } from "react-router-dom";
import { PersistGate } from "redux-persist/integration/react";
import App from "./App";
import { persistor, store } from "./app/store";
import { LoadingPage } from "./components/Loading/Loading";
import i18n from "./i18nf/i18n";
import "./index.css";
import reportWebVitals from "./reportWebVitals";

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <BrowserRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <ChakraProvider value={defaultSystem}>
        <Provider store={store}>
          <I18nextProvider i18n={i18n} defaultNS={"translation"}>
            <PersistGate loading={<LoadingPage />} persistor={persistor}>
              <App />
            </PersistGate>
          </I18nextProvider>
        </Provider>
      </ChakraProvider>
    </BrowserRouter>
  </React.StrictMode>,
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
