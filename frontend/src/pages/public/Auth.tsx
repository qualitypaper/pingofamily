import { useCallback, useEffect } from "react";
import { useDispatch } from "react-redux";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { getUserDetails } from "utils/userUtils";
import { LoadingPage } from "../../components/Loading/Loading";
import i18n from "../../i18nf/i18n";
import { checkAuthentication, setTokenPair } from "../../store/user/userSlice";
import {
  setCurrentlySelectedVocabulary,
  setVocabularies,
} from "../../store/vocabulary/vocabularySlice";
import { VocabularyType } from "../../store/vocabulary/vocabularyTypes";
import { fetchUserVocabularies } from "../../utils/vocabularyUtils";

const Auth = () => {
  const [searchParams] = useSearchParams();
  const { lastPickedVocabularyId: lastPickedVocabularyIdParam } = useParams();
  const accessToken = searchParams.get("accessToken");
  const refreshToken = searchParams.get("refreshToken");
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const fetchVocabularies = useCallback(async (lastPickedVocabularyId?: number) => {
    const fetchedVoc = await fetchUserVocabularies();

    if (fetchedVoc.length === 0) {
      navigate(`/${i18n.language}/form/learning`);
      return false;
    } else {
      dispatch(setVocabularies(fetchedVoc));
      if (!lastPickedVocabularyId) {
        dispatch(setCurrentlySelectedVocabulary(fetchedVoc[0]));
      } else {
        const element = fetchedVoc.find((v: VocabularyType) => v.id === lastPickedVocabularyId);
        if (element) dispatch(setCurrentlySelectedVocabulary(element));
        else dispatch(setCurrentlySelectedVocabulary(fetchedVoc[0]));
      }
      navigate(`/${i18n.language}/vocabularies`);
      return true;
    }
  },
    [dispatch, navigate],
  );

  useEffect(() => {
    if (!accessToken || !refreshToken) return;
    dispatch(setTokenPair({ accessToken, refreshToken }));
    // @ts-ignore
    dispatch(checkAuthentication())

    const fetchUserDetails = async (): Promise<number | undefined> => {
      if (!accessToken) return;

      try {
        const details = await getUserDetails();

        return (
          details?.userDetails?.lastPickedVocabularyId ?? +(lastPickedVocabularyIdParam ?? "0")
        );
      } catch (error) {
        console.error("Error fetching user details:", error);
      }
    };

    fetchUserDetails().then((lastPickedVocabularyId) => {
      fetchVocabularies(lastPickedVocabularyId).then();
    });
  }, [
    dispatch,
    navigate,
    accessToken,
    refreshToken,
    lastPickedVocabularyIdParam,
    fetchVocabularies,
  ]);

  return <LoadingPage />;
};

export default Auth;
