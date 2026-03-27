import {Drawer as ChakraDrawer, Portal} from '@chakra-ui/react'
import {ReactNode} from "react";

export type Placement = 'start' | 'end' | 'bottom' | 'top';

export type DrawerProps = {
	placement: Placement;
	children: ReactNode;
	header?: ReactNode;
	open: boolean;
}

export const Drawer = ({placement, open, children, header}: DrawerProps) => {

	return (
		<ChakraDrawer.Root placement={placement} open={open}>
			<Portal>
				<ChakraDrawer.Backdrop/>
				<ChakraDrawer.Positioner>
					<ChakraDrawer.Content>
						<ChakraDrawer.Header>
							{header}
						</ChakraDrawer.Header>
						<ChakraDrawer.Body>
							{children}
						</ChakraDrawer.Body>
					</ChakraDrawer.Content>
				</ChakraDrawer.Positioner>
			</Portal>
		</ChakraDrawer.Root>
	)
}