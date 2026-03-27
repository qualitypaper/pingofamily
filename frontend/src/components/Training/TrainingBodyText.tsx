import React from "react";

type TrainingBodyTextProps = {
	text: string;
	subtext?: string;
	color?: string;
	visible?: boolean;
}

const TrainingBodyText = React.memo(({text, subtext, color = "grey-700", visible = true}: TrainingBodyTextProps) => {

	return (
		<span
			className={`${visible ? "visible animation" : "invisible"} flex gap-2 items-center text-2xl text-center mt-1 text-${color}`}>
            {text}
			{subtext && <span className="text-lg text-gray-500">({subtext})</span>}
        </span>
	)
});

export default TrainingBodyText;