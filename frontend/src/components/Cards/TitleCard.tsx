import React, {FC} from "react";
import {Link} from "react-router-dom"; // Import Link if not already imported
import Subtitle from "../Typography/Subtitle";

export type WrappedTitleCardProps = {
	title?: string | React.ReactElement | undefined;
	icon?: any;
	children?: React.ReactNode;
	toUrl?: string;
	alignment?: string;
	titleClassName?: string;
	border?: boolean;
	className?: string;
	setInputValue?: (value: (preValue: any) => string) => void
};

export type TitleCardProps = {
	TopSideButtons?: string | undefined;
} & WrappedTitleCardProps;

export const WrappedTitleCard: FC<TitleCardProps> = ({
																											 title,
																											 icon,
																											 children,
																											 className,
																											 toUrl,
																											 titleClassName,
																											 border,
																											 alignment,
																										 }: TitleCardProps) => {
	return (
		<div className={`${alignment}`}>
			<TitleCard
				border={border}
				icon={icon}
				toUrl={toUrl}
				className={className}
				title={title}
				titleClassName={titleClassName}
			>
				{children}
			</TitleCard>
		</div>
	);
};

export const TitleCard: FC<TitleCardProps> = ({
																								title,
																								icon,
																								children,
																								TopSideButtons,
																								toUrl,
																								titleClassName,
																								className

																							}: TitleCardProps) => {
	return (
		<div className={`w-full ${className}`}>
			<Subtitle
				styleClass={` ${TopSideButtons ? "inline-block" : ""}`}
			>
				<span>{icon}</span>
				<span className={`font-bold text-xl md:text-3xl ${titleClassName}`}>
             {title}
             </span>
				{TopSideButtons && (
					<Link
						to={toUrl ?? ''}
						className="inline-block float-right rounded-lg border border-primary
                            p-3 hover:bg-violet-700"
					>
						{TopSideButtons}
					</Link>
				)}
			</Subtitle>
			<div className="w-full font-medium">
				{children}
			</div>
		</div>
	);
};

export default TitleCard;
