// Graph TypeScript file

import {
  drag,
  forceCenter,
  forceLink,
  forceManyBody,
  forceSimulation,
  json,
} from 'd3';

export const graph = () => {
  // Defines height & width values
  const height = window.innerHeight;
  const width = window.innerWidth;

  return async (selection) => {
    const data = await json('../miserables.json');

    // Backup code for setting the nodes to a solid colour if needed
    // const colour = scaleOrdinal(schemeCategory10);

    // Defines the links & nodes in the graph
    const links = data.links.map((d) => ({ ...d }));
    const nodes = data.nodes.map((d) => ({ ...d }));

    // Defines the individual links & nodes in the graph
    let link;
    let node;

    // Defines the simulation of the physics on the nodes in the graph
    const simulation = forceSimulation(nodes)
      .force(
        'link',
        forceLink(links).id((d) => d.id),
      )
      .force('charge', forceManyBody())
      .force('centre', forceCenter(width / 2, height / 2))
      .on('tick', ticked);

    // Defines the svg element that the graph will be within
    const svg = selection
      .append('svg')
      .attr('height', height)
      .attr('width', width)
      .attr('viewBox', [0, 0, width, height])
      .attr('style', 'max-width: 100%; height: auto;');

    // Defines the element for the avatar URl pattern on the nodes in the graph
    const definitions = svg.append('defs');

    // Defines the avatar URL pattern themselves on the nodes in the graph
    const pattern = definitions
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
        'https://avatars.githubusercontent.com/u/22572315?v=4',
      );

    // Defines the link between each node in the graph
    link = svg
      .append('g')
      .attr('stroke', '#999')
      .attr('stroke-opacity', 1.5) // this number controls the thickness of each link
      .selectAll()
      .data(links)
      .join('line')
      .attr('stroke-width', (d) => Math.sqrt(d.value));

    // Defines each individual node in the graph
    node = svg
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

    // Appends a title to each node
    nodes.append('title').text((d) => d.id);

    // Applies the drag physics to each node in the graph
    node.call(drag()).on('start', started).on('drag', dragged).on('end', ended);

    // Sets the position attribute of the links & nodes in the graph each time the simulations 'ticks'
    function ticked() {
      link
        .attr('x1', (d) => d.source.x)
        .attr('y1', (d) => d.source.y)
        .attr('x2', (d) => d.target.x)
        .attr('y2', (d) => d.target.y);

      node.attr('cx', (d) => d.x).attr('cy', (d) => d.y);
    }

    // Reheats the simulation when the drag starts & fix the subject's i.e. the node's position
    function started(event) {
      if (!event.active) simulation.alphaTarget(0.3).restart();
      event.subject.fx = event.x;
      event.subject.fy = event.y;
    }

    // Updates the subject's i.e. the dragged node's position during the drag
    function dragged(event) {
      event.subject.fx = event.x;
      event.subject.fy = event.y;
    }

    // Restores the target alpha so the simulation cools after dragging ends
    // Unfixes the subject's i.e. the previously dragged node's position now that it is no longer being dragged
    function ended(event) {
      if (!event.active) simulation.alphaTarget(0);
      event.subject.fx = null;
      event.subject.fy = null;
    }

    // When the simulation is re-run, stop the previous simulation
    invalidation.then(() => simulation.stop());

    // Returns the svg node
    return svg.node();
  };
};
