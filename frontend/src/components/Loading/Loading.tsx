export function Loading({size}: { size?: number }) {
	return (
		<div>
			<span style={{
				height: !!size && size > 0 ? size : 24,
				width: !!size && size > 0 ? size : 24
			}} className="text-blue-600 loader">
			</span>
		</div>
	);
}

export const LoadingPage = ({size}: { size?: number }) => {
	return <div className="flex items-center justify-center h-[100dvh]">
		<span style={{
			height: !!size && size > 0 ? size : 36,
			width: !!size && size > 0 ? size : 36
		}} className={`text-blue-600 loader`}></span>
	</div>
}

export function LoadingWithBackground({size}: { size?: number }) {


	return (
		<div className={`fixed inset-0 flex items-center justify-center z-50`}>
			<div className="bg-black bg-opacity-50 fixed inset-0 z-30"></div>
			<LoadingPage size={size}/>
		</div>
	);
}

