// Graph TypeScript file

import {
  drag,
  forceCenter,
  forceLink,
  forceManyBody,
  forceSimulation,
} from 'd3';
import axios from 'axios';
import { Data, Link, Node } from './Types';

const graph = () => {
  // Defines height & width values
  const height = window.innerHeight;
  const width = window.innerWidth;

  return async (selection: any) => {
    // Collects a JSON response from the 'mock.json' file
    const response: Data | undefined = await axios.get(
      'http://localhost:8080/getGraph?owner=microsoft&project=typescript',
    );

    // If there is no response then return
    if (!response) return;

    // Stores the response data in a 'data' variable
    const { data } = response;

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
      .attr('stroke-width', (d: Link) => Math.sqrt(d.thickness));

    // Defines each individual node in the graph
    const node = svg
      .append('g')
      .attr('stroke', '#fff')
      .attr('stroke-width', 1.5)
      .selectAll()
      .data(nodes)
      .join('circle')
      .attr('r', (d: Node) => d.size) // this number controls the size of each node
      // sets an individual ID for each node to ensure the images' height/width are the correct
      // dimensions
      .attr('fill', (d: Node) => `url(#image-${d.id})`);

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
    function startDrag(event: any) {
      const newEvent = event;
      if (!event.active) simulation.alphaTarget(0.3).restart();
      newEvent.subject.fx = event.x;
      newEvent.subject.fy = event.y;
    }

    // Updates the subject's i.e. the dragged node's position during the drag
    function dragging(event: any) {
      const newEvent = event;
      newEvent.subject.fx = event.x;
      newEvent.subject.fy = event.y;
    }

    // Restores the target alpha so the simulation cools after dragging ends
    // Unfixes the subject's i.e. the previously dragged node's position now that it is no longer
    // being dragged
    function endDrag(event: any) {
      const newEvent = event;
      if (!event.active) simulation.alphaTarget(0);
      newEvent.subject.fx = null;
      newEvent.subject.fy = null;
    }

    // Defines the element for the avatar URL pattern on the nodes in the graph
    const defs = svg.append('defs');

    // Defines the avatar URL pattern themselves on the nodes in the graph
    const pattern = defs
      .selectAll('pattern')
      .data(nodes)
      .enter()
      .append('pattern')
      .attr('id', (d: Node) => `image-${d.id}`)
      .attr('x', '0')
      .attr('y', '0')
      .attr('height', '1')
      .attr('width', '1');

    // Adds the avatar URL pattern to the nodes in the graph
    pattern
      .append('image')
      .attr('x', '0')
      .attr('y', '0')
      .attr('height', (d: Node) => d.size * 2) // double the radius value
      .attr('width', (d: Node) => d.size * 2) // double the radius value
      .attr(
        'xlink:href',
        'https://avatars.githubusercontent.com/u/22572315?v=4', // this value controls the image on the nodes in the graph
      );

    // Appends a title to each node
    node.append('title').text((d: any) => d.id);

    // Applies the drag physics to each node in the graph
    node.call(
      drag().on('start', startDrag).on('drag', dragging).on('end', endDrag),
    );
  };
};

export default graph();
