
import { LoadingPage } from "components/Loading/Loading";
import { BASE_URL } from "constant";
import i18n from "i18nf/i18n";
import { useEffect } from "react";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { selectTokenPair } from "store/user/userSelector";
import { setTokenPair } from "store/user/userSlice";

const RefreshToken = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const tokenPair = useSelector(selectTokenPair);
  
  useEffect(() => {
    const getNewToken = async () => {
      if (!tokenPair.refreshToken) {
        navigate(`/${i18n.language}/login`);
        return;
      }
      let res;
      try {

        res = await fetch(`${BASE_URL}/auth/refresh`, {
          method: "PUT",
          body: JSON.stringify({ refreshToken: tokenPair.refreshToken }),
          headers: {
            "Content-Type": "application/json",
          },
        });
        if (!res || !res.ok) {
          navigate(`/${i18n.language}/login`);
          return;
        }
        const data = await res.json();
        dispatch(setTokenPair(data));
      } catch (e) {
        navigate(`/${i18n.language}/login`)
        return;
      }
    }
    getNewToken()
      .catch(e => {
        navigate(`/${i18n.language}/login`);
        return;
      });
  // eslint-disable-next-line 
  }, []);

  return <LoadingPage />;
};

export default RefreshToken;
