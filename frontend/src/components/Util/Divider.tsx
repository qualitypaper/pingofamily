

type DividerProps = {
    text?: string;
}

const Divider = ({text}: DividerProps) => {

    return (
        <div className='divider-wrapper'>
            <span className="divider">
                {text}
            </span>
        </div>
    )
}

export default Divider;