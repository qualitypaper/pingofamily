import {useState} from "react"
import {useSelector} from 'react-redux'
import {selectCurrentlySelected} from "../store/vocabulary/vocabularySelector"
import LeftSidebar from "./Header/LeftSidebar"
import PageContent from "./PageContent"

function Layout() {
	const [open, setOpen] = useState(false)
	const latest = useSelector(selectCurrentlySelected)

	return (
		<>
			<div>
				<label htmlFor="my-drawer" aria-label="close sidebar" className="drawer-overlay"></label>
				<LeftSidebar open={open} setOpen={setOpen}/>
			</div>
			<div className="w-full  mx-auto">
				{latest && <PageContent selected={latest} open={open} setOpen={setOpen}/>}
			</div>
		</>
	)
}

export default Layout
