import axios, {
  AxiosInstance,
  AxiosRequestConfig,
  AxiosResponse,
  CanceledError,
  CancelTokenSource,
} from "axios";
import i18n from "i18nf/i18n";
import { BASE_URL } from "constant";
import { getAuthToken } from "./localStorageService";
import { store } from "./store";
import { preconnect, prefetchDNS } from "react-dom";

let tokenRedux = store.getState().user?.tokenPair.accessToken;
let token = tokenRedux ?? getAuthToken();
store.subscribe(() => {
  token = store.getState()?.user?.tokenPair?.accessToken;
});

let CANCEL_TOKEN_SOURCE = axios.CancelToken.source();

export const axiosInstance: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  cancelToken: CANCEL_TOKEN_SOURCE.token,
});

axiosInstance.interceptors.response.use(
  (response) => {
    // @ts-ignore
    if (Number.parseInt(response.status / 100) === 5) {
      throw new Error("Server-Side error, code: " + response.status);
    }
    return response;
  },
  (error) => {
    if (error?.response?.status === 401) {
      console.error(error);
      const href = window.location.href;
      if (href.includes("login") || href.includes("register")) {
        return Promise.reject(error);
      }
      window.location.href = `/${i18n.language}/refresh-token`;
      return;
    } else if (error instanceof CanceledError) {
      console.error("Caught error without throwing: ", error);
      return;
    } else {
      return Promise.reject(error);
    }
  },
);

function getDefaultHeaders(headers: {} = {}) {
  return token
    ? {
      Authorization: `Bearer ${token}`,
      ...headers,
    }
    : { ...headers };
}

export type RequestMethod = "GET" | "POST" | "DELETE" | "PUT";

const currentRequests = new Map<string, CancelTokenSource>();

export const API = {
  stopRequestsIfExists(endpoint: string) {
    if (currentRequests.has(endpoint)) {
      currentRequests.get(endpoint)?.cancel();
      currentRequests.delete(endpoint);
    }
  },
  get<DataResponseType = any>(
    endpoint: string,
    params: object = {},
    config?: AxiosRequestConfig,
    cancelToken: CancelTokenSource | null = null,
    auth: boolean = true
  ): Promise<AxiosResponse<DataResponseType>> {
    this.stopRequestsIfExists(endpoint);

    const cToken = cancelToken ? cancelToken : axios.CancelToken.source();

    currentRequests.set(endpoint, cToken);
    const req = axiosInstance
      .get<DataResponseType>(endpoint, {
        ...config,
        params: params,
        headers: config?.headers ?? auth ? getDefaultHeaders(config?.headers) : {},
        cancelToken: cToken.token,
      })
      .finally(() => {
        currentRequests.delete(endpoint);
      });

    return req;
  },
  post<DataResponseType = any>(
    endpoint: string,
    data: {} | [],
    config: AxiosRequestConfig = {},
    cancelToken: CancelTokenSource = CANCEL_TOKEN_SOURCE,
    auth: boolean = true
  ): Promise<AxiosResponse<DataResponseType>> {
    // this.stopRequestsIfExists(endpoint);
    currentRequests.set(endpoint, cancelToken);

    const req = axiosInstance
      .post<DataResponseType>(endpoint, data, {
        ...config,
        headers: config?.headers ?? auth ? getDefaultHeaders(config?.headers) : {},
        cancelToken: cancelToken.token,
      })
      .finally(() => {
        currentRequests.delete(endpoint);
      });

    return req;
  },
  put<DataResponseType = any>(
    endpoint: string,
    data: {} | [],
    config: AxiosRequestConfig = {},
    cancelSimiliarRequests = true,
    cancelToken: CancelTokenSource = CANCEL_TOKEN_SOURCE,
    auth: boolean = true
  ): Promise<AxiosResponse<DataResponseType>> {
    if (cancelSimiliarRequests) this.stopRequestsIfExists(endpoint);

    currentRequests.set(endpoint, cancelToken);
    const req = axiosInstance
      .put<DataResponseType>(endpoint, data, {
        ...config,
        headers: config?.headers ?? auth ? getDefaultHeaders(config?.headers) : {},
        cancelToken: cancelToken.token,
      })
      .finally(() => {
        currentRequests.delete(endpoint);
      });

    return req;
  },

  delete<DataResponseType = any>(
    endpoint: string,
    params: object = {},
    config: AxiosRequestConfig = {},
    cancelToken: CancelTokenSource | null = null,
    auth: boolean = true
  ): Promise<AxiosResponse<DataResponseType>> {
    this.stopRequestsIfExists(endpoint);

    const cToken = cancelToken ? cancelToken : axios.CancelToken.source();

    const req = axiosInstance
      .delete<DataResponseType>(endpoint, {
        ...config,
        params: params,
        headers: config?.headers ?? auth ? getDefaultHeaders(config?.headers) : {},
        cancelToken: cToken.token,
      })
      .then((e) => {
        currentRequests.delete(endpoint);
        return e;
      });

    currentRequests.set(endpoint, cToken);
    return req;
  },

  finishPendingRequestsLike(regex: string) {
    currentRequests.forEach((_, key) => {
      if (key.match(regex)) {
        this.stopRequestsIfExists(key);
      }
    });
  },
};

const initializeApp = () => {
  axios.defaults.baseURL = BASE_URL;
  // preload needed images for login, register forms
  preconnect('https://images.unsplash.com');
  prefetchDNS('https://images.unsplash.com');

  preconnect('https://image.shutterstock.com');
  prefetchDNS('https://image.shutterstock.com');

  preconnect("https://fluentfusion-static.s3.eu-central-1.amazonaws.com")
  prefetchDNS("https://fluentfusion-static.s3.eu-central-1.amazonaws.com")

  preconnect(BASE_URL)
  prefetchDNS(BASE_URL)

  if (!process.env.NODE_ENV || process.env.NODE_ENV === "development") {
    // dev code
  } else {
    // prd code
  }
};

export default initializeApp;
