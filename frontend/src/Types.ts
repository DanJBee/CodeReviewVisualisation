import { SimulationLinkDatum, SimulationNodeDatum } from 'd3';

export interface Node extends SimulationNodeDatum {
  id: string;
  group?: string;
}

export interface Link extends SimulationLinkDatum<Node> {
  source: Node;
  target: Node;
  value: number;
}

export interface Data {
  nodes: Node[];
  links: Link[];
  data: Data;
}
