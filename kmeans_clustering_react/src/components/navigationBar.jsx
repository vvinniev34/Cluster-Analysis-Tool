import React from "react";
import UploadKMData from "./uploadKMData";
import ViewClusters from "./viewClusters";
import CalculateClusters from "./calculateClusters";

const NavigationBar = () => {
  return (
    <div>
      <UploadKMData></UploadKMData>
      <ViewClusters></ViewClusters>
      <CalculateClusters></CalculateClusters>
    </div>
  );
};
export default NavigationBar;
