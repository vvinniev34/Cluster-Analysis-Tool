import React, { useState } from "react";
function CalculateClusters() {
  const [label, setLabel] = useState("0");
  const [nclust, setNClust] = useState("0");
  const [maxIterations, SetMaxIterations] = useState("-1");
  const handleButtonClick = () => {
    console.log(
      `http://localhost:8080/clusters/calculate_counts/${label}-${nclust}-${maxIterations}`
    );
    const fetchData = async () => {
      fetch(
        `http://localhost:8080/clusters/calculate_counts/${label}-${nclust}-${maxIterations}`
      )
        .then((response) => response.json())
        .then((data) => {
          // RECEIVE CLUST->COUNTS
          //   setKMClust(data);
        });
    };

    fetchData();
  };

  return (
    <div>
      <input
        placeholder="label..."
        onChange={(e) => setLabel(e.target.value)}
      ></input>
      <input
        placeholder="nclust..."
        onChange={(e) => setNClust(e.target.value)}
      ></input>
      <input
        placeholder="max iterations..."
        onChange={(e) => SetMaxIterations(e.target.value)}
      ></input>
      <button onClick={handleButtonClick}>Calculate</button>
    </div>
  );
}

export default CalculateClusters;
