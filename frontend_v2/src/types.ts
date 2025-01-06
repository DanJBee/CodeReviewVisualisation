import { SimulationLinkDatum, SimulationNodeDatum } from "d3";

export interface Node extends SimulationNodeDatum {
  authorId: string;
  avatarUrl: string;
  size: number;
  colourValue: number;
  count: number;
}

export interface Link extends SimulationLinkDatum<Node> {
  source: Node;
  target: Node;
  thickness: number;
  colourValue: number;
}

export interface Data {
  nodes: Node[];
  links: Link[];
  duration: number;
  data: Data;
}

export interface Project {
  id: number;
  owner: string;
  repository: string;
}
