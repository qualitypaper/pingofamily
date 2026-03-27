import { useMediaQuery } from '@react-hook/media-query';
import { ReactComponent as Bars3Icon } from 'assets/icons/bars3.svg';
import { NavLink, useLocation } from "react-router-dom";
import { WEBSITE_NAME } from "../../constant";
import SidebarRoutes from "../../routes/sidebar";
import { LogoSidebar } from "./LeftSidebar";

type SidebarProps = {
	openLeftSidebar: () => void;
}

const HeaderRoutes = ({ openLeftSidebar }: SidebarProps) => {
	const location = useLocation();
	const routes = SidebarRoutes();
 	const isMobile = useMediaQuery("(max-width: 782px)")

	return (
		<div className="drawer-content flex items-center gap-3 w-full">
			<button
				onClick={openLeftSidebar}
				className="btn btn-ghost text-black drawer-button md:hidden"
			>
				<Bars3Icon className="h-7 inline-block w-7" />
			</button>
			<div className="flex items-center justify-between gap-10 ml-4 lg:ml-0">
				<LogoSidebar
					className="hidden md:block"
					title={WEBSITE_NAME}
				/>
				{!isMobile && (
					<ul className="items-center gap-6 md:flex lg:justify-between">
						{routes.map((route, k) => (
							<li className="flex" key={route.name + k}>
								<NavLink
									end
									to={route.path}
									className="flex gap-2 text-color-big-text font-bold
                                            hover:bg-slate-100 rounded-md p-2"
								>
									{route.icon} {route.name}

									{location.pathname === route.path && (
										<span
											className="absolute t inset-y-0 left-0 w-1 rounded-tr-md rounded-br-md bg-blue-500"
											aria-hidden="true"
										></span>
									)}
								</NavLink>
							</li>
						))}
					</ul>
				)}
			</div>
		</div>
	)
};


export default HeaderRoutes;
