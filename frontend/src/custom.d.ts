declare module "*.svg" {
	import React = require("react");
	export const ReactComponent: React.FC<React.SVGProps<SVGSVGElement>>;

	const src: string;
	export default src;
}

declare module 'uuid'

export type Difficulty = "EASY" | "MEDIUM" | "HARD"

declare var require: any;

declare module '@chakra-ui/react' {
	export const Avatar: any;
	export const Spinner: any;
	export const IconButton: any;
	export const Tooltip: any;
	export const Drawer: any;
	export const DrawerOverlay: any;
	export const DrawerContent: any;
	export const Input: any;
	export const ChakraProvider: any;
	export const defaultSystem: any;
	export const Button: any;
	export const Textarea: any;
	export const Icon: any;
}