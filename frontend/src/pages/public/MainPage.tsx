import { Outlet } from "react-router-dom";
import { HeaderMain } from "../../components/MainPage/HeaderMain";
import { Footer } from "../../containers/Footer";

function MainPage() {

  return (
    <>
      <div className="w-full h-100vh flex flex-col flex-1 bg-[#F4F9FF]">
        <HeaderMain />
        <Outlet />
        <Footer />
      </div>
      {/* <CookiesComponent/> */}
    </>
  );
}

export default MainPage;
