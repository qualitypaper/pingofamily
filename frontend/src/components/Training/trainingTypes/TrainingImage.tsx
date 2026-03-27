import React, {forwardRef, Ref} from "react";
import empty from '../../../assets/icons/empty.png';

const TrainingImage = React.memo(forwardRef(({src, className}: {
	src: string,
	className?: string
}, ref: Ref<HTMLImageElement>) => {
	return (
		<img
			ref={ref}
			src={src && src.length !== 0 ? src : empty} alt=''
			className={`training-image animation ${className}`}/>
	)
}));

export default TrainingImage
