import {ReactNode} from "react";

export const CardMain = ({mainText, subText, img}: { mainText: string; subText: string; img: ReactNode }) => {
	return (
		<div className="relative">
			<div className="benefits-thumb">
				{img}
			</div>
			<div className="btn-card relative overflow-hidden w-full">
				<div className="p-4 relative z-10 ">
					<h2 className="card-title">{mainText}</h2>
					<p className="text-sm ">{subText}</p>
				</div>
			</div>
		</div>
	);
};