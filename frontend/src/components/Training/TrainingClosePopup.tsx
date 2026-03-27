import SlidingForm from "../Popup/SlidingForm";
import React from "react";
import SlidingFormButton from "../Popup/SlidingFormButton";


function TrainingClosePopup({ setSlidingForm, onBackClick }:
	{ setSlidingForm: (value: boolean) => void, onBackClick: () => void }) {

	return (
		<SlidingForm showCloseButton={false} onClose={() => setSlidingForm(false)}>
			<form className={`w-[90vw] max-w-[320px] h-auto rounded flex flex-col gap-4 justify-start`}>
				<span className='text-xl font-bold m-0'>Are you sure you want to leave the training?</span>
				<SlidingFormButton onBackClick={onBackClick} setSlidingForm={setSlidingForm} />
			</form>
		</SlidingForm>
	)
}

export default TrainingClosePopup;
