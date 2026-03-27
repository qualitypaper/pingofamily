import { Avatar, Popover } from "@chakra-ui/react";
import { ReactComponent as LoginIcon } from "assets/icons/login.svg";
import { ReactComponent as RegisterIcon } from "assets/icons/registry.svg";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { selectIsAuthenticated } from "store/user/userSelector";
import { FireStreak } from "../../../components/Streak/FireStreak";
import SelectVocabularyButton from "../../../components/Vocabulary/SelectVocabularyButton";
import i18n from "../../../i18nf/i18n";
import { VocabularyType } from "../../../store/vocabulary/vocabularyTypes";
import { extractImageFile } from "../../../utils/userUtils";
import ProfileDropdownContent, { ProfileDropDownItem } from "./ProfileDropdownContent";

type ProfileDropdownProps = {
  profileImageUrl: string;
  logoutUser: () => void;
  authenticated?: boolean;
};

type RightHeaderProps = {
  profileImageUrl: string;
  selected?: VocabularyType;
  logoutUser: () => void;
};

const RightHeader = ({ profileImageUrl, selected, logoutUser }: RightHeaderProps) => {
  const isAuthenticated = useSelector(selectIsAuthenticated);

  return (
    <div className="order-last items-center">
      <div className="flex items-center gap-3">
        {isAuthenticated && (
          <>
            <FireStreak />
            <SelectVocabularyButton selected={selected} />
          </>
        )}

        <ProfileDropdown
          authenticated={isAuthenticated}
          profileImageUrl={profileImageUrl}
          logoutUser={logoutUser}
        />
      </div>
    </div>
  );
};

const ProfileDropdown = ({
  authenticated = false,
  profileImageUrl,
  logoutUser,
}: ProfileDropdownProps) => {
  const [menuOpen, setMenuOpen] = useState<boolean>(false);

  const toggleMenu = () => {
    setMenuOpen(!menuOpen);
  };

  return (
    <Popover.Root open={menuOpen}>
      <Popover.Trigger onClick={toggleMenu} width='40px'>
        {authenticated && extractImageFile(profileImageUrl).length !== 0 ? (
          <img
            className="w-[40px] h-[40px] object-cover border-1 cursor-pointer rounded-full border-green-200"
            src={profileImageUrl}
            alt=""
          />
        ) : (
          <>
            <Avatar.Root
              size="md"
              className="border-2 cursor-pointer rounded-full border-green-200"
            >
              <Avatar.Fallback />
              <Avatar.Image src="assets/icons/avatar.svg" />
            </Avatar.Root>
          </>
        )}
      </Popover.Trigger>
      <Popover.Positioner>
        <Popover.Content>
          <Popover.Arrow>
            <Popover.ArrowTip />
          </Popover.Arrow>
          <Popover.Body>
            {authenticated ? (
              <ProfileDropdownContent
                profileImageUrl={profileImageUrl}
                setMenuOpen={setMenuOpen}
                logoutUser={logoutUser}
              />
            ) : (
              <ProfileDropdownContentUnauthenticated />
            )}
          </Popover.Body>
        </Popover.Content>
      </Popover.Positioner>
    </Popover.Root>
  );
};

function ProfileDropdownContentUnauthenticated() {
  const { t } = useTranslation();

  return (
    <div className="flex flex-col  gap-3 max-h-[calc(100vh-100px)] w-full  min-w-[296px] overflow-y-auto p-2 ">
      <div className="flex items-center justify-start flex-col">
        <ProfileDropDownItem
          link={`/${i18n.language}/login`}
          icon={<LoginIcon />}
          text={t("Login")}
        />
        <ProfileDropDownItem
          link={`/${i18n.language}/register`}
          icon={<RegisterIcon />}
          text={t("Register")}
        />
      </div>
    </div>
  );
}

export default RightHeader;
