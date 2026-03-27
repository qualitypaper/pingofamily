import {ReactComponent as PlusIcon} from "../../assets/icons/plus.svg";

function AddVocabularyButton({setOpen, setIsFormVisible}) {


	return (
		<div onClick={() => {
			setOpen(false)
			setIsFormVisible(true)
		}}
				 className="relative btn-ghost cursor-pointer hover:bg-slate-100 flex items-center justify-center h-20 md:h-[6.5rem]">
			<button
				className="rounded flex items-center justify-center cursor-pointer"
			>
				<div className="flex items-center justify-center m-auto w-20">
					<PlusIcon fontSize=""/>
				</div>

			</button>
		</div>
	);
}

export default AddVocabularyButton;
