import {useState} from "react";
import {ReactComponent as HidePasswordIcon} from '../../assets/icons/eye_password_hide.svg';
import {ReactComponent as ShowPasswordIcon} from '../../assets/icons/eye_show.svg';
import {useTranslation} from "react-i18next";

type PasswordInputProps = {
	onChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
}

const PasswordInput = ({onChange}: PasswordInputProps) => {
	const [showPassword, setShowPassword] = useState(false);
	const {t} = useTranslation();
	const changePasswordInputState = () => {
		setShowPassword(!showPassword)
	}

	return (
		<div className='w-full relative bg-white'>
			<input autoComplete="current-password" id="outlined-basic" placeholder={t('Password')}
						 className='outline-none text-md lg:text-lg border hover:border-opacity-100 hover:border-[#1154FF]  border-[#1154FF] rounded-xl  py-3 px-4  w-full border-opacity-80  bg-white'
						 type={showPassword ? "text" : "password"} onChange={onChange}/>
			<button type='button' className='absolute top-[0.9rem] right-3' onClick={changePasswordInputState}>
				{showPassword ? <ShowPasswordIcon className='w-6 h-6 lg:w-7 lg:h-7'/> :
					<HidePasswordIcon className='w-6 h-6 lg:w-7 lg:h-7'/>}
			</button>
		</div>
	)
}

export default PasswordInput
