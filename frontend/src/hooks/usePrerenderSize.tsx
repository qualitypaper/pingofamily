import {ComponentType, ReactPortal, useEffect, useMemo, useRef, useState} from "react";
import {createPortal} from "react-dom";

export type PrerenderSize = {
	height: number;
	width: number;
}

export function usePrerenderSize(
	Component: ComponentType<any>,
	props: Record<string, any> = {}
): { size: PrerenderSize | null, portal: ReactPortal | null } {

	const measureRef = useRef<HTMLDivElement>(null);
	const [size, setSize] = useState<PrerenderSize | null>(null);

	const [container, setContainer] = useState(() => document.createElement("div"));

	useEffect(() => {
		const temp = container;
		temp.style.position = "absolute";
		temp.style.visibility = "hidden";
		temp.style.pointerEvents = "none";
		document.body.appendChild(temp);
		setContainer(temp)

		return () => {
			document.body?.removeChild(temp);
		};
	}, [container]);


	const portal = useMemo(() =>
			createPortal(
				<div ref={measureRef}>
					<Component {...props} />
				</div>,
				container
			),
		[Component, container, props]
	);

	useEffect(() => {
		if (measureRef.current) {
			setSize({height: measureRef.current.offsetHeight, width: measureRef.current.offsetWidth});
		}
	}, [container, portal])

	return {size, portal};
}
