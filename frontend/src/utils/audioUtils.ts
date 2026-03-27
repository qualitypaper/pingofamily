
// const audios: Map<string, PlayingAudio> = new Map();
export const playingAudio: { source: string, pause: VoidFunction | undefined } = {
    source: "",
    pause: undefined
};

const AUDIO = {
    async play(
        source: string,
        resolve: VoidFunction = () => { },
        reject: (reason?: any) => void = () => { }
    ) {
        if (!source) return;
        else if (playingAudio) {
            playingAudio.pause = undefined;
        }

        const audio = new Audio(source);

        audio.onended = () => {
            playingAudio.pause = undefined;
            resolve();
        };
        audio.onerror = (e) => reject(e);
        audio.onload = () => {
            audio.currentTime = 0;
        };

        playingAudio.source = audio.src;
        playingAudio.pause = () => audio.pause();
        await audio.play();
    },
    stop() {
        if (playingAudio.pause) {
            console.log("stopping audio", playingAudio.source);
            playingAudio.pause();
            playingAudio.pause = undefined;
        }

    }
}

export default AUDIO;