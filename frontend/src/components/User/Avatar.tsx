import { Avatar as ChakraAvatar } from "@chakra-ui/react";

type AvatarProps = {
    size?: "md" | "sm" | "lg" | "xl" | "2xl" | number;
    showIcon?: boolean;
}

const Avatar = ({ size = "md", showIcon = true }: AvatarProps) => {

    return (
        <ChakraAvatar.Root size={typeof size === "string" ? size : "md"}
            className={`${typeof size === "number" && `w-${size} h-${size}`} border-2 cursor-pointer rounded-full`}>
            {showIcon && <ChakraAvatar.Fallback />}
        </ChakraAvatar.Root>
    )
}

export default Avatar;