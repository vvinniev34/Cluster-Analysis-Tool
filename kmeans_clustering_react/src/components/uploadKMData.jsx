import React, { useRef, useState } from "react";
const UploadKMData = () => {
  const [file, setFile] = useState(null);
  const [label, setLabel] = useState(0);

  const handleFileChange = (event) => {
    setFile(event.target.files[0]);
  };

  const handleLabelChange = (event) => {
    setLabel(event.target.value);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    const formData = new FormData();
    formData.append("file", file);
    fetch(`http://localhost:8080/uploadKMData/${label}`, {
      method: "POST",
      body: formData,
    })
      .then((response) => response.json())
      .then((data) => console.log(data))
      .catch((error) => console.error(error));
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label>Choose a file:</label>
        <input type="file" onChange={handleFileChange} />
      </div>
      <div>
        <label>Enter a label:</label>
        <input type="number" value={label} onChange={handleLabelChange} />
      </div>
      <button type="submit">Submit</button>
    </form>
  );
};
export default UploadKMData;
