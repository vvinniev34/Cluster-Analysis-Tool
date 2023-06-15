import { useState } from "react";
const DataPoints = ({ dataAssignments }) => {
  const [sort, SetSort] = useState(true);
  const sortedDataAssignments = Array.from(dataAssignments, (value, index) => [
    index,
    value,
  ]).sort((a, b) => a[1] - b[1]);
  return (
    <div>
      <button onClick={() => SetSort(!sort)}>Sort/Unsort</button>
      {!sort ? (
        <ul>
          {dataAssignments.map((data, index) => (
            <li key={index}>
              Data {index} assigned to cluster: {data}
            </li>
          ))}
        </ul>
      ) : (
        <div>
          <ul>
            {sortedDataAssignments.map((data) => (
              <li key={data[0]}>
                Data {data[0]} assigned to cluster: {data[1]}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};
export default DataPoints;
