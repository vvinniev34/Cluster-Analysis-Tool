function Switch({ switchView }) {
  return (
    <div>
      <button onClick={() => switchView(true)}>Cluster Counts</button>
      <button onClick={() => switchView(false)}>Data Point Assignments</button>
    </div>
  );
}
export default Switch;
