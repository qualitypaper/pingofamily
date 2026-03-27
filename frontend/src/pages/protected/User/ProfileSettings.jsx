import { Spinner } from "@chakra-ui/react";
import { useRef, useState } from 'react';
import { useTranslation } from "react-i18next";
import { useDispatch, useSelector } from "react-redux";
import TitleCard from '../../../components/Cards/TitleCard';
import InputText from '../../../components/Input/InputText';
import { showNotification } from "../../../store/headerSlice";
import { selectUserDetails } from "../../../store/user/userSelector";
import { setName, setUserDetails } from "../../../store/user/userSlice";

import { API } from "app/init";
import { ReactComponent as CameraIcon } from 'assets/icons/camera.svg';
import Avatar from "components/User/Avatar";
import { LoadingPage } from "../../../components/Loading/Loading";

function ProfileSettings() {
	const dispatch = useDispatch();
	const userDetails = useSelector(selectUserDetails);

	const [profileImageUrl, setProfileImageUrl] = useState(userDetails.profileImageUrl);
	const [nameInput, setNameInput] = useState(userDetails.name || "");
	const [loading, setLoading] = useState(false);
	const [showAvatar, setShowAvatar] = useState(true);

	const fileInputRef = useRef(null);
	const { t } = useTranslation();

	const handleFileChange = async (event) => {
		const selectedFile = event.target.files[0];

		if (selectedFile) {
			const reader = new FileReader();
			reader.onloadend = () => {
				setProfileImageUrl(reader.result);
				dispatch(setUserDetails({
					...userDetails,
					profileImageUrl: reader.result
				}));
			};
			reader.readAsDataURL(selectedFile);

			const data = new FormData();
			data.append("file", selectedFile);

			try {
				await API.post(`/user/change-avatar`, data);
			} catch (err) {
				if (err.response?.status === 413) {
					dispatch(showNotification({ message: t("FileTooBig"), status: 0 }));
				}
			}
		}
	};

	const handleImageClick = () => {
		fileInputRef.current.click();
	};

	const updateProfile = async () => {
		setLoading(true);
		const url = `/user/change-name`;
		try {
			await API.post(url, {
				data: nameInput
			},
			);
			dispatch(setName(nameInput));
			dispatch(showNotification({ message: t("ProfileUpdated"), status: "success" }));
		} catch (error) {
			console.error("Error updating profile", error);
		} finally {
			setLoading(false);
		}
	};

	const updateFormValue = ({ updateType, value }) => {
		if (updateType === "language") {
			dispatch(setUserDetails({
				...userDetails,
				language: value
			}));
		} else {
			console.log(updateType);
		}
	};

	return (
		loading ? (
			<LoadingPage />
		) : (
			<TitleCard title={t("ProfileSettings")} className="mt-5">
				<div className="flex flex-col items-center">
					<button
						className="relative group cursor-pointer w-28 h-28 my-6"
						onClick={handleImageClick}
					>
						{profileImageUrl ? (
							<img className="object-cover w-28 h-28 rounded-full" src={profileImageUrl} alt="" />
						) : (
							<>
								<Avatar size={28} showIcon={showAvatar}/>
							</>
						)}
						<div
							onMouseOver={() => setShowAvatar(false)}
							onMouseLeave={() => setShowAvatar(true)}
							className="absolute inset-0 bg-black bg-opacity-50 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-300 rounded-full"
						>
							<CameraIcon className="text-white text-4xl" />
						</div>
						<input
							type="file"
							accept="image/*"
							onChange={handleFileChange}
							ref={fileInputRef}
							className="hidden"
						/>
					</button>
					<div className="grid grid-cols-1 md:grid-cols-2 gap-6 w-full">
						<InputText
							labelTitle="Email"
							disabled
							value={userDetails.email}
							updateFormValue={updateFormValue}
							className="w-full"
						/>
						<InputText
							labelTitle="Name"
							value={nameInput}
							updateFormValue={(e) => setNameInput(e.target.value)}
							className="w-full"
						/>
					</div>
					<div className="mt-16 w-full flex justify-center md:justify-end ">
						<button
							className="btn btn-all bg-[#1E90FF] transition transform  hover:bg-blue-600 border border-blue-500 hover:border-blue-600 text-white py-2 px-4 rounded w-full md:w-36"
							onClick={updateProfile}
							disabled={loading}
						>
							{loading ? (
								<Spinner size="md" />
							) : (
								t("UpdateProfile")
							)}
						</button>
					</div>
				</div>
			</TitleCard>
		)
	);
}

export default ProfileSettings;