import { LoadingPage } from "components/Loading/Loading";
import { useCallback, useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { Route, Routes, useNavigate, useParams } from "react-router-dom";
import MoreSuggestedGroupsComponent from "../../../components/VocabularyGroupList/MoreSuggestedGroups";
import { SOCKET_URL } from "../../../constant";
import { ImportWords } from "../../../features/vocabulary/ImportWords";
import WordDetails from "../../../features/vocabulary/WordList/WordDetails";
import WordList from "../../../features/vocabulary/WordList/WordList";
import i18n from "../../../i18nf/i18n";
import { showNotification } from "../../../store/headerSlice";
import { Language } from "../../../store/language/languageTypes";
import {
  selectIsAuthenticated,
  selectRoomCode,
  selectTokenPair,
  selectUserDetails,
} from "../../../store/user/userSelector";
import { selectCurrentlySelected } from "../../../store/vocabulary/vocabularySelector";
import {
  changeWordParameters,
  setCurrentlySelectedVocabulary,
  setSuggestedVocabularyGroups,
  setVocabularies,
  updateTempIdWord,
} from "../../../store/vocabulary/vocabularySlice";
import { VocabularyType } from "../../../store/vocabulary/vocabularyTypes";
import { getFullLanguageNameForRequest } from "../../../utils/globalUtils";
import { getUserDetails } from "../../../utils/userUtils";
import {
  fetchSuggestedVocabularyGroups,
  fetchUserVocabularies,
  getMultiLingualSuggestedGroups,
} from "../../../utils/vocabularyUtils";
import VocabularyGroupList from "./VocabularyGroupList";

function Vocabulary() {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [loading, setLoading] = useState<number>(0);
  const roomCode = useSelector(selectRoomCode);
  const tokenPair = useSelector(selectTokenPair);
  const selected = useSelector(selectCurrentlySelected);
  const userDetails = useSelector(selectUserDetails);
  const isAuthenticated = useSelector(selectIsAuthenticated);
  const urlParams = useParams();
  const { vocabularyGroupId, wordId } = getUrlParams();

  function getUrlParams(): { vocabularyGroupId?: number; wordId?: number } {
    const ids = urlParams["*"] ?? "";

    const params = ids.split("/");
    if (params.length === 1) return { vocabularyGroupId: +params[0] };
    else if (params.length === 2) {
      return {
        vocabularyGroupId: +params[0],
        wordId: +params[1],
      };
    } else return {};
  }

  // WEB_SOCKET
  useEffect(() => {
    if (!roomCode || !isAuthenticated) {
      console.error("Could connect to websocket server: Insufficient data", {
        roomCode,
        isAuthenticated,
      });
      return;
    }

    const socket = new WebSocket(`${SOCKET_URL}?room=${roomCode}&token=${tokenPair?.accessToken}`);

    const handleChangeParametersRequest = (data: object | string) => {
      dispatch(
        showNotification({
          status: "success",
          message: "Your word parameters have been successfully changed.",
        }),
      );
      if (typeof data === "string") {
        data = JSON.parse(data);
      }
      dispatch(changeWordParameters(data));
    };

    const handleAddWordRequest = (data: object | string) => {
      dispatch(
        showNotification({
          status: "success",
          message: "Your word has been successfully added.",
        }),
      );

      if (typeof data === "string") {
        data = JSON.parse(data);
      }
      dispatch(updateTempIdWord(data));
    };

    socket.addEventListener("message", (e) => {
      const { event, data } = JSON.parse(e.data);

      switch (event) {
        case "ADD_WORD":
          handleAddWordRequest(data);
          break;
        case "CHANGE_WORD_PARAMETERS":
          handleChangeParametersRequest(data);
          break;
        default:
          break;
      }
    });

    socket.addEventListener("close", (event) => {
      console.log("close event", event);
    });

    socket.addEventListener("error", (event) => {
      console.log("error event", event);
    });

    return () => socket.close();
  }, [dispatch, isAuthenticated, roomCode, tokenPair.accessToken]);

  const getSuggestedVocabularyGroups = useCallback(async (vocabulary: VocabularyType, setState: boolean = true) => {
    try {
      setLoading((l) => l + 1);
      const response = await fetchSuggestedVocabularyGroups(
        vocabulary.learningLanguage ?? "ENGLISH",
        vocabulary.nativeLanguage ?? "ENGLISH",
      );

      if (!response) return [];

      if (setState) {
        dispatch(setSuggestedVocabularyGroups(response.data));
      }
      return response.data;
    } finally {
      setLoading((l) => l - 1);
    }
  }, [dispatch]);

  const fetchVocabularies = useCallback(async (lastPickedVocabularyId: number | undefined) => {
    try {
      setLoading((l) => l + 1);
      const fetchedVoc = await fetchUserVocabularies();

      if (fetchedVoc === undefined) return;
      else if (fetchedVoc.length === 0) {
        navigate(`/${i18n.language}/form/learning`);
        return;
      } else {
        dispatch(setVocabularies(fetchedVoc));
        for (let i = 0; i < fetchedVoc.length; i++) {
          if (fetchedVoc[i].id === selected.id) {
            dispatch(setCurrentlySelectedVocabulary(fetchedVoc[i]));
            return fetchedVoc[i];
          }
        }
        if (!lastPickedVocabularyId) {
          dispatch(setCurrentlySelectedVocabulary(fetchedVoc[0]));
          return fetchedVoc[0];
        } else {
          const element = fetchedVoc.find((v: VocabularyType) => v.id === lastPickedVocabularyId);
          if (element) {
            dispatch(setCurrentlySelectedVocabulary(element));
            return element;
          } else {
            dispatch(setCurrentlySelectedVocabulary(fetchedVoc[0]));
            return fetchedVoc[0];
          }
        }
      }
    } finally {
      setLoading((l) => l - 1);
    }
  },
    // eslint-disable-next-line
    [dispatch, navigate],
  );

  useEffect(() => {
    if (!tokenPair || !isAuthenticated) return;
    fetchVocabularies(userDetails.lastPickedVocabularyId).then();
  }, [fetchVocabularies, isAuthenticated, tokenPair, userDetails.lastPickedVocabularyId]);

  useEffect(() => {
    if (!tokenPair || !isAuthenticated) return;

    try {
      setLoading((l) => l + 1);
      getUserDetails()
        .then()
        .catch((e) => console.error(e));
    } catch (error) {
      console.error("Error fetching user details:", error);
    } finally {
      setLoading((l) => l - 1);
    }
  }, [isAuthenticated, tokenPair]);


  const setMultipleSuggestedVocabularyGroups = useCallback(async (learningLanguage: Language) => {
    setLoading(l => l + 1);
    try {
      const groups = await getMultiLingualSuggestedGroups(learningLanguage);
      dispatch(setSuggestedVocabularyGroups(groups))
    } finally {
      setLoading(l => l - 1);
    }
  },
    [dispatch],
  );

  useEffect(() => {
    if (vocabularyGroupId || wordId) return;

    if (isAuthenticated && selected && selected.nativeLanguage !== selected.learningLanguage) {
      getSuggestedVocabularyGroups(selected, true).then();
    } else {
      const tempLang = getFullLanguageNameForRequest(i18n.language ?? "en").toUpperCase();
      setMultipleSuggestedVocabularyGroups(tempLang as Language).then();
    }
  }, [
    dispatch,
    getSuggestedVocabularyGroups,
    isAuthenticated,
    selected,
    setMultipleSuggestedVocabularyGroups,
    tokenPair,
    vocabularyGroupId,
    wordId,
  ]);

  return (
    <Routes>
      <Route index element={loading ? <LoadingPage /> : <VocabularyGroupList />} />
      <Route path=":vocabularyGroupId" element={<WordList />} />
      <Route path=":vocabularyGroupId/:wordId" element={<WordDetails />} />
      <Route path=":vocabularyGroupId/import" element={<ImportWords selected={selected} />} />
      <Route path="suggested" element={<MoreSuggestedGroupsComponent />} />
    </Routes>
  );
}

export default Vocabulary;
