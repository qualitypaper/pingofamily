import { ReactComponent as VolumeUpIcon } from "assets/icons/volume-up.svg";
import { playSound } from "../../utils/trainingUtils";

const PlaySoundButton = ({ soundUrl }: { soundUrl: string }) => {
  
  const handlePlaySound = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    e.stopPropagation();
    playSound(soundUrl);
  }

  return (
    <button
      className="m-1 hover:cursor-pointer text-color-big-text  sm:text-sm dark:text-gray-300 font-semibold hover:text-button-color  hover:scale-110 transition-all duration-300"
      onClick={handlePlaySound}
    >
      <VolumeUpIcon />
    </button>
  );
};

export default PlaySoundButton;
