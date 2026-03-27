function ErrorAuthorization({ children, open, setOpen }) {
  const closeError = () => setOpen(false);

  return (
    open && (
      <div
        aria-hidden={!open}
        className="fixed top-0 left-0 w-full h-full z-50 flex justify-center items-center backdrop-blur-md bg-opacity-50"
      >
        <div className="bg-[#EBF0FB] w-[20rem] lg:w-[20rem] flex items-center justify-center p-4 rounded-lg shadow-xl">
          <div className="flex flex-col gap-8 p-2">
            <div className="flex flex-col gap-4">
              <h1 className="text-center text-3xl font-bold">Error</h1>
              <p className="font-semibold text-xl">{children}</p>
            </div>
            <button
              onClick={closeError}
              className="p-2 bg-[#3C72FF] w-full rounded-xl hover:bg-[#3C72FF]/90 transition-all duration-200"
            >
              <p className="text-white font-semibold text-xl">Close</p>
            </button>
          </div>
        </div>
      </div>
    )
  );
}

export function ErrorText({ styleClass, children }) {
  return <p className={`text-center error-text ${styleClass}`}>{children}</p>;
}

export default ErrorAuthorization;
