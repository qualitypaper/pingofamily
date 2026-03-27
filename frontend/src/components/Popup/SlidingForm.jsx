import {useEffect, useRef} from 'react';
import {ReactComponent as CloseIcon} from 'assets/icons/close.svg';


const SlidingForm = ({onClose, children, position = "", showCloseButton = true, className = ""}) => {
	const ref = useRef(null);

	useEffect(() => {
		const handleClickOutside = (event) => {
			if (ref.current && !ref.current.contains(event.target)) {
				onClose()
			}
		};

		document.addEventListener('mousedown', handleClickOutside, true);

		return () => {
			document.removeEventListener('mousedown', handleClickOutside, true);
		};
	}, [onClose]);

	useEffect(() => {
		const handleESCClick = (event) => {
			if (event.key === 'Escape') {
				onClose();
			}
		};

		document.addEventListener('keydown', handleESCClick, true);

		return () => {
			document.removeEventListener('keydown', handleESCClick, true);
		};
	}, [onClose]);

	return (
		<div className={`fixed inset-0 flex items-center justify-center z-50`}>
			<div className="bg-black bg-opacity-50 fixed inset-0 z-40"></div>
			<div ref={ref}
					 className={`${className} relative z-50  mx-4 sm:mx-8 p-4 sm:p-8 bg-white dark:bg-[#2b333b] shadow-md rounded-2xl`}>
				{
					showCloseButton && <button
						className={`absolute ${position} cursor-pointer bg-opacity-0 hover:bg-opacity-50 transition-all duration-300 ease-out p-1 rounded-full`}
						onClick={onClose}>
						<CloseIcon height={24} width={24}/>
					</button>
				}
				{children}
			</div>
		</div>
	);
};

export default SlidingForm;
