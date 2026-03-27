import { ReactComponent as PlusIcon } from "assets/icons/plus.svg";
import { useState } from "react";
import CreateVocabularyGroupPopup from "../../features/vocabulary/Popups/CreateVocabularyGroupPopup";

function AddButton() {
  const [isFormVisible, setFormVisible] = useState(false);

  const showForm = () => {
    setFormVisible(true);
  };

  const hideForm = () => {
    setFormVisible(false);
  };

  return (
    <div className="relative rounded-lg border border-slate-300 btn-ghost hover:border-slate-300 hover:bg-[#E0F3FF] ease-out duration-300">
      <div
        className="rounded flex items-center justify-center cursor-pointer ease-out duration-300"
        onClick={showForm}
      >
        <span className="px-5 py-5 lg:px-8 lg:py-8 md:px-7 md:py-7">
          <PlusIcon fontSize="large" />
        </span>
      </div>
      {isFormVisible && (
        <CreateVocabularyGroupPopup onClose={hideForm} />
      )}
    </div>
  );
}

export default AddButton;
