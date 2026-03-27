import { Loading } from "components/Loading/Loading";
import { Suspense } from "react";

type ImageProps = {
  src?: string;
  alt?: string;
  children?: React.ReactNode;
  className?: string;
};

const Image = ({ src, alt, children, className }: ImageProps) => {
  return (
    <span className={className}>
      <Suspense fallback={<Loading />}>
        {src ? <img src={src} alt={alt} /> : children}
      </Suspense>
    </span>
  );
};

export default Image;
