import {ReactComponent as DeleteIcon} from "../../assets/delete.svg";

export const DeleteButton = ({showForm}: { showForm: () => void }) => {

	return (
		<>
			<div onClick={showForm} className="flex flex-col items-start justify-between relative">
				<div
					className="absolute top-0 right-0 p-1 hover:cursor-pointer hover:scale-110 hover:text-red-500 ease-out duration-300">
					<div className="cursor-pointer w-6 h-6 flex items-center justify-start rounded-full">
						<DeleteIcon/>
					</div>
				</div>
			</div>
		</>
	);
};