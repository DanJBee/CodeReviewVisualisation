// Graph TypeScript file

import {
  drag,
  forceCenter,
  forceLink,
  forceManyBody,
  forceSimulation,
  SimulationLinkDatum,
  SimulationNodeDatum,
} from 'd3';
import axios from 'axios';

interface Node extends SimulationNodeDatum {
  id: string;
  group?: string;
}

interface Link extends SimulationLinkDatum<Node> {
  source: Node;
  target: Node;
  value: number;
}

interface Data {
  nodes: Node[];
  links: Link[];
  data: Data;
}

const graph = () => {
  // Defines height & width values
  const height = window.innerHeight;
  const width = window.innerWidth;

  return async (selection: any) => {
    // Collects a JSON response from the 'miserables.json' file
    const response: Data | undefined = await axios.get('/miserables.json');

    // If there is no response then return
    if (!response) return;

    // Stores the response data in a 'data' variable
    const { data } = response;

    // Backup code for setting the nodes to a solid colour if needed
    // const colour = scaleOrdinal(schemeCategory10);

    // Defines the links & nodes in the graph
    const links = data.links.map((d: Link) => ({ ...d }));
    const nodes = data.nodes.map((d: Node) => ({ ...d }));

    // Defines the svg element that the graph will be within
    const svg = selection
      .append('svg')
      .attr('height', height)
      .attr('width', width)
      .attr('viewBox', [50, 10, width / 2, height / 2])
      .attr('style', 'max-width: 100%; height: auto;');

    // Defines the link between each node in the graph
    const link = svg
      .append('g')
      .attr('stroke', '#999')
      .attr('stroke-opacity', 1.5) // this number controls the thickness of each link
      .selectAll()
      .data(links)
      .join('line')
      .attr('stroke-width', (d: Link) => Math.sqrt(d.value));

    // Defines each individual node in the graph
    const node = svg
      .append('g')
      .attr('stroke', '#fff')
      .attr('stroke-width', 1.5)
      .selectAll()
      .data(nodes)
      .join('circle')
      .attr('r', 15) // this number controls the size of each node
      // Backup code for when nodes are filled with a solid colour if needed
      // .attr('fill', (d) => colour(d.group))
      .attr('fill', 'url(#image)');

    // Sets the position attribute of the links & nodes in the graph each time the nodes 'ticks'
    function ticked() {
      link
        .attr('x1', (d: Link) => d.source.x)
        .attr('y1', (d: Link) => d.source.y)
        .attr('x2', (d: Link) => d.target.x)
        .attr('y2', (d: Link) => d.target.y);

      node.attr('cx', (d: Node) => d.x).attr('cy', (d: any) => d.y);
    }

    // Defines the simulation of the physics on the nodes in the graph
    const simulation = forceSimulation(nodes)
      .force(
        'link',
        forceLink(links).id((d: any) => d.id),
      )
      .force('charge', forceManyBody())
      .force('centre', forceCenter(width / 4 + 20, height / 4 + 45))
      .on('tick', ticked);

    // Reheats the simulation when the drag starts & fix the subject's i.e. the node's position
    function started(event: any) {
      const newEvent = event;
      if (!event.active) simulation.alphaTarget(0.3).restart();
      newEvent.subject.fx = event.x;
      newEvent.subject.fy = event.y;
    }

    // Updates the subject's i.e. the dragged node's position during the drag
    function dragged(event: any) {
      const newEvent = event;
      newEvent.subject.fx = event.x;
      newEvent.subject.fy = event.y;
    }

    // Restores the target alpha so the simulation cools after dragging ends
    // Unfixes the subject's i.e. the previously dragged node's position now that it is no longer
    // being dragged
    function ended(event: any) {
      const newEvent = event;
      if (!event.active) simulation.alphaTarget(0);
      newEvent.subject.fx = null;
      newEvent.subject.fy = null;
    }

    // Defines the element for the avatar URl pattern on the nodes in the graph
    const defs = svg.append('defs');

    // Defines the avatar URL pattern themselves on the nodes in the graph
    const pattern = defs
      .append('pattern')
      .attr('id', 'image')
      .attr('x', '0')
      .attr('y', '0')
      .attr('height', '1')
      .attr('width', '1');

    // Adds the avatar URL pattern to the nodes in the graph
    pattern
      .append('image')
      .attr('x', '0')
      .attr('y', '0')
      .attr('height', '30') // double the radius value
      .attr('width', '30') // double the radius value
      .attr(
        'xlink:href',
        'https://avatars.githubusercontent.com/u/22572315?v=4', // this value controls the image on the nodes in the graph
      );

    // Appends a title to each node
    node.append('title').text((d: any) => d.id);

    // Applies the drag physics to each node in the graph
    node.call(drag().on('start', started).on('drag', dragged).on('end', ended));
  };
};

export default graph();
