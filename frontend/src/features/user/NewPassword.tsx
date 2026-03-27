import React, {ChangeEvent, FormEvent, useState} from "react";
import {useTranslation} from "react-i18next";
import {useDispatch} from "react-redux";
import {useNavigate, useParams} from "react-router-dom";
import {BASE_URL} from "constant";
import {showNotification} from "../../store/headerSlice";
import {check400} from "../../utils/globalUtils";
import PasswordInput from "./PasswordInput";
import UnauthorizedFormComponent from "./UnauthorizedFormComponent";
import ErrorText from "../../components/Typography/ErrorText";
import {ReactComponent as NormalButterfly} from '../../assets/penguins/normal_butterfly.svg';
import {ButtonBack} from "../../components/Button/ButtonBack";
import LanguageSelect from "../../components/Footer/LanguageSelect";
import FormButton from "../../components/Button/FormButton";

function NewPassword() {

	const dispatch = useDispatch();
	const navigate = useNavigate();
	const {token} = useParams();
	const [open, setOpen] = useState<boolean>(false);
	const [errorMessage, setErrorMessage] = useState<string>();
	const [loading, setLoading] = useState<boolean>(false);
	const [newPassword, setNewPassword] = useState<string>("");
	const {t} = useTranslation();


	async function submitForm(e: FormEvent<HTMLFormElement>) {
		e.preventDefault();
		setLoading(true);
		const res = await fetch(`${BASE_URL}/auth/forgot-password/${token}?newPassword=${newPassword}`);
		setLoading(false);

		if (check400(res.status)) {
			setErrorMessage(t('PasswordChangedError'));
			setOpen(true);
			return;
		}
		dispatch(showNotification({
			status: 1,
			message: t('PasswordChanged')
		}))
		setTimeout(() => {
			navigate('/login')
		}, 750)
	}

	return (
		<>
			<ErrorText open={open} setOpen={setOpen}>{errorMessage}</ErrorText>
			<UnauthorizedFormComponent>
				<div className='bg-white rounded-lg relative'>
					<div className="flex absolute py-2 lg:py-4 pr-[3px] justify-between w-full">
						<ButtonBack classNameText="hidden" className='mb-0 lg:mb-3 bg-none shadow-none ml-4'/>
						<LanguageSelect displayLabel={false}/>
					</div>
					<form onSubmit={submitForm}
								className="flex flex-col justify-center w-[90%] lg:w-[75%] mx-auto h-[70%]">
						<NormalButterfly className="w-[100%]  py-8 lg:py-0 h-[60%] object-contain mx-auto"/>
						<div className="my-4 flex flex-col gap-4">
							<PasswordInput
								onChange={(e: ChangeEvent<HTMLInputElement>) => setNewPassword(e.target.value)}/>
							<FormButton value={t("ResetPassword")} loading={loading}/>
						</div>
					</form>
				</div>
			</UnauthorizedFormComponent>
		</>
	)
}

export default NewPassword;
