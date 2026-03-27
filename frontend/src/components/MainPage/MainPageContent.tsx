import { Suspense } from "react"
import { SectionCard } from "./Sections/SectionCard"
import { SectionFour } from "./Sections/SectionFour"
import SectionLanguage from "./Sections/SectionLanguage"
import { SectionOne } from "./Sections/SectionOne"
import { SectionThree } from "./Sections/SectionThree"
import { LoadingPage } from "components/Loading/Loading"

const MainPageContent = () => {
	return (
		<Suspense fallback={<LoadingPage />}>
			<div className="flex-1 flex flex-col gap-20 lg:gap-36">
				<SectionOne />
				<SectionCard />
				<SectionLanguage />
				<SectionThree />
				<SectionFour />
			</div>
		</Suspense>
	)
}

export default MainPageContent;
