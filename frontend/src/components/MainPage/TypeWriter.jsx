import {useEffect, useState} from 'react';

const TypeWriter = ({texts}) => {
	const [currentText, setCurrentText] = useState('')
	const [isDeleting, setIsDeleting] = useState(false)
	const [charIndex, setIsCharIndex] = useState(0)
	const [textIndex, setTextIndex] = useState(0);

	useEffect(() => {
		const handleClick = () => {
			let fullText = texts[textIndex];

			setCurrentText(fullText.substring(0, charIndex))

			if (!isDeleting && charIndex === fullText.length) {
				setTimeout(() => setIsDeleting(true), 1000)
			} else if (isDeleting && charIndex === 0) {
				setIsDeleting(false)
				setTextIndex((prev) => (prev + 1) % texts.length)
			}

			setIsCharIndex((prev) => (prev + (!isDeleting ? 1 : -1)))
		}

		const timer = setTimeout(handleClick, isDeleting ? 50 : 150)
		return () => {
			clearTimeout(timer)
		}
	}, [currentText, isDeleting, charIndex, textIndex, texts])

	useEffect(() => {
		setIsCharIndex(0)
		setCurrentText('')
		setIsDeleting(false)
		setTextIndex(0)
	}, [texts]);


	return (
		<span className="typewriter-text">
            {currentText}
        </span>
	);
};

export default TypeWriter;
