import {ReactElement} from "react";


function UnauthorizedFormComponent({children}: Readonly<{ children: ReactElement[] | ReactElement | undefined }>) {

	return (
		<div className="grid place-items-center h-dvh overflow-auto bg-gray-50 lg:bg-[#D3E0FD]">
			<div className="mx-auto w-full max-w-xl">
				{children}
			</div>
		</div>
	)
}

export default UnauthorizedFormComponent;