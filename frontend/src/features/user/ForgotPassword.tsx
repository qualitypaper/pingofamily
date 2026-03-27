import { ChangeEvent, FormEvent, useState } from 'react';
import { useTranslation } from "react-i18next";
import { useDispatch } from "react-redux";
import { API } from "../../app/init";
import { ReactComponent as PenguinNormalWithMap } from "../../assets/penguins/normal_map.svg";
import { ButtonBack } from "../../components/Button/ButtonBack";
import FormButton from "../../components/Button/FormButton";
import LanguageSelect from "../../components/Footer/LanguageSelect";
import InputText from '../../components/Input/InputText';
import ErrorAuthorization from '../../components/Typography/ErrorText';
import { showNotification } from "../../store/headerSlice";
import { check400 } from "../../utils/globalUtils";
import UnauthorizedFormComponent from "./UnauthorizedFormComponent";

const INITIAL_USER_OBJ = {
	email: ""
}

function ForgotPassword() {
	const dispatch = useDispatch();
	const [loading, setLoading] = useState(false)
	const [errorMessage, setErrorMessage] = useState("")
	const [open, setOpen] = useState(false)
	const [userObj, setUserObj] = useState(INITIAL_USER_OBJ)
	const {t} = useTranslation();

	const submitForm = async (e: FormEvent<HTMLFormElement>) => {
		e.preventDefault()
		setErrorMessage("")

		if (userObj.email.trim() === "") {
			setErrorMessage(t("EmailIsRequired"))
			setOpen(true)
		} else {
			setLoading(true)
			try {
				const res = await API.get(`/user/get-forget-password-token?email=${userObj.email}`)
				if (check400(res.status)) {
					setErrorMessage(t("ErrorProcessingRequest"))
					return;
				}
				setUserObj(INITIAL_USER_OBJ);
				dispatch(showNotification({
					status: "success",
					message: t("ResetLinkSent")
				}))
			} finally {
				setLoading(false)
			}
		}
	}

	const updateFormValue = (event: ChangeEvent<HTMLInputElement>) => {
		setErrorMessage("")
		setUserObj({email: event.target.value})
	}

	return (
		<UnauthorizedFormComponent>
			<div className='bg-white rounded-lg relative'>
				<div className="flex absolute  py-2 lg:py-4 pr-[3px] justify-between w-full">
					<ButtonBack classNameText="hidden" className='mb-0 lg:mb-3 bg-none shadow-none ml-4'/>
					<LanguageSelect displayLabel={false}/>
				</div>
				<form onSubmit={submitForm}
							className="flex flex-col justify-center w-[90%] lg:w-[75%] mx-auto h-[70%] ">
					<PenguinNormalWithMap className="w-[100%]  py-8 lg:py-0 h-[60%] object-contain mx-auto"/>

					<div className="mb-5">
						<InputText placeholder={t('Email')} value={userObj.email} type="email"
											 defaultValue={userObj.email}
											 containerStyle="mt-4" updateFormValue={updateFormValue}/>
						<FormButton loading={loading} value={t("SendResetLink")}/>
					</div>


					<ErrorAuthorization open={open} setOpen={setOpen}>{errorMessage}</ErrorAuthorization>
				</form>
			</div>
		</UnauthorizedFormComponent>
	)
}

export default ForgotPassword
