type LoadingWaveProps = {
	color?: string;
}
const LoadingWave = ({color}: LoadingWaveProps) => {

	const styles = `loading-bar bg-${color ?? "white"}`

	return (
		<div className="loading-wave h-full">
			<div className={styles}></div>
			<div className={styles}></div>
			<div className={styles}></div>
			<div className={styles}></div>
		</div>
	)
}

export default LoadingWave