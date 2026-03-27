import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import i18n from "../../i18nf/i18n";
import { selectUserDetails } from "../../store/user/userSelector";
import { logOut } from "../../store/user/userSlice";
import { clearVocabularySlice } from "../../store/vocabulary/vocabularySlice";
import { VocabularyType } from "../../store/vocabulary/vocabularyTypes";
import HeaderRoutes from "./HeaderRoutes";
import RightHeader from "./RightHeader/RightHeader";
import { API } from "app/init";

export type HeaderProps = {
  selected?: VocabularyType;
  open: boolean;
  setOpen: (open: boolean) => void;
};

function Header({ selected, open, setOpen }: Readonly<HeaderProps>) {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { profileImageUrl } = useSelector(selectUserDetails);

  const openLeftSidebar = () => {
    setOpen(!open);
  };

  async function logoutUser() {
    await API.post("/auth/logout", {});
    document.cookie = "";
    dispatch(logOut());
    dispatch(clearVocabularySlice());
    sessionStorage.setItem(
      "isValidated",
      JSON.stringify({
        valid: false,
        token: "",
      }),
    );
    navigate(`/${i18n.language}`);
  }

  return (
    <div className="flex justify-end flex-col w-full lg:max-w-[64rem] m-auto">
      <div className="flex z-64 pt-2 px-4 justify-between w-full lg:w-[64rem] p-3">
        <HeaderRoutes openLeftSidebar={openLeftSidebar} />

        <RightHeader
          profileImageUrl={profileImageUrl}
          selected={selected}
          logoutUser={logoutUser}
        />
      </div>
      <hr className="line" />
    </div>
  );
}

export default Header;
