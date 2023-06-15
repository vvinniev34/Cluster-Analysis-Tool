import React, { useState, useEffect } from "react";
import Search from "./search";
import ClusterCount from "./clusterCount";
import DataPoints from "./dataPoints";
import Switch from "./switch";

function ViewClusters() {
  // const [query, setQuery] = useState("-1");
  const [kmclustCounts, setKMClustCounts] = useState([-1]);
  const [dataAssignments, setDataAssignments] = useState([-1]);
  const [viewDataOrCounts, setViewDataOrCounts] = useState(true);

  const handleSearch = (searchEntry) => {
    // setQuery(searchEntry);
    console.log(`http://localhost:8080/clusters/counts/${searchEntry}`);
    fetch(`http://localhost:8080/clusters/counts/${searchEntry}`)
      .then((response) => {
        console.log(response);
        return response.json();
      })
      .then((data) => {
        console.log(data);
        setKMClustCounts(data);
      })
      .catch((error) => {
        console.error("Error fetching data:", error);
      });

    console.log(`http://localhost:8080/clusters/datapoints/${searchEntry}`);
    fetch(`http://localhost:8080/clusters/datapoints/${searchEntry}`)
      .then((response) => {
        console.log(response);
        return response.json();
      })
      .then((data) => {
        console.log(data);
        setDataAssignments(data);
      })
      .catch((error) => {
        console.error("Error fetching data:", error);
      });
  };

  const SwitchView = (switchMode) => {
    console.log(switchMode);
    setViewDataOrCounts(switchMode);
  };

  return (
    <div>
      <Search handleSearch={handleSearch}></Search>
      <Switch switchView={SwitchView}></Switch>
      {viewDataOrCounts ? (
        <ClusterCount kmclustCounts={kmclustCounts}></ClusterCount>
      ) : (
        <DataPoints dataAssignments={dataAssignments}></DataPoints>
      )}
    </div>
  );
}

export default ViewClusters;
