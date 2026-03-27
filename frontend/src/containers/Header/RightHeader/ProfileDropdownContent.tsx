import { ReactComponent as SettingsIcon } from 'assets/icons/settings.svg';
import { ReactComponent as BarChartIcon } from 'assets/icons/bar-chart.svg';
import { ReactComponent as LogoutIcon } from 'assets/icons/logout.svg';
import React, { MouseEventHandler } from "react";
import { useTranslation } from "react-i18next";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import i18n from "../../../i18nf/i18n";
import { selectUserDetails } from "../../../store/user/userSelector";
import { extractImageFile } from '../../../utils/userUtils';
import Avatar from 'components/User/Avatar';


type ProfileDropdownContentProps = {
	setMenuOpen: (val: boolean) => void;
	logoutUser: () => void;
	profileImageUrl: string;
}

const ProfileDropdownContent = ({ setMenuOpen, logoutUser, profileImageUrl }: ProfileDropdownContentProps) => {
	const { t } = useTranslation()
	const userDetails = useSelector(selectUserDetails);

	return (
		<div className="flex flex-col  gap-3 max-h-[calc(100vh-100px)] w-full  min-w-[296px] overflow-y-auto p-2 ">
			<div className="gap-2 flex flex-col ">
				<div className="flex items-center gap-4 m-2 overflow-hidden">
					{extractImageFile(profileImageUrl) ? (
						<img
							className="w-[60px] h-[60px] object-cover border-1 rounded-full border-green-200"
							src={profileImageUrl}
							alt=""
						/>
					) : (
						<Avatar size="lg" />
					)}
					<h4 className="font-semibold text-lg">{userDetails.name}</h4>
				</div>
			</div>

			<div className="flex items-center justify-start flex-col">
				<ProfileDropDownItem text={t("Statistics")}
					icon={<BarChartIcon className="w-[20px] h-[20px]" />}
					link={`/${i18n.language}/statistics`}
					onClick={() => setMenuOpen(false)}
				/>
				<ProfileDropDownItem text={t("ProfileSettings")}
					icon={<SettingsIcon width="20" height="20" />}
					link={`/${i18n.language}/settings-profile`}
					onClick={() => setMenuOpen(false)}
				/>
				<ProfileDropDownItem text={t("Logout")}
					icon={<LogoutIcon width="20" height="20" />}
					onClick={logoutUser}
				/>
			</div>
		</div>
	)
}

interface DropdownItem {
	text: string;
}

class ProfileButtonDropdownProps implements DropdownItem {
	icon: React.ReactNode;
	text: string;
	onClick?: MouseEventHandler<HTMLButtonElement>;

	constructor(icon: React.ReactNode, text: string, onClick?: MouseEventHandler<HTMLButtonElement>) {
		this.icon = icon;
		this.text = text;
		this.onClick = onClick;
	}
}

class ProfileLinkDropdownProps implements DropdownItem {
	icon: React.ReactNode;
	onClick?: MouseEventHandler<HTMLAnchorElement>;
	link: string;
	text: string;

	constructor(icon: React.ReactNode, text: string, link: string, onClick?: MouseEventHandler<HTMLAnchorElement>) {
		this.icon = icon;
		this.text = text;
		this.onClick = onClick;
		this.link = link;
	}
}

let isButton = (p: any): p is ProfileButtonDropdownProps => !(!!p.link)
let isLink = (p: any): p is ProfileLinkDropdownProps => !!p.link

export const ProfileDropDownItem = (props: ProfileButtonDropdownProps | ProfileLinkDropdownProps) => {

	if (isButton(props)) {
		const casted = props as ProfileButtonDropdownProps;

		return (
			<button
				onClick={casted.onClick}
				className="p-2 gap-1 rounded-lg w-full flex hover:bg-[#F4F3F3] transition duration-300"
			>
				{casted.icon}
				<p className="text-[15px]">{casted.text}</p>
			</button>
		)
	} else if (isLink(props)) {
		const casted = props as ProfileLinkDropdownProps;

		return (
			<Link
				to={casted.link}
				onClick={casted.onClick}
				className="p-2 gap-1 rounded-lg w-full flex hover:bg-[#F4F3F3] transition duration-300"
			>
				{casted.icon}
				<p className="text-[15px]">{casted.text}</p>
			</Link>
		)
	} else {
		return null;
	}
}

export default ProfileDropdownContent;