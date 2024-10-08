import { useParams } from "react-router-dom";
import { Typography } from "@mui/material";
import Controller from "./Controller";
import Visualisation from "./Visualisation";

function App() {
  const { owner, project } = useParams<{
    owner: string;
    project: string;
  }>();

  return (
    <>
      {owner && project && <Visualisation />}
      <Controller />
    </>
  );
}

export default App;
