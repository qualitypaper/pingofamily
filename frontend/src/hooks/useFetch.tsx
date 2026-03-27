import { API, RequestMethod } from "app/init";
import { useEffect, useState } from "react";

const useFetch = (
  endpoint: string,
  method: RequestMethod,
  params?: object | [],
  cancelSimiliar: boolean = true,
  config = {},
  auth: boolean = true
) => {
  const [response, setResponse] = useState({});
  const [loading, setLoading] = useState<boolean>(false);

  useEffect(() => {
    async function fetch() {
      setLoading(true);
      try {
        let res;
        switch (method) {
          case "GET":
            res = await API.get(endpoint, params, config, null, auth);
            break;
          case "POST":
            res = await API.post(endpoint, params ?? {}, config, undefined, auth);
            break;
          case "PUT":
            res = await API.put(endpoint, params ?? {}, config, cancelSimiliar, undefined, auth);
            break;
          case "DELETE":
            res = await API.delete(endpoint, params, config, null, auth);
            break;
          default:
            throw new Error("Unsupported request method");
        }
        debugger

        setResponse(res);
      } catch (e) {
        // @ts-ignore
        setResponse(e)
      }
      finally {
        setLoading(false);
      }
    }

    fetch().then();
  }, [cancelSimiliar, endpoint, method, params]);

  return [response, loading];
};

export default useFetch;
