import React from "react";
import axios from "axios";
import {
  Container,
  Typography,
  List,
  ListItem,
  ListItemButton,
  ListItemText,
} from "@mui/material";

interface Project {
  id: number;
  owner: string;
  repository: string;
}

export default function Projects() {
  const [projects, setProjects] = React.useState<Project[]>();

  React.useEffect(() => {
    axios.get("http://localhost:8080/projects").then((response) => {
      console.log(response.data);
      setProjects(response.data);
    });
  }, []);

  return (
    <Container>
      <Typography variant="h3">Project List</Typography>
      {projects && (
        <List>
          {projects.map((p: Project) => {
            return (
              <ListItem>
                <ListItemButton href={`/${p.owner}/${p.repository}`}>
                  <ListItemText primaryTypographyProps={{ variant: "h4" }}>
                    {p.owner}/{p.repository}
                  </ListItemText>
                </ListItemButton>
              </ListItem>
            );
          })}
        </List>
      )}
    </Container>
  );
}
