function Subtitle({styleClass, children}) {
	return (
		<div className={`text-lg font-semibold mb-2 ${styleClass}`}>
			{children}
		</div>
	)
}

export default Subtitle