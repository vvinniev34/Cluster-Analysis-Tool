const ClusterCount = ({ kmclustCounts }) => {
  return (
    <ul>
      {kmclustCounts.map((clusterCount, index) => (
        <li key={index}>
          Cluster {index}: {clusterCount}
        </li>
      ))}
    </ul>
  );
};
export default ClusterCount;
