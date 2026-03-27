import { Dispatch, SetStateAction, useEffect, useRef, useState } from 'react';
import { Link, NavLink, useLocation } from 'react-router-dom';
import i18n from '../../i18nf/i18n';
import SidebarRoutes from "../../routes/sidebar";
import { Drawer } from "../../components/Drawer/Drawer";
import { IconButton } from "@chakra-ui/react";
import { ReactComponent as CloseIcon } from 'assets/icons/close.svg';
import { WEBSITE_NAME } from 'constant';

type LogoSidebarProps = {
	title: string;
	className?: string;
	classNameTitle?: string;
};

type LeftSidebarProps = {
	open: boolean;
	setOpen: Dispatch<SetStateAction<boolean>>;
};


export const LogoSidebar = ({ title, className, classNameTitle }: LogoSidebarProps) => {
	return (
		<ul>
			<li className={className}>
				<Link to={`/${i18n.language}`} className="flex items-center gap-2">
					<img className="mask mask-squircle w-9 h-9 lg:w-10 lg:h-10 rounded" src="/logo.png"
						alt="PingoFamily Logo" />
					<span className={`text-2xl text-color-big-text font-['Sour_Gummy'] font-semibold ${classNameTitle}`}>{title}</span>
				</Link>
			</li>
		</ul>
	);
};

function LeftSidebar({ open, setOpen }: LeftSidebarProps) {
	const location = useLocation();
	const [isMobile, setIsMobile] = useState(window.innerWidth <= 768);
	const ref = useRef<null | HTMLDivElement>(null);
	const routes = SidebarRoutes();


	useEffect(() => {
		const handleClickOutside = (event: MouseEvent) => {
			if (ref.current && !ref.current.contains(event.target as Node)) {
				setOpen(false);
			}
		};
		document.addEventListener('mousedown', handleClickOutside);
		return () => {
			document.removeEventListener('mousedown', handleClickOutside);
		};
	}, [ref, setOpen]);

	useEffect(() => {
		const handleResize = () => {
			setIsMobile(window.innerWidth <= 768);
		};
		window.addEventListener('resize', handleResize);
		return () => {
			window.removeEventListener('resize', handleResize);
		};
	}, []);

	const toggleSidebar = () => {
		setOpen(!open);
	};

	return (
		<>
			{isMobile && (
				<Drawer open={open} placement="start" header={
					<div className="flex justify-between items-center border-b-2 pl-2 w-full">
						<LogoSidebar className="text-lg max-md:text-md text-color-big-text font-semibold"
							title={WEBSITE_NAME} />
						<IconButton onClick={toggleSidebar}>
							<CloseIcon className="w-6 h-6" />
						</IconButton>
					</div>
				}>
					<ul className="flex flex-col justify-end">
						{routes.map((route, k) => (
							<li className="p-4 hover:bg-blue-300" key={k}>
								<NavLink
									end
									onClick={toggleSidebar}
									to={route.path}
									className={`flex gap-1 ${location.pathname === route.path && 'font-semibold'}`}
								>
									{route.icon} {route.name}
									{location.pathname === route.path && (
										<span
											className="absolute inset-y-0 left-0 w-1 rounded-tr-md rounded-br-md bg-button-color"
											aria-hidden="true"></span>
									)}
								</NavLink>
							</li>
						))}
					</ul>
				</Drawer>
			)}
		</>
	);
}

export default LeftSidebar;
