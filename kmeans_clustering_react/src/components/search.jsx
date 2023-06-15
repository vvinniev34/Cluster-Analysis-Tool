import React, { useState } from "react";

const Search = ({ handleSearch }) => {
  const [inputValue, SetInputValue] = useState("");
  const searchHandler = () => {
    SetInputValue("");
    handleSearch(inputValue);
  };

  function handleInputChange(event) {
    if (event.key === "Enter") {
      handleSearch(inputValue);
      SetInputValue("");
    }
  }

  return (
    <div>
      <input
        type="text"
        placeholder="Search For Cluster By Label..."
        value={inputValue}
        onChange={(e) => SetInputValue(e.target.value)}
        onKeyUp={handleInputChange}
      />
      <button onClick={searchHandler}>Search</button>
    </div>
  );
};

export default Search;
