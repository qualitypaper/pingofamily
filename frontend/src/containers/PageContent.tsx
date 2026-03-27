import { Dispatch, SetStateAction, useEffect, useRef } from "react";
import { Route, Routes } from "react-router-dom";
import routes from "../routes/routes";
import { Footer } from "./Footer";
import Header from "./Header/Header";

type PageContentProps = {
  selected: any;
  open: boolean;
  setOpen: Dispatch<SetStateAction<boolean>>;
};

function PageContent({ selected, open, setOpen }: PageContentProps) {
  const mainContentRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    mainContentRef.current &&
      mainContentRef.current.scroll({
        top: 0,
        behavior: "smooth",
      });
  }, []);

  return (
    <div className="flex flex-col top-0 min-h-screen w-full bg-[#F4F9FF]">
      <Header selected={selected} setOpen={setOpen} open={open} />
      <main
        className="h-full flex-1 w-full max-w-[64rem] m-auto mb-6 p-4"
        ref={mainContentRef}
      >
        <Routes>
          {routes.map((route, i) => (
            <Route key={i} path={`${route.path}`} element={<route.component />} />
          ))}
        </Routes>
      </main>
      <Footer />
    </div>
  );
}

export default PageContent;
