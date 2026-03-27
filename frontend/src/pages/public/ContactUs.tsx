import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useDispatch } from "react-redux";
import { showNotification } from "../../store/headerSlice";
import InputText from "../../components/Input/InputText";
import { ButtonBack } from "../../components/Button/ButtonBack";
import { ErrorText } from "../../components/Typography/ErrorText";
import { API } from "app/init";

type ContactUsPageFormType = {
	email: string;
	header: string;
	name: string;
	lastname: string;
	description: string;
};

const INITIAL_FORM_OBJ: ContactUsPageFormType = {
	email: "",
	header: "",
	name: "",
	lastname: "",
	description: "",
};


const ContactUsPage = () => {
	const dispatch = useDispatch();
	const [errorMessage, setErrorMessage] = useState<string>("");
	const [formObject, setFormObject] = useState<ContactUsPageFormType>(INITIAL_FORM_OBJ);
	const [loading, setLoading] = useState<boolean>(false);
	const { t } = useTranslation();

	const updateFormValue = (updateType: keyof ContactUsPageFormType, value: string) => {
		setFormObject({ ...formObject, [updateType]: value });
	}
	const updateContactUs = () => {
		if (window.location.pathname === '/contact-us') {
			dispatch(showNotification({ message: "You have successfully sent your message.", status: 1 }));
		}
	}

	const submitForm = async (e: React.FormEvent<HTMLFormElement>) => {
		e.preventDefault();
		setLoading(true);
		try {
			await API.post("/auth/contact-us", {
				email: formObject.email,
				header: formObject.header,
				name: formObject.name + " " + formObject.lastname,
				description: formObject.description
			}, {}, undefined, false);

			dispatch(
				showNotification({
					status: "success",
					message: "You have successfully sent your message.",
				})
			);
			setFormObject(INITIAL_FORM_OBJ);
		} catch (error) {
			setErrorMessage(String(error));
		} finally {
			setLoading(false);
		}
	};

	return (
		<div className="w-full max-w-[64rem] m-auto lg:p-0 p-4 mt-2 flex-1 mb-20">
			<div className="flex flex-col gap-4">
				<ButtonBack className="lg:pt-4" backUrl="/" />
				<form onSubmit={submitForm}>
					<h1 className="text-4xl font-bold text-center mb-6">{t("ContactUsPageMainText")}</h1>
					<div className="mb-4 flex flex-col">
						<div className="flex flex-col gap-4">
							<InputText
								required
								type="text"
								value={formObject.name}
								placeholder={t("ContactUsPageInputPlaceholderName")}
								containerStyle="text-base"

								updateFormValue={(e) => updateFormValue("name", e.target.value)}
							/>
							<InputText
								required
								type="text"
								value={formObject.lastname}
								placeholder={t("ContactUsPageInputPlaceholderLastName")}
								containerStyle="text-base"

								updateFormValue={(e) => updateFormValue("lastname", e.target.value)}
							/>
							<InputText
								required
								type="email"
								value={formObject.email}
								placeholder={t("ContactUsPageInputPlaceholderEmail")}
								containerStyle="text-base"

								updateFormValue={(e) => updateFormValue("email", e.target.value)}
							/>
							<InputText
								required
								type="text"
								value={formObject.header}
								placeholder={t("ContactUsPageInputPlaceholderHeader")}
								containerStyle="text-base"

								updateFormValue={(e) => updateFormValue("header", e.target.value)}
							/>

							<textarea
								required
								value={formObject.description}
								placeholder={t("ContactUsPageTextareaPlaceholderDetails")}
								onChange={(e: React.ChangeEvent<HTMLTextAreaElement>) => updateFormValue("description", e.target.value)}
								className="outline-none text-md lg:text-lg border border-[#1154FF] rounded-xl py-3 px-4 border-opacity-80 w-full bg-white"
							/>
						</div>
					</div>

					<ErrorText styleClass="mt-8">{errorMessage}</ErrorText>
					<button onClick={() => updateContactUs()}
						className={"btn-all rounded w-full text-white" + (loading ? " loading" : "")} type="submit">
						<p className="p-2 font-bold text-white">{t("ContactUsPageSubmitButton")}</p>
					</button>
				</form>
			</div>
		</div>
	);
};

export default ContactUsPage;
